# MA TRẬN TRUY VẾT YÊU CẦU & KIỂM THỬ (TRACEABILITY MATRIX)

> **Mô hình quy trình:** Thác Nước (Waterfall Lifecycle) - Pha 1 & 10: Traceability & QA Standards  
> **Dự án:** English LMS - Hệ thống quản lý học tập tiếng Anh trực tuyến  
> **Phiên bản:** 1.2 • Ngày: 17/09/2026

---

## 1. MA TRẬN TRUY VẾT YÊU CẦU CHỨC NĂNG (FR-01 .. FR-21)

| Mã YC | Mô tả Nghiệp vụ | Use Case / Analysis | Thành phần / Module | Tệp Mã Nguồn / API Endpoint | Ca Kiểm Thử (Test Case ID) | Trạng thái Nghiệm thu |
| :---: | :--- | :--- | :--- | :--- | :--- | :---: |
| **FR-01** | Đăng ký tài khoản | UC-01: Đăng ký | User Service | `AuthController.register()`, `UserServiceImpl.register()` | `TC-AUTH-01`, `TC-AUTH-02` | **PASS** |
| **FR-02** | Đăng nhập hệ thống | UC-02: Đăng nhập | User Service, Gateway | `AuthController.login()`, `JwtService.generateToken()` | `TC-AUTH-03`, `TC-AUTH-04` | **PASS** |
| **FR-03** | Đăng xuất | UC-03: Đăng xuất | Frontend Client | `AuthContext.logout()`, `Navbar.jsx` | `TC-AUTH-05` | **PASS** |
| **FR-04** | Xem thông tin cá nhân | UC-04: Xem Profile | User Service | `UserController.getProfile()`, `Profile.jsx` | `TC-USER-01` | **PASS** |
| **FR-05** | Cập nhật thông tin | UC-05: Sửa Profile | User Service | `UserController.updateProfile()`, `Profile.jsx` | `TC-USER-02` | **PASS** |
| **FR-06** | Đổi mật khẩu tự phục vụ | UC-06: Đổi Mật Khẩu | User Service, Frontend | `PATCH /api/v1/users/me/password`, `Profile.jsx`, `AdminProfile.jsx` | `TC-PWD-01`, `TC-PWD-02`, `TC-PWD-03` | **PASS** |
| **FR-07** | Đăng ký khóa học | UC-07: Ghi danh | Course Service | `CourseServiceImpl.enroll()`, `V14__add_unique_constraint_enrollments.sql` | `TC-ENROLL-01`, `TC-ENROLL-02` | **PASS** |
| **FR-08** | Xem danh sách bài học | UC-08: Xem DS bài học | Course Service | `GET /api/v1/courses/{id}/lessons`, `CourseDetail.jsx` | `TC-LESSON-01` | **PASS** |
| **FR-09** | Lịch sử tương tác AI | UC-09: Lịch sử AI | AI Service | `AiController.getHistory()`, `AiServiceImpl.getHistory()` | `TC-AI-01`, `TC-AI-PRIVACY-01` | **PASS** |
| **FR-10** | Admin thêm người dùng | UC-10: Thêm người dùng | User Service | `UserController.createUser()`, `AdminUsers.jsx` | `TC-ADM-USR-01` | **PASS** |
| **FR-11** | Admin xóa người dùng | UC-11: Xóa người dùng | User Service | `UserController.deleteUser()`, `AdminUsers.jsx` | `TC-ADM-USR-02` | **PASS** |
| **FR-12** | Admin sửa người dùng | UC-12: Sửa người dùng | User Service | `UserController.updateUser()`, `AdminUsers.jsx` | `TC-ADM-USR-03` | **PASS** |
| **FR-13** | Admin xem DS người dùng | UC-13: DS người dùng | User Service | `UserController.getUsers()`, `AdminUsers.jsx` | `TC-ADM-USR-04` | **PASS** |
| **FR-14** | Admin thêm khóa học | UC-14: Thêm khóa học | Course Service | `CourseController.createCourse()`, `AdminCourseForm.jsx` | `TC-ADM-CRS-01` | **PASS** |
| **FR-15** | Admin sửa khóa học | UC-15: Sửa khóa học | Course Service | `CourseController.updateCourse()`, `AdminCourseForm.jsx` | `TC-ADM-CRS-02` | **PASS** |
| **FR-16** | Admin xóa khóa học | UC-16: Xóa khóa học | Course Service | `CourseController.deleteCourse()` (409 if enrolled) | `TC-ADM-CRS-03` | **PASS** |
| **FR-17** | Xem danh mục khóa học | UC-17: Catalog | Course Service | `GET /api/v1/courses`, `Courses.jsx` | `TC-CRS-01`, `TC-CRS-02` | **PASS** |
| **FR-18** | Xem đơn hàng & thanh toán | UC-18: Đơn hàng | Course Service | `CourseOrderController.getMyOrders()`, `MyOrders.jsx` | `TC-ORDER-01` | **PASS** |
| **FR-19** | Chi tiết khóa học & Học thử | UC-19: Học thử | Course Service | `CourseController.getCourseById()`, `LessonView.jsx` | `TC-TRIAL-01`, `TC-TRIAL-02` | **PASS** |
| **FR-20** | Thống kê doanh thu | UC-20: Doanh thu | Course Service | `AdminRevenueController.getRevenueAnalytics()`, `AdminRevenue.jsx` | `TC-REV-01` | **PASS** |
| **FR-21** | Thống kê hệ thống/học tập | UC-21: Thống kê HT | Course & User Service | `GET /api/v1/admin/enrollments/statistics`, `AdminDashboard.jsx` | `TC-STATS-01`, `TC-NPLUSONE-01` | **PASS** |

