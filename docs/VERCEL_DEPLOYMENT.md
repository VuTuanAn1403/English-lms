# HƯỚNG DẪN TRIỂN KHAI FRONTEND LÊN VERCEL (VERCEL DEPLOYMENT GUIDE)
**Dự án:** English LMS – Hệ thống Quản lý Khóa học Tiếng Anh Trực tuyến Tích hợp AI  
**Thành phần triển khai:** Frontend React 18 + Vite 6 Single Page Application (SPA)  
**Phiên bản:** 1.2 • **Ngày cập nhật:** 17/09/2026  

---

## 1. MỤC TIÊU VÀ NGUYÊN TẮC THIẾT KẾ (OBJECTIVES & ARCHITECTURE)

### 1.1. Mục tiêu
Triển khai ứng dụng Frontend React/Vite lên nền tảng đám mây Vercel nhằm:
- Cung cấp giao diện người dùng qua giao thức HTTPS bảo mật trên toàn cầu qua mạng lưới CDN/Edge Network của Vercel.
- Kết nối ổn định, tin cậy tới hệ thống Backend Microservices thông qua API Gateway công khai.
- Tự động hóa quá trình triển khai Preview (khi tạo Pull Request trên GitHub) và Production (khi merge vào nhánh `main`).
- Bảo đảm an toàn tuyệt đối: Không lưu bất kỳ secret, API key hay database credential nào trong mã nguồn hoặc gói bundle của client.

### 1.2. Mô hình luồng dữ liệu
```
[ Trình duyệt Client ] 
       | 
       | (1) Tải tài sản tĩnh SPA (HTML, CSS, JS) qua HTTPS
       v 
[ Vercel Edge Network ] (Hosting thư mục dist/)
       | 
       | (2) Gửi REST API Request (Header: Authorization: Bearer <JWT>)
       v 
[ Public API Gateway ] (Domain: https://api.yourdomain.com:8080)
       | 
       +---> user-service (Xác thực, Quản lý tài khoản)
       +---> course-service (Khóa học, Ghi danh, Tiến độ)
       +---> ai-service (Gia sư AI, Lịch sử tương tác)
```

---

## 2. KIỂM TOÁN VÀ CHUẨN BỊ MÃ NGUỒN FRONTEND (AUDIT & PREPARATION)

### 2.1. Xác nhận thông số dự án
- **Thư mục gốc Frontend:** `frontend/` (Chứa `package.json`, `vite.config.js`, `index.html`).
- **Framework Preset:** `Vite`.
- **Trình quản lý gói:** `npm` (có tệp `package-lock.json` đồng bộ).
- **Lệnh cài đặt (Install Command):** `npm ci`.
- **Lệnh biên dịch (Build Command):** `npm run build`.
- **Thư mục đầu ra (Output Directory):** `dist`.

### 2.2. Kiểm toán đường dẫn API (Zero Localhost in Production)
- Ứng dụng sử dụng biến môi trường chuẩn Vite: `import.meta.env.VITE_API_BASE_URL`.
- Trong môi trường Production, biến này được nạp giá trị URL HTTPS công khai của API Gateway.
- Tuyệt đối không hardcode `localhost`, `127.0.0.1` hay các dải địa chỉ IP nội bộ trong mã nguồn frontend.

---

## 3. CẤU HÌNH VERCEL SPA REWRITE (`vercel.json`)

Để khắc phục hiện tượng người dùng gặp lỗi **HTTP 404 Not Found** khi truy cập trực tiếp vào các liên kết con (Deep-link như `/courses/1`, `/profile`, `/admin/users`) hoặc khi bấm F5 tải lại trang, tệp `frontend/vercel.json` đã được tạo sẵn tại thư mục gốc của frontend:

```json
{
  "$schema": "https://openapi.vercel.sh/vercel.json",
  "rewrites": [
    {
      "source": "/(.*)",
      "destination": "/index.html"
    }
  ]
}
```

> [!IMPORTANT]
> - Cấu hình này chỉ thị cho Vercel chuyển tiếp mọi yêu cầu định tuyến không trỏ vào tệp tĩnh thực tế về `index.html`, cho phép React Router ở phía client tiếp nhận và điều hướng chính xác.
> - Nếu sử dụng cơ chế reverse proxy cho API qua Vercel, các quy tắc `/api/:path*` phải được đặt **trước** quy tắc catch-all `/(.*)` để tránh làm hỏng yêu cầu API.

---

## 4. MA TRẬN BIẾN MÔI TRƯỜNG TRÊN VERCEL (ENVIRONMENT VARIABLES)

