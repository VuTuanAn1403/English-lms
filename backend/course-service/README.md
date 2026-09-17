# COURSE SERVICE - QUẢN LÝ KHÓA HỌC, THANH TOÁN & TIẾN ĐỘ

## 1. Mô tả
Quản lý khóa học, bài học, học thử 5 bài đầu, đơn hàng, thanh toán (VNPay / Mock), ghi nhận tiến độ học tập và báo cáo doanh thu admin.

## 2. Thông tin Kỹ thuật
- **Port**: 8082
- **Database**: `course_db` (PostgreSQL - Port 5433)
- **Swagger UI**: `http://localhost:8082/swagger-ui.html`

## 3. Danh sách Endpoints chính
| Method | Path | Quyền Truy Cập | Mô tả |
| :--- | :--- | :--- | :--- |
| GET | `/api/v1/courses` | Public | Danh sách khóa học & lọc trình độ |
| GET | `/api/v1/courses/{id}` | Public | Chi tiết khóa học tổng quan |
| GET | `/api/v1/courses/{id}/lessons` | Public | Danh sách bài học outline (Summary DTO) |
| GET | `/api/v1/lessons/{id}` | Authenticated | Chi tiết bài học (Full Markdown, Video Embed, PDF Link) |
| POST | `/api/v1/enrollments` | Authenticated | Đăng ký học phần / Học thử 5 bài |
| POST | `/api/v1/progress/complete` | Authenticated | Đánh dấu hoàn thành bài học |
| POST | `/api/v1/orders` | Authenticated | Tạo đơn hàng mua khóa học |
| POST | `/api/v1/payments/create` | Authenticated | Khởi tạo URL thanh toán |
| GET | `/api/v1/admin/revenue` | Admin (`ROLE_ADMIN`) | Báo cáo thống kê doanh thu admin |

> Tài liệu API đầy đủ xem tại [docs/04-api.md](../../docs/04-api.md).
