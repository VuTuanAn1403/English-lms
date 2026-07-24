# English LMS - Hệ thống Quản lý Khóa học Tiếng Anh Trực tuyến

## Giới thiệu
Hệ thống quản lý khóa học tiếng Anh trực tuyến tích hợp trí tuệ nhân tạo theo kiến trúc Microservice sử dụng API Gateway.

## Kiến trúc

```
                        +---------------------+
                        |     React Client    |
                        +----------+----------+
                                   |
                                   v
                    +-----------------------------+
                    | Spring Cloud Gateway :8080  |
                    +-------------+---------------+
                                  |
          +-----------------------+-----------------------+
          |                       |                       |
          v                       v                       v
 +----------------+     +----------------+      +----------------+
 | User Service   |     | Course Service |      | AI Service     |
 | :8081          |     | :8082          |      | :8083          |
 +--------+-------+     +--------+-------+      +--------+-------+
          |                      |                       |
          v                      v                       v
     user_db               course_db                ai_db
   (PostgreSQL)          (PostgreSQL)           (PostgreSQL)
```

## Công nghệ

| Thành phần | Công nghệ |
|------------|-----------|
| Backend | Java 21, Spring Boot 3.5, Spring Cloud |
| Frontend | React, Material UI, Axios |
| Database | PostgreSQL |
| AI | Spring AI, Google Gemini |
| Container | Docker, Docker Compose |
| Security | JWT, BCrypt |

## Cấu trúc dự án
```
english-lms/
├── discovery-server/      # Eureka Discovery Server (:8761)
├── config-server/         # Spring Cloud Config Server (:8888)
├── api-gateway/           # Spring Cloud Gateway (:8080)
├── user-service/          # User Management (:8081)
├── course-service/        # Course Management (:8082)
├── ai-service/            # AI Service (:8083)
├── frontend/              # React Frontend (:3000)
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Chạy dự án

### Yêu cầu
- Java 21
- Maven 3.9+
- Docker & Docker Compose
- Node.js 20+ (cho frontend)

### Build
```bash
cd english-lms
mvn clean install -DskipTests
```

### Chạy bằng Docker Compose
```bash
docker compose up --build
```

### Chạy thủ công (theo thứ tự)
```bash
# 1. Discovery Server
cd discovery-server && mvn spring-boot:run

# 2. Config Server
cd config-server && mvn spring-boot:run

# 3. API Gateway
cd api-gateway && mvn spring-boot:run

# 4. User Service
cd user-service && mvn spring-boot:run

# 5. Course Service
cd course-service && mvn spring-boot:run

# 6. AI Service
cd ai-service && mvn spring-boot:run
```

## Endpoints

| Service | URL |
|---------|-----|
| Eureka Dashboard | http://localhost:8761 |
| Config Server | http://localhost:8888 |
| API Gateway | http://localhost:8080 |
| User Service Swagger | http://localhost:8081/swagger-ui.html |
| Course Service Swagger | http://localhost:8082/swagger-ui.html |
| AI Service Swagger | http://localhost:8083/swagger-ui.html |
| Frontend | http://localhost:3000 |
