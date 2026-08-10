# USER SERVICE - QUẢN LÝ NGƯỜI DÙNG & ĐỊNH DANH

## 1. Mô tả
Quản lý người dùng: Đăng ký, Đăng nhập, JWT Token Service, Hồ sơ cá nhân, Phân quyền RBAC.

## 2. Thông tin Kỹ thuật
- **Port**: 8081
- **Database**: `user_db` (PostgreSQL - Port 5432)
- **Swagger UI**: `http://localhost:8081/swagger-ui.html`

## 3. Danh sách Endpoints chính
| Method | Path | Quyền Truy Cập | Mô tả |
| :--- | :--- | :--- | :--- |
| POST | `/api/v1/auth/register` | Public | Đăng ký học viên |
| POST | `/api/v1/auth/login` | Public | Đăng nhập & Lấy JWT Token |
| GET | `/api/v1/users/my-info` | Authenticated | Xem thông tin cá nhân |
| PUT | `/api/v1/users/my-info` | Authenticated | Cập nhật thông tin cá nhân |
| GET | `/api/v1/users` | Admin (`ROLE_ADMIN`) | Danh sách người dùng phân trang |
| PATCH | `/api/v1/users/{id}/status` | Admin (`ROLE_ADMIN`) | Cập nhật trạng thái tài khoản |

> Tài liệu API đầy đủ xem tại [docs/04-api.md](../../docs/04-api.md).
