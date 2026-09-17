# ĐẶC TẢ YÊU CẦU CƠ SỞ (REQUIREMENTS BASELINE)

> **Mô hình quy trình:** Thác Nước (Waterfall Lifecycle) - Pha 1 & 2: Yêu Cầu & Phân Tích  
> **Dự án:** English LMS - Hệ thống quản lý học tập tiếng Anh trực tuyến tích hợp AI Assistant  
> **Phiên bản:** 1.2 • Ngày hiệu lực: 17/09/2026

---

## 1. YÊU CẦU CHỨC NĂNG (FUNCTIONAL REQUIREMENTS - FR)

### FR-01: Đăng ký tài khoản (User Registration)
- **Tác nhân:** Khách vãng lai (Guest).
- **Mô tả:** Người dùng nhập Họ và tên, Email, Mật khẩu để tạo tài khoản Học viên mới.
- **Tiêu chí nghiệm thu (AC):**
  1. Kiểm tra định dạng email và tính duy nhất trong hệ thống; nếu trùng email trả về HTTP 409 Conflict.
  2. Mật khẩu phải có tối thiểu 6 ký tự và được mã hóa bằng thuật toán BCrypt trước khi ghi vào CSDL `user_db`.
  3. Mặc định gán vai trò `STUDENT` và trạng thái `ACTIVE`.

### FR-02: Đăng nhập hệ thống (Authentication & Token Issuance)
- **Tác nhân:** Người dùng (Guest / Student / Admin).
- **Mô tả:** Xác thực Email và Mật khẩu; trả về Access Token (JWT) và Refresh Token.
- **Tiêu chí nghiệm thu (AC):**
  1. Nếu thông tin không chính xác hoặc tài khoản bị khóa (`INACTIVE`), trả về HTTP 401 Unauthorized.
  2. Access Token chứa Claims: `sub` (email), `userId` (UUID thực trong CSDL), `role` (`STUDENT` hoặc `ADMIN`).
  3. Thời hạn hiệu lực của Token: 24 giờ.

### FR-03: Đăng xuất hệ thống (User Logout)
- **Tác nhân:** Người dùng đã xác thực.
- **Mô tả:** Chấm dứt phiên làm việc tại client, xóa bỏ token và thông tin phiên.
- **Tiêu chí nghiệm thu (AC):**
  1. Client xóa sạch `token` và `user` khỏi `localStorage`.
  2. Đồng bộ lại trạng thái `AuthContext` về null và chuyển hướng về trang chủ hoặc màn hình đăng nhập.

### FR-04: Xem thông tin cá nhân (View User Profile)
- **Tác nhân:** Người dùng đã xác thực (Student / Admin).
- **Mô tả:** Lấy thông tin chi tiết của tài khoản đang đăng nhập.
- **Tiêu chí nghiệm thu (AC):**
  1. Endpoint: `GET /api/v1/users/profile`.
  2. Xác định người dùng dựa trên JWT principal, không nhận email tùy ý từ query param để chống lộ thông tin.

### FR-05: Cập nhật thông tin cá nhân (Update User Profile)
- **Tác nhân:** Người dùng đã xác thực.
- **Mô tả:** Cập nhật họ tên và đường dẫn ảnh đại diện (avatar URL).
- **Tiêu chí nghiệm thu (AC):**
  1. Endpoint: `PUT /api/v1/users/profile`.
  2. Không cho phép thay đổi email và vai trò qua endpoint này. Cập nhật thành công lưu CSDL và trả về thông tin mới.

### FR-06: Đổi mật khẩu tự phục vụ (Self-service Password Change)
- **Tác nhân:** Người dùng đã xác thực (Student / Admin).
- **Mô tả:** Người dùng chủ động đổi mật khẩu tài khoản của chính mình.
- **Tiêu chí nghiệm thu (AC):**
  1. Endpoint: `PATCH /api/v1/users/me/password` (hoặc alias `/api/v1/users/profile/password`).
  2. Yêu cầu nhập: `currentPassword`, `newPassword` (tối thiểu 6 ký tự), `confirmPassword`.
  3. Xác thực mật khẩu cũ bằng `PasswordEncoder.matches()`; nếu sai trả về HTTP 400 với thông báo "Mật khẩu hiện tại không chính xác".
  4. Mật khẩu mới và xác nhận mật khẩu phải trùng nhau; mật khẩu mới không được trùng mật khẩu cũ.
  5. Mật khẩu mới được mã hóa BCrypt; không ghi mật khẩu vào log.
  6. Giao diện `Profile.jsx` và `AdminProfile.jsx` gọi API thật, hiển thị thông báo lỗi/thành công từ server.

