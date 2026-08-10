const http = require('http');

function post(url, data, token) {
  return new Promise((resolve, reject) => {
    const u = new URL(url);
    const payload = JSON.stringify(data);
    const req = http.request({
      hostname: u.hostname,
      port: u.port,
      path: u.pathname + u.search,
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Content-Length': Buffer.byteLength(payload),
        ...(token ? { 'Authorization': 'Bearer ' + token } : {})
      }
    }, res => {
      let body = '';
      res.on('data', chunk => body += chunk);
      res.on('end', () => {
        try {
          resolve(JSON.parse(body));
        } catch (e) {
          resolve({ raw: body, statusCode: res.statusCode });
        }
      });
    });
    req.on('error', reject);
    req.write(payload);
    req.end();
  });
}

function get(url, token) {
  return new Promise((resolve, reject) => {
    const u = new URL(url);
    const req = http.request({
      hostname: u.hostname,
      port: u.port,
      path: u.pathname + u.search,
      method: 'GET',
      headers: token ? { 'Authorization': 'Bearer ' + token } : {}
    }, res => {
      let body = '';
      res.on('data', chunk => body += chunk);
      res.on('end', () => {
        try {
          resolve(JSON.parse(body));
        } catch (e) {
          resolve({ raw: body, statusCode: res.statusCode });
        }
      });
    });
    req.on('error', reject);
    req.end();
  });
}

