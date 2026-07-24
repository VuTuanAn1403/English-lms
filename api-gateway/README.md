# API Gateway

## Mô tả
Spring Cloud Gateway - Điểm truy cập duy nhất của hệ thống. Thực hiện routing, CORS, JWT Authentication.

## Port
- 8080

## Routing
| Path | Service |
|------|---------|
| /api/v1/auth/**, /api/v1/users/** | user-service |
| /api/v1/courses/**, /api/v1/lessons/**, /api/v1/enrollments/** | course-service |
| /api/v1/ai/** | ai-service |

## Chạy
```bash
mvn spring-boot:run
```

## Docker
```bash
docker build -t english-lms/api-gateway .
docker run -p 8080:8080 english-lms/api-gateway
```
