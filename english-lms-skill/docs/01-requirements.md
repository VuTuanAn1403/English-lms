# TÀI LIỆU YÊU CẦU NGIỆM VỤ (01-REQUIREMENTS)

---

## 1. YÊU CẦU CHỨC NĂNG (FUNCTIONAL REQUIREMENTS - FR)

### 1.1. Quản lý Tài khoản & Định danh (Authentication & User Management)
- **FR-01 (Đăng ký tài khoản)**: Hệ thống cho phép người dùng đăng ký tài khoản mới với Email, Mật khẩu và Họ tên. Mật khẩu phải được mã hóa BCrypt trước khi lưu database.
- **FR-02 (Đăng nhập & Cấp Token)**: Xác thực Email và Mật khẩu, trả về chuỗi JWT Token chứa thông tin định danh `userId`, `sub` (email) và `roles`.
- **FR-03 (Quản lý Profile)**: Người dùng có thể xem và cập nhật thông tin cá nhân (Họ tên, Số điện thoại, Avatar).
- **FR-04 (Quản trị Người dùng - Admin)**: ADMIN có quyền xem danh sách người dùng phân trang, thay đổi trạng thái tài khoản (`ACTIVE`, `SUSPENDED`) hoặc cấp quyền.

### 1.2. Quản lý Khóa học & Bài học (Course & Lesson Management)
- **FR-05 (Public Course Catalog)**: Mọi người dùng (kể cả chưa đăng nhập) có thể tìm kiếm và xem danh sách khóa học kèm bộ lọc trình độ (`BEGINNER`, `INTERMEDIATE`, `ADVANCED`).
- **FR-06 (Chi tiết Khóa học & Summary Lessons)**: Endpoint public `/api/v1/courses/{id}/lessons` chỉ trả về danh sách bài học định dạng tóm tắt (Summary) chứa `id`, `title`, `lessonOrder`, `duration`, `isTrial`. Nội dung chi tiết bài học và media chỉ trả về tại `/api/v1/lessons/{id}` khi có xác thực hợp lệ.
- **FR-07 (Quản trị Khóa học & Bài học - Admin)**: ADMIN có quyền Thêm, Sửa, Xóa khóa học và bài học; thay đổi thứ tự bài học (`reorder`).
- **FR-08 (Chính sách Xóa Khóa học)**: Hệ thống chặn xóa đối với khóa học đã có bài học hoặc đã có học viên đăng ký, trả về HTTP 409 Conflict.

### 1.3. Học thử & Mua Khóa học (Trial & Payment Engine)
- **FR-09 (Học thử 5 bài đầu)**: Khóa học trả phí (`price > 0`) cho phép học viên đã đăng ký học thử miễn phí 5 bài học đầu tiên (Index 0..4) dưới trạng thái enrollment `TRIAL`. Truy cập từ bài 6 trở đi mà chưa mua sẽ bị từ chối với mã lỗi HTTP 403 `COURSE_PURCHASE_REQUIRED`.
- **FR-10 (Thanh toán Khóa học)**: Học viên tạo đơn hàng mua khóa học và thanh toán qua cổng VNPay Sandbox hoặc Mock Payment. Khi thanh toán thành công, trạng thái enrollment chuyển sang `ACTIVE` và mở khóa toàn bộ bài học.
- **FR-11 (Xử lý Callback Bất biến)**: Hệ thống xử lý các request callback/IPN lặp lại từ cổng thanh toán một cách bất biến (Idempotent), không tạo trùng lặp enrollment hay nhân đôi doanh thu.

### 1.4. Tiến độ Học tập (Progress Tracking)
- **FR-12 (Đánh dấu Hoàn thành Bài học)**: Học viên ghi nhận hoàn thành bài học qua `POST /api/v1/progress/complete`. Hệ thống kiểm tra bài học phải thuộc về khóa học tương ứng, tự động tính % tiến độ và khóa trần ở mức 100%.

