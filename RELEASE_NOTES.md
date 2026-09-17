# GHI CHÚ PHÁT HÀNH PHIÊN BẢN (RELEASE NOTES - VERSION 1.2.0)
**Dự án:** English LMS – Hệ thống Quản lý Khóa học Tiếng Anh Trực tuyến Tích hợp AI  
**Phiên bản:** `v1.2.0` • **Ngày phát hành:** 17/09/2026  
**Chủ sở hữu mã nguồn:** `@VuTuanAn1403` • **Kho lưu trữ:** `https://github.com/VuTuanAn1403/English-lms`  
**Mô hình phát triển:** Thác nước Tuyến tính (Strict Linear Waterfall Lifecycle)  

---

## 1. TỔNG QUAN BẢN PHÁT HÀNH (RELEASE OVERVIEW)

Bản phát hành **English LMS v1.2.0** là cột mốc nâng cấp toàn diện hệ thống từ kiến trúc, mã nguồn chức năng, cơ chế bảo mật an toàn thông tin, kiểm soát hiệu năng cơ sở dữ liệu đến quy trình tự động hóa CI/CD và triển khai thực tế trên Vercel. 

Toàn bộ quy trình quản lý và hồ sơ kỹ thuật đã được chuyển đổi hoàn toàn từ mô hình Agile/Sprint sang **Mô hình Thác nước Tuyến tính (Strict Linear Waterfall)** tuân thủ các tiêu chuẩn quốc tế ISO/IEC/IEEE 12207, IEEE 730, ISO/IEC 25010 và ISO/IEC/IEEE 29119.

---

## 2. CÁC ĐIỂM NÂNG CẤP VÀ TÍNH NĂNG MỚI (WHAT'S NEW)

### 2.1. Hoàn thiện Chức năng Nghiệp vụ Cốt lõi (Functional Enhancements)
- **FR-06 Đổi mật khẩu tự phục vụ (Self-service Password Change):**
  - Cài đặt endpoint an toàn `PATCH /api/v1/users/me/password` yêu cầu xác thực JWT Principal.
  - Kiểm tra mật khẩu hiện tại bằng thuật toán mã hóa BCrypt.
  - Chặn tái sử dụng mật khẩu cũ và áp dụng Bean Validation (`@NotBlank`, `@Size(min=6)`).
  - Tích hợp giao diện đổi mật khẩu trên trang Hồ sơ cá nhân (`Profile.jsx`) và Quản trị (`AdminProfile.jsx`), có cờ chống nhấn đúp (double-click submission prevention).
- **FR-21 Số liệu Thống kê Hệ thống Thực tế:**
  - Bổ sung `totalCourses` và `totalLessons` vào DTO phản hồi của `CourseService`.
  - Thay thế vòng lặp gọi 10 API trên frontend bằng một lệnh gọi API duy nhất đến `/api/v1/admin/enrollments/statistics`.

### 2.2. Tối ưu Hiệu năng và Toàn vẹn Cơ sở dữ liệu (Database & Performance)
- **Triệt tiêu vấn đề N+1 Query:**
  - Bổ sung các truy vấn tổng hợp theo nhóm (`GROUP BY`) trong `LessonRepository` và `LearningProgressRepository`.
  - Tái cấu trúc hàm `CourseServiceImpl.getAllEnrollmentsForAdmin`, giảm thời gian phản hồi API từ >1200ms xuống còn **215ms** dưới tải đồng thời.
- **Ràng buộc Duy nhất Cơ sở dữ liệu (Data Integrity):**
  - Tạo migration Flyway `V14__add_unique_constraint_enrollments.sql` thêm ràng buộc `UNIQUE(user_id, course_id)` trên bảng `enrollments`, ngăn ngừa triệt để lỗi ghi danh trùng lặp.

### 2.3. Tăng cường An toàn Thông tin và Phòng vệ AI (Security & AI Guardrails)
- **Chống Giả mạo Quyền hạn (Header Spoofing Elimination):**
  - Cấu hình Gateway loại bỏ toàn bộ các header `X-User-*` do người dùng tự gửi; dịch vụ downstream độc lập xác thực chữ ký JWT Bearer.
- **AI Prompt Injection Guardrails:**
  - Cài đặt chỉ thị hệ thống phòng thủ bất biến cho `ChatPromptStrategy`, `GrammarPromptStrategy`, `QuizPromptStrategy`, giữ vững vai trò gia sư LMS.
  - Giới hạn độ dài câu lệnh và kẹp số câu hỏi bài tập trắc nghiệm từ 5 đến 10 câu.
- **Vệ sinh Khóa bí mật (Secret Hygiene):**
  - Chuyển đổi toàn bộ khóa nhạy cảm sang biến môi trường `${JWT_SECRET:...}`; cấu hình Hibernate DDL về chế độ `validate`.

### 2.4. Trải nghiệm Người dùng và Triển khai Đám mây (Frontend, Vercel & CI/CD)
- **Loại bỏ Hoàn toàn Dữ liệu Mẫu Giả lập (Zero Mock Fallback):**
  - Xóa bỏ mảng `sampleCourses` tĩnh trên `Courses.jsx` và `Home.jsx`; hiển thị giao diện báo lỗi trực quan kèm nút "Thử lại" khi mất kết nối mạng.
- **Cấu hình Triển khai Vercel SPA (`vercel.json`):**
  - Thêm quy trình rewrite `/(.*) -> /index.html`, triệt tiêu lỗi HTTP 404 khi truy cập deep-link trực tiếp hoặc tải lại (F5) trang.
- **Tự động hóa CI/CD GitHub Actions:**
  - Cấu hình 3 workflows độc lập: `frontend-ci.yml`, `backend-ci.yml`, `security.yml` bảo vệ nhánh `main`.

---

## 3. THÔNG SỐ KIỂM CHỨNG KỸ THUẬT (VERIFICATION METRICS)

- **Backend Automated Tests:** 67/67 tests PASSED (100%).
  - `user-service`: 10/10 PASS.
  - `course-service`: 19/19 PASS.
  - `ai-service`: 38/38 PASS.
- **Frontend Production Build:** Vite 6.4.3 clean build PASS trong 9.73s.
- **JMeter Load Test (20 Threads):**
  - Catalog API: p95 = 320ms.
  - Course Detail API: p95 = 255ms.
  - Auth Login API: p95 = 560ms.
  - Admin Stats API: p95 = 365ms.
  - Tỷ lệ lỗi: 0.0%.
- **Responsive Compatibility:** Xác minh 100% đạt chuẩn trên 3 kích thước: Mobile (375x667), Tablet (768x1024), Desktop (1366x768).
- **Disaster Recovery:** Kịch bản `backup-db.ps1` và `restore-db.ps1` chạy thành công kèm xác minh sandbox.

---

## 4. HƯỚNG DẪN CẬP NHẬT VÀ NÂNG CẤP (UPGRADE INSTRUCTIONS)

1. **Kéo mã nguồn mới nhất:**
   ```bash
   git fetch origin
   git checkout main
   git pull origin main
   ```
2. **Cập nhật Backend:**
   ```bash
   docker compose down
   docker compose up --build -d
   ```
3. **Cập nhật Frontend trên Vercel:**
   - Đảm bảo nhánh `main` được liên kết với Vercel Project.
   - Kiểm tra biến môi trường `VITE_API_BASE_URL` trên Vercel Project Settings trỏ đúng URL HTTPS công khai của API Gateway.
   - Vercel tự động biên dịch và phát hành phiên bản mới nhất ra CDN toàn cầu.
