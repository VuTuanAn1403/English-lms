# ENGLISH LMS - HỆ THỐNG QUẢN LÝ HỌC TẬP TIẾNG ANH TÍCH HỢP AI ASSISTANT

> **Mô hình quy trình phát triển:** Thác nước Tuyến tính (Strict Linear Waterfall Lifecycle) tuân thủ ISO/IEC/IEEE 12207, IEEE 730, ISO/IEC 25010 và ISO/IEC/IEEE 29119.  

---

## 📌 1. TỔNG QUAN HỆ THỐNG VÀ ĐỐI TƯỢNG SỬ DỤNG

### 1.1. Mục tiêu hệ thống
English LMS là nền tảng quản lý đào tạo trực tuyến hiện đại với kiến trúc Microservices phân tán (Spring Boot 3 + React 18 / Vite 6), tích hợp Trợ lý Trí tuệ Nhân tạo (Google Gemini AI):
- **Dành cho Học viên (Student):** Tìm kiếm và đăng ký khóa học, học thử miễn phí 5 bài đầu tiên đối với các khóa học có phí, thanh toán qua cổng VNPay/Mock Sandbox, đánh dấu tiến độ hoàn thành, đổi mật khẩu tự phục vụ (FR-06), tương tác luyện tập đàm thoại, kiểm tra sửa lỗi ngữ pháp chi tiết và sinh đề trắc nghiệm thông minh cùng Trợ lý AI.
- **Dành cho Quản trị viên (Admin):** Quản lý người dùng và phân quyền RBAC, quản lý danh mục khóa học và bài học, theo dõi đơn hàng và báo cáo doanh thu, theo dõi thống kê hệ thống thực tế (FR-21).

### 1.2. Tính năng Trợ lý AI thực tế (AI Feature Scope)
- **Chat AI Tutor:** Trò chuyện hỏi đáp tiếng Anh trực tiếp, ngữ cảnh duy trì ổn định theo `sessionId`, bảo đảm cách ly dữ liệu cá nhân theo người dùng (Data Privacy).
- **Grammar Checker:** Phân tích câu/đoạn văn bản tiếng Anh, trả về cấu trúc JSON chuẩn gồm giải thích lỗi, thang điểm ngữ pháp và câu chuẩn hóa theo khung CEFR.
- **Quiz Generator:** Tự động sinh bộ câu hỏi trắc nghiệm (5 đến 10 câu) kèm 4 lựa chọn, đáp án đúng dạng số nguyên (`0..3`) và lời giải chi tiết.
- **Prompt Injection Guardrails:** Chỉ thị phòng vệ hệ thống bất biến, ngăn ngừa tiêm prompt làm sai lệch vai trò trợ lý gia sư LMS.

---

## 🛠️ 2. CÔNG NGHỆ VÀ KIẾN TRÚC HỆ THỐNG (TECH STACK & ARCHITECTURE)

### 2.1. Ngăn xếp Công nghệ (Tech Stack)
- **Backend:** Java 21 (OpenJDK / Temurin), Spring Boot `3.4.1`, Spring Cloud `2024.0.0`, Spring Security 6, JJWT `0.12.6`, Spring Data JPA, Flyway Migration.
- **AI Integration:** Spring AI `1.0.0-M4` (Google Gemini API).
- **Frontend:** React `18.3.1`, Vite `6.4.3`, Material UI (MUI) `6.1.10`, Axios `1.7.9`, React Router DOM `6.28.0`.
- **Cơ sở dữ liệu:** PostgreSQL 16 (Mô hình Database-per-Service: `user_db`, `course_db`, `ai_db`).
- **Triển khai Đám mây & Container:** Vercel (Frontend Hosting với SPA Routing), Docker Compose (Backend Microservices).
- **Tự động hóa CI/CD:** GitHub Actions (`frontend-ci.yml`, `backend-ci.yml`, `security.yml`).

### 2.2. Kiến trúc Triển khai Lai (Hybrid Deployment)
```
[ Trình duyệt Client ] 
       | 
       | HTTPS (CDN toàn cầu)
       v 
[ Vercel Edge Hosting ] (Frontend React/Vite SPA - vercel.json rewrite)
       | 
       | HTTPS REST API
       v 
[ Spring Cloud API Gateway (Port 8080) ]
       |-- Stripping Client X-User-* Headers
       |-- JWT Verification & Routing
       +---> [ User Service (8081) ]   <---> PostgreSQL (user_db)
       +---> [ Course Service (8082) ] <---> PostgreSQL (course_db)
       +---> [ AI Service (8083) ]     <---> PostgreSQL (ai_db)
```

