# BÁO CÁO ĐÁNH GIÁ TỔNG QUAN DỰ ÁN (PROJECT REVIEW)

> **Báo cáo tổng kết Bài Tập Lớn môn Môi trường và Công cụ Lập trình phần mềm (MSCNPTPM)**  
> **Dự án**: English LMS - Hệ thống quản lý học tập tiếng Anh tích hợp AI Assistant

---

## 1. TỔNG QUAN ĐÁNH GIÁ DỰ ÁN

Dự án English LMS được hoàn thiện theo mô hình Microservices với 6 dịch vụ Java Spring Boot và 1 ứng dụng React Frontend. Hệ thống tập trung giải quyết các bài toán nghiệp vụ học tập cốt lõi, bảo mật định danh người dùng, học thử 5 bài đầu miễn phí, thanh toán mở khóa học và tích hợp Trợ lý AI Assistant.

---

## 2. NHỮNG ĐIỂM MẠNH ĐÃ ĐƯỢC XÁC MINH (PROVEN STRENGTHS)

1. **Bảo mật và Định danh Chặt chẽ**:
   - Xác thực qua JWT Token chứa `userId` thật. API Gateway và từng Microservice độc lập giải mã JWT để kiểm tra quyền hạn (`ROLE_STUDENT`, `ROLE_ADMIN`), ngăn chặn hoàn toàn mạo danh header (Header Spoofing).
2. **Nghiệp vụ Mua khóa học & Học thử Chính xác**:
   - Cho phép học thử 5 bài đầu miễn phí đối với khóa học trả phí.
   - Xử lý mở khóa bài 6+ sau khi thanh toán qua VNPay Sandbox / Mock Gateway với cơ chế xử lý callback bất biến (Idempotent).
3. **Trợ lý AI Đảm bảo Cấu trúc Dữ liệu**:
   - Xử lý Chat AI giữ nguyên `sessionId`.
   - Phản hồi Grammar Check tuân thủ JSON 8 trường dữ liệu và Sinh Quiz 4 lựa chọn với chỉ số `correctAnswer` (0..3).
4. **Kiến trúc Docker Multi-Stage Chạy Trực Tiếp Từ Mã Nguồn**:
   - `docker compose up --build` tự động biên dịch Maven và Node từ mã nguồn sạch, không phụ thuộc file `.jar` có sẵn trên máy host.
5. **Độ Tin Cậy Kiểm Thử Cao**:
   - **65/65 Backend Automated Unit & Security Tests Passed 100%**.
   - Frontend Production Build đóng gói Vite thành công không lỗi.

---

## 3. CÁC HẠN CHẾ CÒN TỒN TẠI (EXISTING LIMITATIONS)

1. **Cổng thanh toán Sandbox**: Môi trường thanh toán VNPay hiện tại sử dụng ngân hàng thử nghiệm (NCB Sandbox) của cổng VNPay.
2. **Phụ thuộc API Key bên thứ ba**: Trợ lý AI dựa trên Google Gemini API. Khi vượt quá quota của key cá nhân, hệ thống sẽ chuyển sang cơ chế xử lý lỗi an toàn chứ không thể phản hồi câu trả lời từ AI.
3. **Phạm vi tính năng**: Không triển khai các tính năng nâng cao nằm ngoài yêu cầu bài tập lớn như RAG / Vector Database / Recommendation Engine.

---

## 4. CHECKLIST DEMO DÀNH CHO GIẢNG VIÊN (DEMO CHECKLIST)

- [x] **Bước 1**: Chạy `docker compose up --build` khởi chạy 10 containers.
- [x] **Bước 2**: Truy cập http://localhost:3000, đăng ký tài khoản Học viên mới.
- [x] **Bước 3**: Mở danh sách khóa học, nhấn học thử 5 bài đầu tiên của một khóa học trả phí.
- [x] **Bước 4**: Thử truy cập bài thứ 6 -> Hệ thống hiển thị Hộp thoại Yêu cầu Mua khóa học (HTTP 403).
- [x] **Bước 5**: Nhấn Mua khóa học -> Chọn Thanh toán Mock / VNPay -> Hoàn tất thanh toán -> Khóa học được mở khóa toàn bộ.
- [x] **Bước 6**: Trải nghiệm Chat AI, Sửa lỗi Ngữ pháp (Grammar Check) và Sinh bài tập trắc nghiệm (Quiz Generator).
- [x] **Bước 7**: Đăng nhập tài khoản Admin (`admin@gmail.com` / `admin123`) -> Xem Báo cáo Doanh thu Admin và Quản lý Đơn hàng.
