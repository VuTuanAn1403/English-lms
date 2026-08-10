# TÀI LIỆU DỰ ÁN ENGLISH LMS (00-PROJECT)

---

## 1. THÔNG TIN DỰ ÁN

- **Tên dự án**: English LMS - Hệ thống quản lý học tập tiếng Anh tích hợp AI Assistant
- **Bối cảnh**: Bài Tập Lớn môn *Môi trường và Công cụ Lập trình phần mềm (MSCNPTPM)*
- **Kiến trúc**: Microservices Architecture (Spring Boot & React)
- **Mục tiêu**: Xây dựng hệ thống học trực tuyến thực tế, đảm bảo an toàn bảo mật, tính năng đúng nghiệp vụ, dễ trình bày demo và đóng gói chạy được bằng Docker Compose.

---

## 2. PHẠM VI DỰ ÁN (PROJECT SCOPE)

### 2.1. Phạm vi triển khai (Included Scope)
1. **Quản lý Tài khoản & Định danh (User & Security)**:
   - Đăng ký, Đăng nhập, Quản lý Profile.
   - Định danh qua JWT Token mang thông tin `userId` thật giữa các microservices.
   - Phân quyền người dùng theo vai trò: `ROLE_STUDENT`, `ROLE_ADMIN`.
2. **Quản lý Khóa học & Bài học (Course & Lesson Management)**:
   - Quản lý danh mục khóa học, tìm kiếm, lọc theo trình độ (`BEGINNER`, `INTERMEDIATE`, `ADVANCED`).
   - Bài học dạng Outline Summary công khai & Chi tiết bài học nâng cao yêu cầu đăng nhập và mua khóa học.
   - Phân bổ media bài học linh hoạt (Embed Video YouTube, Tài liệu PDF).
3. **Học thử & Mua Khóa học (Free Trial & Payment)**:
   - Khóa học miễn phí (`isFree = true`): Truy cập tự do cho học viên đã đăng ký.
   - Khóa học trả phí (`price > 0`): Học thử miễn phí 5 bài học đầu tiên (Index 0..4). Từ bài 6 trở đi yêu cầu mua khóa học.
   - Đơn hàng & Cổng thanh toán: Tích hợp VNPay Sandbox & Mock Payment Gateway với cơ chế xử lý callback bất biến (Idempotent).
4. **Theo dõi Tiến độ Học tập (Progress Tracking)**:
   - Đánh dấu hoàn thành bài học, cập nhật % tiến độ tự động (giới hạn trần 100%).
   - Bảng theo dõi tiến độ cá nhân của học viên.
5. **Trợ lý AI Assistant (AI Features)**:
   - **Chat AI**: Duy trì ngữ cảnh hội thoại ổn định theo `sessionId`.
   - **Grammar Check**: Phân tích đoạn văn bản tiếng Anh và trả về JSON 8 trường dữ liệu.
   - **Quiz Generator**: Sinh bài tập trắc nghiệm 4 lựa chọn với đáp án dạng chỉ số số `correctAnswer` (0, 1, 2, 3).
   - **AI History**: Lưu trữ và truy xuất lịch sử tương tác AI cá nhân của học viên.
6. **Thống kê Doanh thu Quản trị (Admin Analytics)**:
   - Báo cáo tổng doanh thu, số đơn hàng thành công/chờ xử lý, số học viên duy nhất, giá trị trung bình đơn hàng (AOV), biểu đồ theo thời gian và khóa học bán chạy nhất.

### 2.2. Hạng mục nằm ngoài phạm vi (Out of Scope / Non-Goals)
- Không triển khai kiến trúc RAG (Retrieval-Augmented Generation) hoặc Vector Database.
- Không triển khai Recommendation Engine hoặc thuật toán gợi ý khóa học thông minh phức tạp.
- Không hỗ trợ cuộc gọi thoại trực tiếp / Speech-to-Text / Text-to-Speech ngoài luồng văn bản.

---

## 3. ĐỐI TƯỢNG SỬ DỤNG (TARGET USERS)

1. **Học viên (Student)**: Người dùng học tiếng Anh trực tuyến, thực hành làm bài tập và luyện tập giao tiếp với AI.
2. **Quản trị viên (Admin)**: Người quản lý hệ thống, kiểm duyệt nội dung khóa học, bài học và theo dõi báo cáo doanh thu kinh doanh.