### FR-07: Đăng ký khóa học (Course Enrollment & Concurrency Integrity)
- **Tác nhân:** Học viên đã đăng ký (`ROLE_STUDENT`).
- **Mô tả:** Đăng ký tham gia một khóa học miễn phí hoặc bắt đầu học thử khóa trả phí.
- **Tiêu chí nghiệm thu (AC):**
  1. Ràng buộc toàn vẹn CSDL: `UNIQUE(user_id, course_id)` ngăn chặn hoàn toàn đăng ký trùng lặp ở tầng DB.
  2. Nếu học viên đã đăng ký khóa học, trả về HTTP 409 Conflict hoặc bản ghi đăng ký hiện có.

### FR-08: Xem danh sách bài học (View Lessons Catalog)
- **Tác nhân:** Tất cả người dùng / Học viên.
- **Mô tả:** Truy vấn danh sách bài học trong một khóa học.
- **Tiêu chí nghiệm thu (AC):**
  1. Endpoint public `GET /api/v1/courses/{id}/lessons` trả về danh sách bài học tóm tắt (id, title, lessonOrder, duration, isTrial).
  2. Nội dung chi tiết bài học, nội dung văn bản và link video chỉ được trả về qua endpoint authenticated `GET /api/v1/lessons/{id}` khi người dùng có quyền hợp lệ.

### FR-09: Xem lịch sử tương tác AI (AI Interaction History & Privacy)
- **Tác nhân:** Học viên đã đăng ký (`ROLE_STUDENT`).
- **Mô tả:** Xem danh sách các câu hỏi, đoạn văn kiểm tra ngữ pháp hoặc bài quiz đã tương tác với AI.
- **Tiêu chí nghiệm thu (AC):**
  1. Endpoint: `GET /api/v1/ai/history`.
  2. Cô lập dữ liệu: Học viên chỉ nhìn thấy lịch sử của chính mình (dựa vào `principal.getName()`). Tuyệt đối không trả về dữ liệu của học viên khác.
  3. Hỗ trợ phân trang, lọc theo loại prompt (`CHAT`, `GRAMMAR`, `QUIZ`) và `sessionId`.

### FR-10: Admin thêm người dùng mới (Admin Create User)
- **Tác nhân:** Quản trị viên (`ROLE_ADMIN`).
- **Tiêu chí nghiệm thu (AC):** Endpoint `POST /api/v1/users` tạo tài khoản có kiểm tra trùng email và hash mật khẩu BCrypt.

### FR-11: Admin xóa người dùng (Admin Delete User)
- **Tác nhân:** Quản trị viên (`ROLE_ADMIN`).
- **Tiêu chí nghiệm thu (AC):** Endpoint `DELETE /api/v1/users/{id}` xóa người dùng; chặn Admin tự xóa chính tài khoản đang đăng nhập.

### FR-12: Admin cập nhật người dùng (Admin Update User)
- **Tác nhân:** Quản trị viên (`ROLE_ADMIN`).
- **Tiêu chí nghiệm thu (AC):** Endpoint `PUT /api/v1/users/{id}` cập nhật thông tin và `PATCH /api/v1/users/{id}/status` khóa/mở khóa tài khoản.

### FR-13: Admin xem danh sách người dùng (Admin View Users)
- **Tác nhân:** Quản trị viên (`ROLE_ADMIN`).
- **Tiêu chí nghiệm thu (AC):** Endpoint `GET /api/v1/users` phân trang, tìm kiếm keyword theo tên/email, lọc theo role và status.

### FR-14: Admin thêm khóa học mới (Admin Create Course)
- **Tác nhân:** Quản trị viên (`ROLE_ADMIN`).
- **Tiêu chí nghiệm thu (AC):** Endpoint `POST /api/v1/courses` tạo khóa học với giá tiền, trình độ và thông tin đầy đủ.

