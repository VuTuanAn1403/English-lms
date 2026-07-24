# Discovery Server

## Mô tả
Eureka Discovery Server - Quản lý đăng ký và phát hiện các Microservice.

## Port
- 8761

## Chạy
```bash
mvn spring-boot:run
```

## Dashboard
- http://localhost:8761

## Docker
```bash
docker build -t english-lms/discovery-server .
docker run -p 8761:8761 english-lms/discovery-server
```
