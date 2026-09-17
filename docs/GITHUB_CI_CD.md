# ĐẶC TẢ QUY TRÌNH TỰ ĐỘNG HÓA TÍCH HỢP VÀ PHÁT HÀNH (GITHUB CI/CD SPECIFICATION)
**Dự án:** English LMS – Hệ thống Quản lý Khóa học Tiếng Anh Trực tuyến Tích hợp AI  
**Nền tảng tự động hóa:** GitHub Actions & Vercel Git Integration  
**Phiên bản:** 1.2 • **Ngày cập nhật:** 17/09/2026  

---

## 1. TỔNG QUAN HỆ THỐNG CI/CD (PIPELINE ARCHITECTURE)

Hệ thống CI/CD được thiết lập nhằm bảo đảm chất lượng theo mô hình Thác nước (Waterfall Verification Gate), ngăn chặn mã nguồn lỗi lọt vào nhánh chính và tự động hóa phát hành giao diện:

```
      [ Developer Push / Pull Request ]
                      |
                      v
      +--------------------------------+
      |       GITHUB ACTIONS CI        |
      +--------------------------------+
             |        |        |
             v        v        v
        Frontend   Backend  Security
           CI        CI      Audit
             \        |        /
              v       v       v
           [ Tất cả Checks PASSED ]
                      |
       +--------------+--------------+
       |                             |
       v (Nếu là Pull Request)       v (Khi Merge vào main)
[ Vercel Preview Deployment ]   [ Vercel Production Deployment ]
(Kiểm thử giao diện trực tiếp)   (Phát hành chính thức ra công chúng)
```

---

## 2. CHI TIẾT CÁC WORKFLOWS GITHUB ACTIONS

### 2.1. Frontend CI (`.github/workflows/frontend-ci.yml`)
- **Bộ kích hoạt (Triggers):**
  - Mọi `pull_request` nhắm tới nhánh `main` hoặc `develop` có thay đổi trong thư mục `frontend/**`.
  - Mọi `push` lên các nhánh `main` hoặc `feature/**`.
- **Môi trường thực thi:** `ubuntu-latest`, Node.js 20.x.
- **Các bước thực thi tuần tự:**
  1. `actions/checkout@v4`: Tải mã nguồn.
  2. `actions/setup-node@v4`: Thiết lập môi trường Node.js kèm cache cho npm dependencies.
  3. `npm ci`: Cài đặt các gói phụ thuộc sạch sẽ theo `package-lock.json`.
  4. `npm run build`: Biên dịch mã nguồn sản xuất với Vite. Đảm bảo toàn bộ asset và bundle `dist/index.html` được tạo thành công không có lỗi cú pháp JSX.

### 2.2. Backend CI (`.github/workflows/backend-ci.yml`)
- **Bộ kích hoạt (Triggers):**
  - Mọi `pull_request` hoặc `push` có thay đổi trong thư mục `backend/**`.
- **Môi trường thực thi:** `ubuntu-latest`, Eclipse Temurin / Microsoft OpenJDK 21.
- **Các bước thực thi tuần tự:**
  1. Khởi tạo máy ảo Ubuntu và cài đặt JDK 21.
  2. Cấu hình Maven local repository cache nhằm tối ưu hóa thời gian chạy.
  3. Chạy ma trận kiểm thử (Matrix Test) hoặc chạy tuần tự qua các module microservices:
     - `mvn -B clean test -f backend/user-service/pom.xml`
     - `mvn -B clean test -f backend/course-service/pom.xml`
     - `mvn -B clean test -f backend/ai-service/pom.xml`
  4. Nếu bất kỳ ca kiểm thử đơn vị hoặc tích hợp nào thất bại, quy trình lập tức báo đỏ (Fail-Fast) và khóa nút Merge trên Pull Request.

### 2.3. Security & Secret Audit (`.github/workflows/security.yml`)
- **Bộ kích hoạt (Triggers):** Chạy trên mọi Pull Request và chạy định kỳ hàng tuần.
- **Nội dung kiểm toán:**
  1. **Secret Scan:** Quét toàn bộ repository để phát hiện các chuỗi khóa bí mật vô tình bị commit (Private keys, JWT base64 secrets, Gemini API keys dạng `AIzaSy...`).
  2. **Dependency Vulnerability Audit:**
     - Chạy `npm audit --audit-level=high` tại thư mục frontend.
     - Kiểm tra phụ thuộc Maven để cảnh báo các lỗ hổng CVE nghiêm trọng đã biết.

---

## 3. TÍCH HỢP TỰ ĐỘNG HÓA VERCEL (VERCEL GIT INTEGRATION)

### 3.1. Triển khai môi trường thử nghiệm (Preview Deployments)
- Khi một Pull Request được mở trên GitHub:
  - Vercel tự động nhận webhook, phân tích mã nguồn trong thư mục `frontend/`.
  - Thực thi lệnh `npm run build`.
  - Sinh ra một URL Preview độc lập (ví dụ: `https://english-lms-frontend-git-feature-xyz.vercel.app`).
  - Gửi bình luận tự động vào Pull Request trên GitHub kèm đường link xem trước, giúp Tester và Product Owner nghiệm thu giao diện trực tiếp trước khi duyệt mã.

### 3.2. Triển khai môi trường chính thức (Production Deployments)
- Khi Pull Request được chấp thuận và hợp nhất vào nhánh `main`:
  - Vercel tự động kích hoạt Production Deployment.
  - Cập nhật phiên bản mới nhất lên tên miền chính (Domain Production).
  - Áp dụng các biến môi trường Production đã cấu hình.

---

## 4. QUẢN LÝ KHÓA VÀ BẢO MẬT TRONG CI/CD (CI/CD SECURITY)

1. **Không in Secret ra Logs:** Mọi biến bảo mật được lưu trong **GitHub Repository Secrets** và được gán cờ che giấu tự động (Masking) trên bảng điều khiển Actions.
2. **Quyền hạn tối thiểu (Least Privilege):** Gán quyền hạn `permissions: contents: read` ở mức workflow; chỉ cấp thêm quyền ghi khi thực sự cần thiết.
3. **Chặn hợp nhất khi CI thất bại:** Thiết lập Branch Protection Rule trên GitHub để bắt buộc các checks sau phải có trạng thái **Passing** mới cho phép bấm Merge:
   - `frontend-ci`
   - `backend-ci`
   - `security`