---

## 🚀 3. HƯỚNG DẪN KHỞI CHẠY HỆ THỐNG (LOCAL RUN & DOCKER)

### 3.1. Khởi chạy Backend bằng Docker Compose
Hệ thống sử dụng Dockerfile multi-stage build, tự động biên dịch trực tiếp trong container từ mã nguồn sạch:
```bash
# 1. Sao chép cấu hình môi trường
cp .env.example .env

# 2. Điền Google Gemini API Key vào file .env (nếu cần dùng AI thật)
# GEMINI_API_KEY=your_actual_key_here

# 3. Khởi động toàn bộ stack dịch vụ
docker compose up --build -d

# 4. Kiểm tra sức khỏe toàn hệ thống
docker compose ps
```

### 3.2. Khởi chạy Frontend cục bộ
```bash
cd frontend
npm ci
npm run dev
# Truy cập giao diện tại http://localhost:5173
```

### 3.3. Kiểm tra khói tự động (Smoke Test)
```powershell
./scripts/smoke-test.ps1 -GatewayUrl "http://localhost:8080"
```

---

## 🌐 4. TRIỂN KHAI FRONTEND LÊN VERCEL (VERCEL DEPLOYMENT)

Ứng dụng Frontend sẵn sàng triển khai trên nền tảng đám mây Vercel:
1. **Root Directory:** `frontend`
2. **Framework Preset:** `Vite`
3. **Build Command:** `npm run build` | **Output Directory:** `dist`
4. **Biến môi trường trên Vercel:**
   - `VITE_API_BASE_URL`: Điền URL HTTPS công khai của API Gateway (Ví dụ: `https://api.yourdomain.com`).
   - `VITE_APP_NAME`: `English LMS`.
5. **Định tuyến SPA:** Tệp [frontend/vercel.json](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/frontend/vercel.json) đã được cấu hình chuyển hướng toàn bộ request về `/index.html`, ngăn ngừa lỗi HTTP 404 khi truy cập deep-link trực tiếp.
6. Xem chi tiết tại: [docs/VERCEL_DEPLOYMENT.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/VERCEL_DEPLOYMENT.md) và [docs/VERCEL_SMOKE_TEST.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/VERCEL_SMOKE_TEST.md).

---

## 🧪 5. KIỂM THỬ VÀ BẢO ĐẢM CHẤT LƯỢNG (TESTING & VERIFICATION)

### 5.1. Kiểm thử Tự động Backend (67 Test Cases - 100% PASS)
```bash
# Thực thi toàn bộ test suite trên cả 3 dịch vụ
mvn clean test

# Hoặc thực thi từng dịch vụ riêng lẻ
mvn test -pl backend/user-service
mvn test -pl backend/course-service
mvn test -pl backend/ai-service
```

### 5.2. Kiểm thử Đóng gói Frontend
```bash
cd frontend
npm run build
# Xác nhận bundle dist/index.html được tạo sạch sẽ trong 10s
```

### 5.3. Kiểm thử Tải Hiệu năng Apache JMeter
- Mở tệp kịch bản [performance/english-lms-performance.jmx](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/performance/english-lms-performance.jmx) bằng Apache JMeter 5.6.3.
- Thực thi kịch bản đo tải đồng thời 20 threads.
- Kết quả đo lường: Thời gian phản hồi trung bình API CRUD < 350ms, thời gian p95 < 560ms (Đạt chuẩn NFR-03: < 2.0 giây).

---

## 💾 6. SAO LƯU VÀ PHỤC HỒI DỮ LIỆU (BACKUP & RECOVERY)

Hệ thống cung cấp sẵn các kịch bản PowerShell tự động hóa sao lưu và khôi phục cơ sở dữ liệu:
- **Sao lưu toàn bộ 3 database (`user_db`, `course_db`, `ai_db`):**
  ```powershell
  ./scripts/backup-db.ps1
  ```
- **Khôi phục dữ liệu lên môi trường đích kèm kiểm tra toàn vẹn sandbox:**
  ```powershell
  ./scripts/restore-db.ps1 -BackupDir "./backups" -VerifySandbox
  ```
- Chi tiết hướng dẫn xem tại: [docs/backup-restore.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/backup-restore.md).

---
