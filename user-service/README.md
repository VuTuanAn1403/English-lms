# User Service

## Mô tả
Quản lý người dùng - Đăng ký, Đăng nhập, JWT, Hồ sơ, Phân quyền.

## Port
- 8081

## Database
- user_db (PostgreSQL)

## API
| Method | Path | Mô tả |
|--------|------|-------|
| POST | /api/v1/auth/register | Đăng ký |
| POST | /api/v1/auth/login | Đăng nhập |
| GET | /api/v1/users/profile | Xem hồ sơ |
| PUT | /api/v1/users/profile | Cập nhật hồ sơ |

## Chạy
```bash
mvn spring-boot:run
```

## Swagger
- http://localhost:8081/swagger-ui.html

## Docker
```bash
docker build -t english-lms/user-service .
docker run -p 8081:8081 english-lms/user-service
```
