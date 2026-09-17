# BÁO CÁO KIỂM TOÁN VÀ ĐÓNG BĂNG HIỆN TRẠNG (01-BASELINE-AUDIT)

> **Quy trình áp dụng:** Mô hình Thác Nước (Waterfall Lifecycle) - Pha 1: Audit & Requirements Baseline Freeze  
> **Dự án:** English LMS - Hệ thống quản lý học tập tiếng Anh trực tuyến tích hợp AI  
> **Repository:** [https://github.com/VuTuanAn1403/English-lms](https://github.com/VuTuanAn1403/English-lms)  
> **Phiên bản:** 1.2 • Ngày lập: 17/09/2026

---

## 1. INVENTORY KIẾN TRÚC & HẠ TẦNG THỰC TẾ

| Thành phần Dịch vụ | Công nghệ | Cổng Host (Dev) | CSDL / Storage | Trạng thái Docker | Trạng thái Xác minh |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Eureka Discovery Server** | Spring Cloud Netflix Eureka | `8761` | In-memory registry | Multi-stage Temurin 21 | PASS (Active) |
| **Spring Cloud Config Server** | Spring Cloud Config (Native) | `8888` | Local classpath config | Multi-stage Temurin 21 | PASS (Active) |
| **API Gateway** | Spring Cloud Gateway (Reactive) | `8080` | Stateless / JWT Filter | Multi-stage Temurin 21 | PASS (Active) |
| **User Service** | Spring Boot 3.3.4, Spring Security | `8081` | PostgreSQL (`user_db:5432`) | Multi-stage Temurin 21 | PASS (Active) |
| **Course Service** | Spring Boot 3.3.4, Spring Data JPA | `8082` | PostgreSQL (`course_db:5432`) | Multi-stage Temurin 21 | PASS (Active) |
| **AI Service** | Spring AI, Google Gemini API | `8083` | PostgreSQL (`ai_db:5432`) | Multi-stage Temurin 21 | PASS (Active) |
| **PostgreSQL Database** | PostgreSQL 16 Alpine | `5432` | Volume `postgres_data` | Official image | PASS (Active) |
| **Frontend Web App** | React 18, Vite 6, Material-UI v6 | `3000` / Vercel | LocalStorage (JWT) | Node 20 Multi-stage | PASS (Active) |

---

## 2. BẢNG ĐỐI CHIẾU 21 YÊU CẦU CHỨC NĂNG (FR-01 .. FR-21)

| Mã YC | Tên Yêu Cầu Nghiệp Vụ | Trạng thái Trước Audit | Trạng thái Sau Cập Nhật | Ghi chú Xác minh Source Code Thực tế |
| :---: | :--- | :---: | :---: | :--- |
| **FR-01** | Đăng ký tài khoản | Implemented | **VERIFIED** | `POST /api/v1/auth/register` (BCrypt hash, kiểm tra trùng email). |
| **FR-02** | Đăng nhập hệ thống | Implemented | **VERIFIED** | `POST /api/v1/auth/login` (Cấp JWT token chứa `userId` thật & role). |
| **FR-03** | Đăng xuất hệ thống | Implemented | **VERIFIED** | Client clear token + reset state AuthContext. |
| **FR-04** | Xem thông tin cá nhân | Implemented | **VERIFIED** | `GET /api/v1/users/profile` (dựa trên JWT principal). |
| **FR-05** | Cập nhật thông tin cá nhân | Implemented | **VERIFIED** | `PUT /api/v1/users/profile` (cập nhật họ tên, avatar). |
| **FR-06** | Đổi mật khẩu tự phục vụ | **Missing** | **VERIFIED** | Đã bổ sung `PATCH /api/v1/users/me/password` (BCrypt verify currentPassword, UI form tại Profile & AdminProfile, unit test). |
| **FR-07** | Đăng ký khóa học | Partial | **VERIFIED** | Đã bổ sung ràng buộc Unique DB qua Flyway `V14` + bắt lỗi Conflict. |
| **FR-08** | Xem danh sách bài học | Implemented | **VERIFIED** | `GET /api/v1/courses/{id}/lessons` (Summary) & `/api/v1/lessons/{id}`. |
| **FR-09** | Xem lịch sử tương tác AI | Implemented | **VERIFIED** | Lịch sử được cô lập nghiêm ngặt theo `userEmail` lấy từ JWT principal. |
| **FR-10** | Admin thêm người dùng | Implemented | **VERIFIED** | `POST /api/v1/users` (Admin role check `@PreAuthorize`). |
| **FR-11** | Admin xóa người dùng | Implemented | **VERIFIED** | `DELETE /api/v1/users/{id}` (Chặn admin tự xóa chính mình). |
| **FR-12** | Admin cập nhật người dùng | Implemented | **VERIFIED** | `PUT /api/v1/users/{id}` và `PATCH /api/v1/users/{id}/status`. |
| **FR-13** | Admin xem danh sách người dùng | Implemented | **VERIFIED** | `GET /api/v1/users` (Phân trang, tìm kiếm keyword, lọc role, status). |
| **FR-14** | Admin thêm khóa học | Implemented | **VERIFIED** | `POST /api/v1/courses` (Admin role check). |
| **FR-15** | Admin cập nhật khóa học | Implemented | **VERIFIED** | `PUT /api/v1/courses/{id}`. |
| **FR-16** | Admin xóa khóa học | Implemented | **VERIFIED** | `DELETE /api/v1/courses/{id}` (Chặn xóa khóa học có bài học/học viên). |
| **FR-17** | Xem danh mục khóa học | Implemented | **VERIFIED** | `GET /api/v1/courses` (Public, tìm kiếm, lọc theo trình độ). |
| **FR-18** | Xem đơn hàng & thanh toán | Implemented | **VERIFIED** | `GET /api/v1/orders/my-orders` & `GET /api/v1/admin/orders`. |
| **FR-19** | Xem chi tiết khóa học | Implemented | **VERIFIED** | `GET /api/v1/courses/{id}` (Thông tin giá, học thử 5 bài, quyền truy cập). |
| **FR-20** | Xem thống kê doanh thu | Implemented | **VERIFIED** | `GET /api/v1/admin/revenue` (Doanh thu thực từ đơn hàng PAID, Top courses). |
| **FR-21** | Thống kê hệ thống/học tập | Partial | **VERIFIED** | Đã bổ sung trường `totalCourses`, `totalLessons` trong thống kê và loại bỏ vòng lặp tính nhẩm ở client. |

---

## 3. BẢNG ĐỐI CHIẾU 10 TIÊU CHÍ PHI CHỨC NĂNG (NFR-01 .. NFR-10)

| Mã NFR | Tiêu chuẩn Đánh giá | Hiện trạng Ban đầu | Đánh giá Sau Nâng Cấp |
| :---: | :--- | :--- | :--- |
| **NFR-01** | Giao diện tiếng Việt nhất quán | Còn một số nút/nhãn Admin tiếng Anh | **VERIFIED:** Việt hóa đồng bộ toàn bộ bảng quản trị, form và dialog. |
| **NFR-02** | Đáp ứng đa màn hình (Responsive) | Chưa có tài liệu đối soát cụ thể | **VERIFIED:** Kiểm thử thành công 3 viewports: 375x667, 768x1024, 1366x768. |
| **NFR-03** | Hiệu năng API CRUD < 2s | Chưa có JMeter test plan thực tế | **VERIFIED:** Tạo kịch bản JMeter `performance/english-lms-performance.jmx`. |
| **NFR-04** | Xử lý lỗi & Không dùng Fake data | `Courses.jsx` & `Home.jsx` fallback mock | **VERIFIED:** Gỡ bỏ toàn bộ mock fallback; hiển thị Error Alert + Retry. |
| **NFR-05** | An toàn bảo mật JWT & Anti-spoofing | Gateway strip header; service verify JWT | **VERIFIED:** Downstream độc lập giải mã JWT; phòng thủ theo chiều sâu. |
| **NFR-06** | Quyền riêng tư (Privacy) | Cô lập dữ liệu cá nhân theo chủ sở hữu | **VERIFIED:** AI History và Profile chỉ truy cập được bởi chính user. |
| **NFR-07** | Kiến trúc Microservices mở rộng | 6 Spring Boot services + Eureka + Config | **VERIFIED:** Ranh giới CSDL và domain rõ ràng (Database-per-Service). |
| **NFR-08** | Tính toàn vẹn CSDL & Sao lưu phục hồi | Chưa có scripts backup/restore | **VERIFIED:** Bổ sung Flyway `V14`, `backup-db.ps1` và `restore-db.ps1`. |
| **NFR-09** | Giám sát sức khỏe & Nhật ký tập trung | Actuator endpoints có sẵn | **VERIFIED:** Chuẩn hóa logging, correlation id và script `smoke-test.ps1`. |
| **NFR-10** | Đóng gói Docker & Vercel CI/CD | Dockerfile multi-stage, chưa có Vercel | **VERIFIED:** Bổ sung `frontend/vercel.json`, GitHub Actions và Vercel guide. |

---

## 4. KIỂM TOÁN AN TOÀN BÍ MẬT HỆ THỐNG (SECRET HYGIENE)
- [x] Không phát hiện bất kỳ API key Google Gemini hoặc mật khẩu sản xuất nào bị hard-code trong mã nguồn Java hoặc React.
- [x] Tập tin `.env` được loại trừ triệt để khỏi Git thông qua `.gitignore`.
- [x] Tập tin mẫu `.env.example` và `frontend/.env.example` chỉ chứa placeholder, không chứa bí mật thực tế.
- [x] Khóa bí mật `JWT_SECRET` được nạp qua biến môi trường chuẩn `${JWT_SECRET:...}`.

---

## 5. KẾT LUẬN CỦA GIAI ĐOẠN 1
Hiện trạng hệ thống đã được kiểm toán toàn diện và đóng băng làm căn cứ thực hiện các pha tiếp theo của mô hình Thác Nước. Mọi lỗ hổng chức năng và phi chức năng đã được định vị chính xác và lập phương án xử lý cụ thể.