### FR-15: Admin cập nhật khóa học (Admin Update Course)
- **Tác nhân:** Quản trị viên (`ROLE_ADMIN`).
- **Tiêu chí nghiệm thu (AC):** Endpoint `PUT /api/v1/courses/{id}` cập nhật nội dung, giá bán và trạng thái xuất bản.

### FR-16: Admin xóa khóa học (Admin Delete Course)
- **Tác nhân:** Quản trị viên (`ROLE_ADMIN`).
- **Tiêu chí nghiệm thu (AC):** Endpoint `DELETE /api/v1/courses/{id}` kiểm tra tính toàn vẹn; từ chối xóa (HTTP 409 Conflict) nếu khóa học đã có bài học hoặc đã có học viên đăng ký.

### FR-17: Xem danh mục khóa học (Public Courses Catalog)
- **Tác nhân:** Mọi người dùng.
- **Tiêu chí nghiệm thu (AC):** Endpoint `GET /api/v1/courses` hỗ trợ tìm kiếm từ khóa và lọc trình độ (`BEGINNER`, `INTERMEDIATE`, `ADVANCED`).

### FR-18: Xem đơn hàng & Giao dịch thanh toán (View Orders)
- **Tác nhân:** Học viên / Admin.
- **Tiêu chí nghiệm thu (AC):**
  1. Học viên: `GET /api/v1/orders/my-orders` xem các đơn hàng của mình.
  2. Admin: `GET /api/v1/admin/orders` xem danh sách đơn hàng toàn hệ thống kèm trạng thái thanh toán.

### FR-19: Xem chi tiết khóa học & Học thử (Course Details & Free Trial)
- **Tác nhân:** Mọi người dùng / Học viên.
- **Tiêu chí nghiệm thu (AC):**
  1. Khóa học trả phí cho phép học thử 5 bài đầu miễn phí (Index 0..4).
  2. Truy cập bài thứ 6 trở đi khi chưa thanh toán sẽ nhận lỗi HTTP 403 `COURSE_PURCHASE_REQUIRED`.

### FR-20: Xem thống kê doanh thu (Admin Revenue Analytics)
- **Tác nhân:** Quản trị viên (`ROLE_ADMIN`).
- **Tiêu chí nghiệm thu (AC):** Endpoint `GET /api/v1/admin/revenue` tính toán tổng doanh thu chỉ từ các đơn hàng có trạng thái `PAID`, số lượng đơn thành công, giá trị trung bình đơn (AOV) và top khóa học mang lại doanh thu cao nhất.

### FR-21: Thống kê hệ thống & Học tập (System & Learning Statistics)
- **Tác nhân:** Quản trị viên (`ROLE_ADMIN`).
- **Mô tả:** Cung cấp bức tranh định lượng toàn diện về quy mô và tiến độ học tập trên toàn hệ thống.
- **Bộ 8 Chỉ Số Đo Lường Định Lượng (Concrete Quantitative Metrics):**
  1. **`totalUsers` (Tổng số người dùng):** Số lượng tài khoản thực tế lưu trong `user_db` (Học viên + Quản trị viên).
  2. **`totalCourses` (Tổng số khóa học):** Số lượng khóa học thực tế lưu trong `course_db`.
  3. **`totalLessons` (Tổng số bài học):** Số lượng bài học thực tế trên toàn hệ thống tính từ `lessons` table.
  4. **`totalEnrollments` (Tổng lượt ghi danh):** Tổng số bản ghi ghi danh (`ACTIVE`, `TRIAL`, `COMPLETED`, `IN_PROGRESS`).
  5. **`completedCoursesCount` (Số khóa học hoàn thành):** Số lượt đăng ký đã đạt 100% bài học hoàn thành.
  6. **`inProgressCoursesCount` (Số khóa học đang học):** Số lượt đăng ký đã hoàn thành > 0 bài nhưng < 100%.
  7. **`notStartedCoursesCount` (Số khóa học chưa học):** Số lượt đăng ký chưa hoàn thành bài học nào (0%).
  8. **`completionRate` (Tỷ lệ hoàn thành):** Tỷ lệ phần trăm tính theo công thức:
     $$\text{completionRate} = \frac{\text{completedCoursesCount}}{\text{totalEnrollments}} \times 100\%$$
