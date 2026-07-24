# Course Service

## Mô tả
Quản lý khóa học - CRUD Khóa học, CRUD Bài học, Đăng ký khóa học, Theo dõi tiến độ.

## Port
- 8082

## Database
- course_db (PostgreSQL)

## API
| Method | Path | Mô tả |
|--------|------|-------|
| GET | /api/v1/courses | Danh sách khóa học |
| GET | /api/v1/courses/{id} | Chi tiết khóa học |
| POST | /api/v1/courses | Thêm khóa học (ADMIN) |
| PUT | /api/v1/courses/{id} | Sửa khóa học (ADMIN) |
| DELETE | /api/v1/courses/{id} | Xóa khóa học (ADMIN) |
| GET | /api/v1/courses/{courseId}/lessons | Danh sách bài học |
| POST | /api/v1/lessons | Thêm bài học (ADMIN) |
| POST | /api/v1/enrollments | Đăng ký khóa học |
| GET | /api/v1/enrollments/my-courses | Khóa học đã đăng ký |

## Chạy
```bash
mvn spring-boot:run
```

## Swagger
- http://localhost:8082/swagger-ui.html

## Docker
```bash
docker build -t english-lms/course-service .
docker run -p 8082:8082 english-lms/course-service
```
