# BÁO CÁO XÁC MINH GIAO DIỆN ĐA THIẾT BỊ (RESPONSIVE DESIGN VERIFICATION REPORT)
**Dự án:** English LMS – Hệ thống Quản lý Khóa học Tiếng Anh Trực tuyến Tích hợp AI  
**Tiêu chuẩn áp dụng:** NFR-02 Responsive Design (ISO/IEC 25010 Usability & Portability)  
**Phiên bản:** 1.2 • **Ngày kiểm toán:** 17/09/2026  

---

## 1. MỤC TIÊU VÀ PHẠM VI KIỂM TOÁN (OBJECTIVES & VIEWPORTS)

Nhằm đảm bảo người học và quản trị viên có thể tiếp cận hệ thống một cách mượt mà, trực quan trên mọi loại thiết bị, hệ thống giao diện Frontend (React 18 + Material UI) được kiểm toán toàn diện trên 3 độ phân giải màn hình chuẩn:
1. **Thiết bị Di động (Mobile Viewport):** 375 x 667 px (Tương đương iPhone SE / iPhone 8).
2. **Thiết bị Máy tính bảng (Tablet Viewport):** 768 x 1024 px (Tương đương iPad Mini / iPad Portrait).
3. **Màn hình Máy tính để bàn (Desktop Viewport):** 1366 x 768 px (Màn hình Laptop / Desktop chuẩn HD).

---

## 2. MA TRẬN ĐÁNH GIÁ TỪNG MÀN HÌNH CHÍNH (PAGE-BY-PAGE AUDIT MATRIX)

| Trang giao diện (Page / Route) | Mobile (375 x 667) | Tablet (768 x 1024) | Desktop (1366 x 768) | Ghi chú thiết kế & Tối ưu hóa UI/UX | Đánh giá |
| :--- | :---: | :---: | :---: | :--- | :---: |
| **Trang chủ (`/`)** | **PASS** | **PASS** | **PASS** | Hero banner co giãn tỷ lệ `100%`, danh sách khóa học chuyển từ 1 cột (Mobile) sang 2 cột (Tablet) và 3-4 cột (Desktop). Không tràn viền. | Đạt |
| **Danh mục Khóa học (`/courses`)** | **PASS** | **PASS** | **PASS** | Bộ lọc danh mục chuyển thành Drawer hoặc thanh cuộn ngang trên mobile; thẻ khóa học co giãn linh hoạt (Grid `xs={12} sm={6} md={4}`). | Đạt |
| **Chi tiết Khóa học (`/courses/:id`)** | **PASS** | **PASS** | **PASS** | Khung bài học và thông tin học phí xếp chồng theo chiều dọc trên mobile; nút "Đăng ký" cố định trực quan dễ thao tác một tay. | Đạt |
| **Trang Học bài & Video (`/lessons/:id`)** | **PASS** | **PASS** | **PASS** | Trình phát video giữ tỷ lệ 16:9 responsive; danh sách đề cương bài học có thể thu gọn/mở rộng dưới màn hình video. | Đạt |
| **Hồ sơ Cá nhân & Đổi mật khẩu (`/profile`)** | **PASS** | **PASS** | **PASS** | Form đổi mật khẩu tự phục vụ (FR-06) căn chỉnh lề vừa vặn màn hình mobile, các nút bấm có kích thước chạm (touch target) >= 44px. | Đạt |
| **Trợ lý Gia sư AI (`/ai-chat`)** | **PASS** | **PASS** | **PASS** | Khung chat tự động co giãn chiều cao theo khung nhìn, thanh nhập liệu cố định ở cạnh dưới màn hình kèm nút gửi chống bấm đúp. | Đạt |
| **Lịch sử Tương tác AI (`/ai-history`)** | **PASS** | **PASS** | **PASS** | Bảng lịch sử hỗ trợ cuộn ngang an toàn hoặc hiển thị dạng thẻ (Card) trên màn hình nhỏ, bảo đảm không vỡ bố cục. | Đạt |
| **Bảng điều khiển Quản trị (`/admin`)** | **PASS** | **PASS** | **PASS** | Sidebar quản trị tự động chuyển thành menu ngăn kéo (Drawer/Hamburger) trên Mobile và Tablet; mở rộng cố định trên Desktop. | Đạt |
| **Quản trị Người dùng & Khóa học (`/admin/users`, `/admin/courses`)** | **PASS** | **PASS** | **PASS** | Bảng dữ liệu quản trị tích hợp thuộc tính `overflow-x: auto`, cho phép xem đầy đủ các cột thông tin mà không làm tràn giao diện trang. | Đạt |

---

## 3. TIÊU CHÍ KHẢ DỤNG TRÊN THIẾT BỊ DI ĐỘNG (MOBILE USABILITY CHECKLIST)

- [x] **Không xuất hiện thanh cuộn ngang trang ngoài ý muốn (Zero Unintended Horizontal Scroll):** Thẻ `body` và container chính giới hạn `max-width: 100vw; overflow-x: hidden`.
- [x] **Kích thước vùng chạm cảm ứng (Touch Targets):** Tất cả các nút bấm (`Button`), biểu tượng hành động (`IconButton`) và ô chọn (`Checkbox`) có kích thước tối thiểu 44 x 44 px, khoảng cách giãn cách tối thiểu 8 px.
- [x] **Khả năng đọc của phông chữ (Typography Legibility):** Cỡ chữ nội dung tối thiểu 14 px (Mobile) và 16 px (Desktop), sử dụng hệ phông chữ Roboto/Inter hiện đại, độ tương phản màu sắc chữ và nền đạt chuẩn WCAG AA (> 4.5:1).
- [x] **Trải nghiệm gõ phím trên màn hình cảm ứng:** Các ô nhập liệu trong form đổi mật khẩu và đăng nhập có thuộc tính `type="password"`, `type="email"`, tự động hiển thị bàn phím ảo thích hợp.

---

## 4. KẾT LUẬN
Giao diện ứng dụng English LMS đạt 100% tiêu chí tương thích Responsive trên toàn bộ 3 độ phân giải tiêu chuẩn (Mobile 375x667, Tablet 768x1024, Desktop 1366x768), đáp ứng trọn vẹn yêu cầu phi chức năng **NFR-02**.
