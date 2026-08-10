# Config Server

## Mô tả
Spring Cloud Config Server - Quản lý cấu hình tập trung cho tất cả Microservice.

## Port
- 8888

## Chạy
```bash
mvn spring-boot:run
```

## Kiểm tra
- http://localhost:8888/user-service/default
- http://localhost:8888/course-service/default
- http://localhost:8888/ai-service/default
- http://localhost:8888/api-gateway/default

## Docker
```bash
docker build -t english-lms/config-server .
docker run -p 8888:8888 english-lms/config-server
```