Cấu hình tại mục **Settings -> Environment Variables** của dự án trên Vercel:

| Tên biến | Môi trường Development | Môi trường Preview (PR) | Môi trường Production | Quy tắc bảo mật |
| :--- | :--- | :--- | :--- | :--- |
| `VITE_API_BASE_URL` | `http://localhost:8080` | `https://api-staging.yourdomain.com` | `https://api.yourdomain.com` | **Công khai**. Không bao giờ chứa mật khẩu/secret. |
| `VITE_APP_NAME` | `English LMS (Dev)` | `English LMS (Preview)` | `English LMS` | Tên hiển thị ứng dụng. |

> [!WARNING]
> Tuyệt đối **KHÔNG** đưa các biến `GEMINI_API_KEY`, `JWT_SECRET`, `POSTGRES_PASSWORD` vào Vercel Environment Variables. Các biến này chỉ thuộc về cấu hình máy chủ backend. Mọi biến có tiền tố `VITE_*` sẽ bị đóng gói công khai vào file JavaScript bundle tải xuống trình duyệt!

---

## 5. CÁC BƯỚC THIẾT LẬP VÀ TRIỂN KHAI (STEP-BY-STEP SETUP)

### Bước 1: Kết nối GitHub Repository với Vercel
1. Truy cập [Vercel Dashboard](https://vercel.com/dashboard) -> Chọn **Add New Project**.
2. Chọn tài khoản GitHub `VuTuanAn1403` và chọn repository `English-lms`.

### Bước 2: Cấu hình Build & Root Directory
1. Tại phần **Configure Project**:
   - **Project Name:** `english-lms-frontend`
   - **Framework Preset:** Chọn `Vite`
   - **Root Directory:** Nhấp **Edit** và chọn thư mục `frontend` (Cực kỳ quan trọng, không để trống).
2. Tại phần **Build and Output Settings**:
   - Build Command: `npm run build` (Mặc định)
   - Output Directory: `dist` (Mặc định)
   - Install Command: `npm ci` (Mặc định)

### Bước 3: Thiết lập Biến Môi trường
Mở mục **Environment Variables** và khai báo:
- Key: `VITE_API_BASE_URL` | Value: `https://api.yourdomain.com` (Chọn Production và Preview).
- Key: `VITE_APP_NAME` | Value: `English LMS`.

### Bước 4: Triển khai và Kiểm tra (Deploy & Verify)
1. Bấm **Deploy**.
2. Theo dõi tiến trình trong **Deployment Logs**. Đảm bảo bước `npm run build` hoàn thành với thông báo `✓ built in ...s`.
3. Nhấp vào liên kết tên miền được Vercel cấp phát (ví dụ: `https://english-lms-frontend.vercel.app`) để thực hiện kiểm tra khói (Smoke Test).

---

## 6. XỬ LÝ SỰ CỐ VÀ KHẮC PHỤC LỖI (TROUBLESHOOTING MATRIX)

| Hiện tượng | Nguyên nhân có thể | Cách kiểm tra | Biện pháp xử lý |
| :--- | :--- | :--- | :--- |
| **Build thất bại trên Vercel** | Sai Root Directory hoặc thiếu file phụ thuộc | Xem Vercel Build Logs | Đảm bảo Root Directory là `frontend`, chạy thử `npm run build` cục bộ để đối chiếu |
| **Truy cập deep-link báo 404** | Thiếu file cấu hình `vercel.json` | Tải lại trang `/courses/1` trên trình duyệt | Đảm bảo `frontend/vercel.json` có rewrite rule về `/index.html` và đã được commit |
| **Web mở được nhưng không tải được dữ liệu** | `VITE_API_BASE_URL` cấu hình sai hoặc trỏ `localhost` | Mở F12 -> Tab Network xem URL các request API | Cập nhật lại `VITE_API_BASE_URL` trên Vercel Settings và kích hoạt **Redeploy** |
| **Lỗi CORS khi gọi API Backend** | Backend API Gateway chưa cấp phép cho domain Vercel | Kiểm tra lỗi `Access-Control-Allow-Origin` trên Console | Thêm domain `https://*.vercel.app` vào cấu hình CORS của Spring Cloud Gateway |
| **Lỗi Mixed Content** | Frontend dùng HTTPS nhưng gọi API backend qua HTTP thường | Trình duyệt chặn request API tự động | Bắt buộc cấu hình chứng chỉ SSL/TLS (HTTPS) cho API Gateway trên máy chủ |