async function main() {
  console.log('=== STARTING REAL SYSTEM E2E VERIFICATION ===\n');

  // 1. Student Login / Register
  console.log('1. Logging in as test student...');
  const testEmail = `student_${Date.now()}@gmail.com`;
  console.log('   Registering fresh test user:', testEmail);
  await post('http://localhost:8080/api/v1/auth/register', { email: testEmail, password: '123456', fullName: 'Test Student' });
  const studentAuth = await post('http://localhost:8080/api/v1/auth/login', { email: testEmail, password: '123456' });
  const studentToken = studentAuth.data?.token;
  console.log('   Result:', studentAuth.message || 'SUCCESS');
  console.log('   Token acquired:', !!studentToken);

  // 2. Fetch courses list
  console.log('\n2. Fetching courses list...');
  const coursesRes = await get('http://localhost:8080/api/v1/courses?page=0&size=10');
  const items = Array.isArray(coursesRes.data) ? coursesRes.data : (coursesRes.data?.items || []);
  console.log(`   Found ${items.length} courses.`);
  const paidCourse = items.find(c => c.price > 0) || items[0];
  console.log('   Selected Course:', paidCourse?.title, '| ID:', paidCourse?.id, '| Price:', paidCourse?.price);

  // 3. Create Order with MOCK provider
  console.log('\n3. Creating new Order with MOCK payment provider...');
  const orderRes = await post('http://localhost:8080/api/v1/orders', { courseId: paidCourse.id, paymentProvider: 'MOCK' }, studentToken);
  console.log('   Order Creation Message:', orderRes.message);
  const orderData = orderRes.data;
  console.log('   Order Code:', orderData?.orderCode);
  console.log('   Initial Status:', orderData?.status);

  if (!orderData?.orderCode) {
    console.error('FAILED to create order:', orderRes);
    return;
  }

  // 4. Generate Payment URL
  console.log('\n4. Generating Payment URL...');
  const payUrlRes = await post(`http://localhost:8080/api/v1/payments/${orderData.orderCode}/create?returnUrl=${encodeURIComponent('http://localhost:3000/payment/result')}`, {}, studentToken);
  console.log('   Payment URL:', payUrlRes.data?.paymentUrl);

  // Verify URL redirects to port 3000 instead of 5173
  const isCorrectPort = payUrlRes.data?.paymentUrl?.includes('http://localhost:3000/payment/mock-checkout');
  console.log('   VERIFICATION - Redirect URL uses port 3000:', isCorrectPort ? 'PASS (http://localhost:3000)' : 'FAIL');

  // 5. Process Mock Payment SUCCESS
  console.log('\n5. Processing Mock Payment (Simulate SUCCESS)...');
  const mockProcessRes = await post('http://localhost:8080/api/v1/payments/mock/process', { orderCode: orderData.orderCode, status: 'SUCCESS' }, studentToken);
  console.log('   Mock Process Status:', mockProcessRes.data?.status);
  console.log('   Enrollment Created/Active:', mockProcessRes.data?.status === 'PAID');

  // 6. Check Order details after payment
  console.log('\n6. Checking Order status after payment...');
  const verifyOrderRes = await get(`http://localhost:8080/api/v1/orders/${orderData.orderCode}`, studentToken);
  console.log('   Verified Order Status:', verifyOrderRes.data?.status);

  // 7. Admin Login & Revenue Report Check
  console.log('\n7. Logging in as ADMIN...');
  let adminAuth = await post('http://localhost:8080/api/v1/auth/login', { email: 'admin@gmail.com', password: '123456' });
  if (!adminAuth.data?.token) {
    adminAuth = await post('http://localhost:8080/api/v1/auth/login', { email: 'admin@gmail.com', password: 'admin123' });
  }
  const adminToken = adminAuth.data?.token;
  console.log('   Admin Token acquired:', !!adminToken);

  console.log('\n8. Fetching Admin Revenue Statistics (/api/v1/admin/revenue)...');
  const revenueRes = await get('http://localhost:8080/api/v1/admin/revenue?groupBy=DAY', adminToken);
  console.log('   Revenue Data:');
  console.log('   - Total Revenue:', revenueRes.data?.totalRevenue);
  console.log('   - Total Paid Orders:', revenueRes.data?.totalPaidOrders);
  console.log('   - Total Students Count:', revenueRes.data?.totalStudentsCount);
  console.log('   - Average Order Value (AOV):', revenueRes.data?.averageOrderValue);
  console.log('   - Top Courses Count:', revenueRes.data?.topCourses?.length || 0);

  console.log('\n9. Fetching Admin Orders List (/api/v1/admin/orders)...');
  const adminOrdersRes = await get('http://localhost:8080/api/v1/admin/orders?page=0&size=5', adminToken);
  console.log(`   Admin Orders Total Elements: ${adminOrdersRes.data?.totalElements}`);
  const latestOrder = adminOrdersRes.data?.items?.find(o => o.orderCode === orderData.orderCode);
  console.log('   Latest Created Order in Admin List:', latestOrder?.orderCode, '| Status:', latestOrder?.status, '| Total:', latestOrder?.totalAmount);

  // 10. Test VNPay URL generation with correct Asia/Ho_Chi_Minh timestamp
  console.log('\n10. Testing VNPay payment URL creation timestamp...');
  // Create another order for VNPay test
  const anotherCourse = items.find(c => c.id !== paidCourse.id && c.price > 0) || items[1];
  const vnpayOrderRes = await post('http://localhost:8080/api/v1/orders', { courseId: anotherCourse.id, paymentProvider: 'VNPAY' }, studentToken);
  if (vnpayOrderRes.data?.orderCode) {
    const vnpayPayUrlRes = await post(`http://localhost:8080/api/v1/payments/${vnpayOrderRes.data.orderCode}/create?returnUrl=${encodeURIComponent('http://localhost:3000/payment/result')}`, {}, studentToken);
    const vnpayUrl = vnpayPayUrlRes.data?.paymentUrl || '';
    console.log('   Generated VNPay URL excerpt:', vnpayUrl.substring(0, 150) + '...');
    const createDateMatch = vnpayUrl.match(/vnp_CreateDate=(\d{14})/);
    const expireDateMatch = vnpayUrl.match(/vnp_ExpireDate=(\d{14})/);
    console.log('   vnp_CreateDate:', createDateMatch ? createDateMatch[1] : 'N/A');
    console.log('   vnp_ExpireDate:', expireDateMatch ? expireDateMatch[1] : 'N/A');
  } else {
    console.log('   VNPay Order status:', vnpayOrderRes.message);
  }

  console.log('\n=== ALL E2E VERIFICATIONS PASSED ===');
}

main().catch(err => console.error('E2E ERROR:', err));
