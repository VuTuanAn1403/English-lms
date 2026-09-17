# BẢNG KIỂM TRA KHÓI MÔI TRƯỜNG VERCEL (VERCEL SMOKE TEST CHECKLIST)
**Dự án:** English LMS – Hệ thống Quản lý Khóa học Tiếng Anh Trực tuyến Tích hợp AI  
**Mục tiêu:** Kiểm tra nhanh tính toàn vẹn và độ tin cậy của bản phát hành Frontend trên môi trường Vercel (Preview và Production)  
**Phiên bản:** 1.2 • **Ngày cập nhật:** 17/09/2026  

---

## 1. TIÊU CHÍ KIỂM TRA MÔI TRƯỜNG VÀ MẠNG (NETWORK & ENVIRONMENT INTEGRITY)

| STT | Hạng mục kiểm tra | Thao tác thực hiện | Kết quả mong đợi | Đánh giá |
| :---: | :--- | :--- | :--- | :---: |
| 1 | **HTTPS Protocol** | Mở URL Vercel trên trình duyệt (Chrome/Firefox/Edge) | Kết nối HTTPS bảo mật (biểu tượng ổ khóa màu xanh/hợp lệ), không cảnh báo chứng chỉ | [x] **PASS** |
| 2 | **Zero Localhost Traffic** | Mở F12 -> DevTools -> Tab Network, tải trang chủ và duyệt tính năng | 100% request API hướng tới domain Gateway công khai; tuyệt đối không có request trỏ về `localhost` hay `127.0.0.1` | [x] **PASS** |
| 3 | **CORS & Preflight (OPTIONS)** | Kiểm tra các request dạng POST/PUT/PATCH/DELETE | Các request OPTIONS trả về HTTP 200/204 kèm header `Access-Control-Allow-Origin` phù hợp | [x] **PASS** |
| 4 | **No HTML Fallback for API** | Kiểm tra response payload của các request API `/api/v1/**` | Trả về chuỗi định dạng JSON hợp lệ; không bị catch-all rewrite trả về nội dung của `index.html` | [x] **PASS** |
| 5 | **No Client Secret Exposure** | Mở Tab Sources / Quét toàn bộ bundle file `.js` | Không chứa `GEMINI_API_KEY`, `JWT_SECRET` hay mật khẩu cơ sở dữ liệu | [x] **PASS** |

---

## 2. KIỂM TRA ĐỊNH TUYẾN TRANG CON (SPA DEEP-LINK REFRESH)

| STT | Tuyến đường (Route) | Thao tác kiểm tra | Kết quả mong đợi | Đánh giá |
| :---: | :--- | :--- | :--- | :---: |
| 1 | `/login` | Dán trực tiếp link lên thanh địa chỉ và bấm Enter; sau đó bấm F5 | Giao diện Đăng nhập hiển thị bình thường; không báo lỗi HTTP 404 | [x] **PASS** |
| 2 | `/courses` | Truy cập trực tiếp danh mục khóa học và bấm F5 | Danh mục khóa học hiển thị bình thường | [x] **PASS** |
| 3 | `/courses/1` | Dán trực tiếp link chi tiết một khóa học cụ thể | Chi tiết khóa học và danh sách bài học nạp thành công | [x] **PASS** |
| 4 | `/profile` | Đăng nhập tài khoản, truy cập `/profile` và bấm F5 | Thông tin hồ sơ người dùng vẫn hiển thị ổn định sau khi tải lại | [x] **PASS** |
| 5 | `/admin/users` | Đăng nhập tài khoản ADMIN, truy cập `/admin/users` và bấm F5 | Danh sách quản lý người dùng hiển thị chuẩn xác | [x] **PASS** |

---

## 3. KIỂM TRA CÁC LUỒNG NGHIỆP VỤ CHÍNH (KEY BUSINESS FLOWS)

### 3.1. Luồng Học viên (Student Flow)
- [x] **Đăng ký tài khoản mới:** Đăng ký với email hợp lệ, chuyển hướng đăng nhập thành công.
- [x] **Đăng nhập & Phiên làm việc:** Nhận Bearer token, thanh điều hướng cập nhật trạng thái người dùng đã đăng nhập.
- [x] **Xem chi tiết khóa học:** Xem thông tin bài học và giảng viên.
- [x] **Ghi danh khóa học:** Ghi danh thành công, trạng thái khóa học chuyển sang đã đăng ký.
- [x] **Học bài & Hoàn thành:** Xem nội dung bài học, video player hiển thị chuẩn xác khi có link, cập nhật tiến độ học tập.
- [x] **Tương tác Trợ lý AI:** Đặt câu hỏi AI Tutor, kiểm tra ngữ pháp hoặc tạo đề trắc nghiệm thành công.
- [x] **Xem lịch sử AI:** Chỉ thấy lịch sử của chính tài khoản mình (Data Isolation).
- [x] **Đổi mật khẩu tự phục vụ (FR-06):** Đổi mật khẩu thành công tại trang Hồ sơ cá nhân; kiểm tra đổi với mật khẩu cũ sai báo lỗi chính xác.
- [x] **Đăng xuất:** Xóa token an toàn và trở về trang đăng nhập.

### 3.2. Luồng Quản trị viên (Admin Flow)
- [x] **Đăng nhập Admin:** Điều hướng thành công vào bảng điều khiển Quản trị (`/admin`).
- [x] **Quản lý người dùng (FR-10..13):** Xem danh sách, thêm, sửa vai trò người dùng.
- [x] **Quản lý khóa học (FR-14..16):** Thêm mới khóa học, cập nhật thông tin và quản lý đề cương bài học.
- [x] **Thống kê hệ thống (FR-21):** Số lượng Users, Courses, Lessons và Enrollments hiển thị dữ liệu thực tế từ database (không phụ thuộc dữ liệu tĩnh giả lập).

---

## 4. KIỂM TRA PHẢN HỒI LỖI VÀ BẢO MẬT (ERROR HANDLING & SECURITY)

- [x] **Token hết hạn (HTTP 401):** Khi xóa hoặc làm hỏng token trong `localStorage` và gửi yêu cầu, ứng dụng tự động xóa phiên và chuyển hướng về `/login` mà không bị lặp vô tận.
- [x] **Truy cập trái quyền (HTTP 403):** Tài khoản Student cố gắng truy cập trực tiếp vào các route `/admin/**` sẽ bị chặn và hiển thị thông báo không có quyền truy cập.
- [x] **Chống nhấn đúp (Double-Click Prevention):** Nút gửi dữ liệu tại các form quan trọng (Đổi mật khẩu, Ghi danh, Đăng nhập) chuyển sang trạng thái Disabled và hiển thị biểu tượng tải khi đang chờ API phản hồi.
- [x] **Lỗi máy chủ (HTTP 500/502):** Hiển thị hộp thông báo lỗi tiếng Việt thân thiện kèm nút "Thử lại"; tuyệt đối không tự ý hiển thị danh sách khóa học giả (`sampleCourses`).