---

## 2. MA TRẬN TRUY VẾT YÊU CẦU PHI CHỨC NĂNG (NFR-01 .. NFR-10)

| Mã YC | Tiêu chí Chất lượng | Tiêu chuẩn ISO/IEC | Thành phần Hiện thực | Bằng chứng Kiểm chứng (Evidence) | Kết quả |
| :---: | :--- | :--- | :--- | :--- | :---: |
| **NFR-01** | Giao diện tiếng Việt nhất quán | ISO 9241-11 (Usability) | Frontend Material-UI Theme, Pages | Rà soát toàn bộ nhãn Admin, form tiếng Việt | **PASS** |
| **NFR-02** | Đáp ứng đa màn hình | ISO/IEC 25010 (Portability) | CSS Grid / MUI Responsive | `docs/RESPONSIVE_VERIFICATION.md` (3 viewports) | **PASS** |
| **NFR-03** | Hiệu năng API CRUD < 2s | ISO/IEC 25010 (Performance) | Microservices, HikariCP, DB Indexes | `performance/english-lms-performance.jmx` | **PASS** |
| **NFR-04** | Xử lý lỗi không fake success | ISO/IEC 25010 (Reliability) | `api.js`, `GlobalExceptionHandler.java` | Loại bỏ `sampleCourses` tĩnh trong `Courses.jsx` | **PASS** |
| **NFR-05** | An toàn bảo mật JWT & RBAC | ISO/IEC 25010 (Security) | API Gateway & Service JwtFilters | `TC-SEC-SPOOFING-01`, `UserSecurityTest.java` | **PASS** |
| **NFR-06** | Quyền riêng tư (Privacy) | ISO/IEC 27001 / GDPR | Filter scoped by principal | `AiServiceImplTest.java` (Session isolation) | **PASS** |
| **NFR-07** | Kiến trúc mở rộng Microservices | IEEE 1471 / ISO 42010 | Eureka, Config, Gateway, DB-per-service | `docs/02-architecture.md`, `docker-compose.yml` | **PASS** |
| **NFR-08** | Toàn vẹn dữ liệu & Phục hồi | ISO/IEC 25010 (Reliability) | Flyway `V1..V14`, Backup scripts | `scripts/backup-db.ps1`, `docs/backup-restore.md` | **PASS** |
| **NFR-09** | Giám sát sức khỏe & Nhật ký | ISO/IEC 25010 (Maintainability) | Spring Actuator Health, Logback | `scripts/smoke-test.ps1` | **PASS** |
| **NFR-10** | Đóng gói Docker & Vercel CI/CD | ISO/IEC 25010 (Portability) | Docker multi-stage, `frontend/vercel.json` | `.github/workflows/*`, `docs/VERCEL_DEPLOYMENT.md` | **PASS** |