### 1.5. Trợ lý AI Assistant (AI Features)
- **FR-13 (Chat AI Theo Session)**: Cho phép giao tiếp với AI Assistant, tự động duy trì ngữ cảnh cuộc hội thoại dựa trên `sessionId`. Mỗi lượt gửi/nhận chỉ lưu 1 bản ghi vào `chat_histories`.
- **FR-14 (Grammar Check & Quiz Generator)**: 
  - Sửa lỗi ngữ pháp trả về JSON chuẩn 8 trường dữ liệu.
  - Sinh bài tập trắc nghiệm trả về danh sách câu hỏi 4 lựa chọn kèm đáp án đúng dạng chỉ số số `correctAnswer` (0..3).
- **FR-15 (Lịch sử Chat AI)**: Học viên truy xuất lịch sử chat của chính mình; ADMIN có quyền xem và quản lý toàn bộ lịch sử AI.

---

## 2. YÊU CẦU PHI CHỨC NĂNG (NON-FUNCTIONAL REQUIREMENTS - NFR)

- **NFR-01 (Bảo mật JWT & RBAC)**: Tất cả API nghiệp vụ phải xác thực Token JWT ở API Gateway và kiểm tra chữ ký/Claims độc lập tại các Microservices.
- **NFR-02 (Hiệu năng Truy vấn Database)**: Thêm index cho các cột truy vấn thường xuyên (`enrollments`, `learning_progress`, `chat_histories`). Tất cả danh sách quản trị phải được phân trang (Pagination).
- **NFR-03 (Tính Nhất quan Dữ liệu Frontend - Backend)**: Trạng thái UI hoàn thành bài học chỉ cập nhật khi backend trả về HTTP 200/201. Không sử dụng dữ liệu giả để che lỗi API.
- **NFR-04 (Xử lý Lỗi AI Mềm dẻo)**: Khi AI Provider gặp lỗi JSON parsing, hết quota hoặc timeout, hệ thống trả về thông báo lỗi thân thiện thay vì làm sập ứng dụng.
- **NFR-05 (Khả năng Đóng gói Docker)**: Hệ thống có thể build và chạy toàn bộ từ mã nguồn qua `docker compose up --build`.

---

## 3. MA TRẬN PHÂN QUYỀN (ROLE & PERMISSION MATRIX)

| API Group / Feature Endpoint | Public (Unauthenticated) | Student (`ROLE_STUDENT`) | Admin (`ROLE_ADMIN`) |
| :--- | :---: | :---: | :---: |
| `POST /api/v1/auth/register`, `login` | ✅ Cho phép | ✅ Cho phép | ✅ Cho phép |
| `GET /api/v1/courses`, `GET /api/v1/courses/{id}` | ✅ Cho phép | ✅ Cho phép | ✅ Cho phép |
| `GET /api/v1/courses/{id}/lessons` (Summary) | ✅ Cho phép | ✅ Cho phép | ✅ Cho phép |
| `GET /api/v1/lessons/{id}` (Full Content) | ❌ Từ chối (401) | ✅ Cho phép (Bài 1..5 hoặc đã mua) | ✅ Cho phép |
| `POST /api/v1/enrollments` | ❌ Từ chối (401) | ✅ Cho phép | ✅ Cho phép |
| `POST /api/v1/orders`, `POST /api/v1/payments/*` | ❌ Từ chối (401) | ✅ Cho phép | ✅ Cho phép |
| `POST /api/v1/progress/complete` | ❌ Từ chối (401) | ✅ Cho phép | ✅ Cho phép |
| `POST /api/v1/ai/*` (Chat, Grammar, Quiz) | ❌ Từ chối (401) | ✅ Cho phép | ✅ Cho phép |
| `GET /api/v1/admin/*` (Revenue, User Admin) | ❌ Từ chối (401) | ❌ Từ chối (403) | ✅ Cho phép |
| `POST/PUT/DELETE /api/v1/courses/*` | ❌ Từ chối (401) | ❌ Từ chối (403) | ✅ Cho phép |