- **Tiêu chí nghiệm thu (AC):**
  - Endpoint `GET /api/v1/admin/enrollments/statistics` trả về dữ liệu aggregation chính xác từ CSDL.
  - Frontend `AdminDashboard.jsx` hiển thị trực tiếp dữ liệu từ API, loại bỏ toàn bộ các vòng lặp tính nhẩm client-side.

---

## 2. YÊU CẦU PHI CHỨC NĂNG (NON-FUNCTIONAL REQUIREMENTS - NFR)

- **NFR-01 (Giao diện Tiếng Việt nhất quán):** Toàn bộ giao diện người dùng, thông báo hệ thống, nhãn form, bảng dữ liệu quản trị viên được chuẩn hóa bằng tiếng Việt chuẩn mực, bố cục rõ ràng, typography hiện đại.
- **NFR-02 (Đáp ứng đa màn hình - Responsive):** Giao diện thích ứng mượt mà và hoạt động đầy đủ tính năng trên 3 kích thước màn hình: Mobile (375x667), Tablet (768x1024) và Desktop (1366x768).
- **NFR-03 (Hiệu năng hệ thống - Performance):** Thời gian phản hồi trung bình của các API CRUD nghiệp vụ thông thường mục tiêu < 2.0 giây trong điều kiện tải đồng thời (có kịch bản JMeter kiểm chứng thực tế). Dịch vụ AI được đo đạc riêng biệt do phụ thuộc thời gian phản hồi của nhà cung cấp mô hình (Google Gemini).
- **NFR-04 (Xử lý lỗi - Error Handling):** Mã trạng thái HTTP chuẩn xác (400, 401, 403, 404, 409, 422, 500, 502). Không sử dụng dữ liệu tĩnh (mock/sampleCourses fallback) để che đậy lỗi mất kết nối backend.
- **NFR-05 (An toàn thông tin - Security):** Xác thực JWT Token chứa `userId` thực; mã hóa mật khẩu một chiều BCrypt; kiểm soát phân quyền dựa trên vai trò RBAC (`@PreAuthorize("hasRole('ADMIN')")`); Gateway loại bỏ các header mạo danh `X-User-*` trước khi chuyển tiếp; Downstream tự giải mã JWT để phòng thủ chiều sâu (Defense-in-depth); Giới hạn tần suất gọi API (Rate Limiting).
- **NFR-06 (Quyền riêng tư - Privacy):** Người dùng chỉ được truy cập dữ liệu thuộc quyền sở hữu của mình (thông tin hồ sơ, đơn hàng, tiến độ học tập và lịch sử tương tác AI).
- **NFR-07 (Khả năng mở rộng - Extensibility):** Kiến trúc phân tán microservices với ranh giới nghiệp vụ độc lập, áp dụng mô hình Database-per-Service, đăng ký động qua Eureka Discovery và cấu hình tập trung qua Config Server.
- **NFR-08 (Toàn vẹn dữ liệu & Khôi phục - Data Integrity & Recovery):** CSDL quản lý versioning qua Flyway Migrations (nguyên tắc bất biến `V1..V14`); có kịch bản sao lưu (`backup-db.ps1`) và phục hồi (`restore-db.ps1`) tự động kèm quy trình kiểm tra sandbox.
- **NFR-09 (Giám sát & Nhật ký - Monitoring & Logging):** Cung cấp endpoint Actuator Health cho từng microservice; ghi nhận nhật ký tập trung theo định dạng `[service, timestamp, level, correlationId, message]`.
- **NFR-10 (Đóng gói & Triển khai - Deployment & CI/CD):** Đóng gói Docker Compose multi-stage build từ mã nguồn sạch không phụ thuộc file `.jar` có sẵn trên máy host; Triển khai Frontend tự động trên nền tảng Vercel với cấu hình SPA rewrites chống 404 khi tải lại trang; Tự động hóa kiểm thử mã nguồn qua GitHub Actions.
