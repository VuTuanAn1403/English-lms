# HỆ THỐNG QUẢN LÝ KHÓA HỌC TIẾNG ANH TRỰC TUYẾN TÍCH HỢP TRÍ TUỆ NHÂN TẠO (ENGLISH LMS)

> **Hệ thống Microservices quản lý khóa học tiếng Anh tích hợp Trí tuệ nhân tạo (Google Gemini AI via Spring AI) xây dựng theo chuẩn Spring Cloud & React Material UI.**

---

## 1. GIỚI THIỆU DỰ ÁN

**English LMS** là một hệ thống quản lý khóa học tiếng Anh trực tuyến hiện đại. Hệ thống kết hợp giữa việc học trực tuyến qua video/tài liệu bài giảng và khả năng hỗ trợ học tập thông minh từ **Trí tuệ nhân tạo (AI)**:
- **Hỏi đáp AI**: Giải đáp các thắc mắc về ngữ pháp, từ vựng và giao tiếp tiếng Anh bằng tiếng Việt.
- **Sửa lỗi ngữ pháp**: Phân tích câu tiếng Anh, tự động sửa lỗi và giải thích nguyên nhân chi tiết.
- **Sinh bài tập trắc nghiệm**: Tự động khởi tạo bộ câu hỏi trắc nghiệm (Multiple Choice) theo bài học/chủ đề kèm lời giải.
- **Bảo mật & Phân quyền**: Đăng ký, đăng nhập JWT stateless với phân quyền chi tiết `ADMIN` và `STUDENT`.

---

## 2. KIẾN TRÚC HỆ THỐNG (MICROSERVICES ARCHITECTURE)

Hệ thống được thiết kế theo kiến trúc **Microservices với Database-per-Service** sử dụng Spring Cloud:

```text
                        ┌─────────────────────────┐
                        │   React Client (Vite)   │
                        │      (Port: 3000)       │
                        └────────────┬────────────┘
                                     │
                                     │ HTTP Request
                                     ▼
                        ┌─────────────────────────┐
                        │   Spring Cloud Gateway  │
                        │      (Port: 8080)       │
                        └────────────┬────────────┘
                                     │
         ┌───────────────────────────┼───────────────────────────┐
         │                           │                           │
         v                           v                           v
┌─────────────────┐         ┌─────────────────┐         ┌─────────────────┐
│  User Service   │         │ Course Service  │         │   AI Service    │
│   (Port: 8081)  │         │   (Port: 8082)  │         │   (Port: 8083)  │
└────────┬────────┘         └────────┬────────┘         └────────┬────────┘
         │                           │                           │
         v                           v                           v
┌─────────────────┐         ┌─────────────────┐         ┌─────────────────┐
│ PostgreSQL User │         │PostgreSQL Course│         │  PostgreSQL AI  │
│   (user_db)     │         │   (course_db)   │         │    (ai_db)      │
└─────────────────┘         └─────────────────┘         └─────────────────┘

                ┌─────────────────────────────────────────┐
                │   Eureka Discovery Server (Port: 8761)  │
                └─────────────────────────────────────────┘
                ┌─────────────────────────────────────────┐
                │    Spring Cloud Config Server (8888)    │
                └─────────────────────────────────────────┘
```

---

## 3. CÔNG NGHỆ SỬ DỤNG

### Backend (Microservices)
- **Java**: Java 21 LTS
- **Framework**: Spring Boot 3.5.0, Spring Data JPA, Spring Security
- **Microservices Infrastructure**: Spring Cloud Gateway, Eureka Server, Spring Cloud Config
- **AI Integration**: Spring AI `1.0.0`, Google Gemini API
- **Database & Migration**: PostgreSQL 16, Flyway Database Migration
- **Tools & Libraries**: MapStruct, Lombok, JJWT (`0.12.6`), SpringDoc OpenAPI 3 / Swagger

### Frontend
- **Framework**: React 18, Vite 6
- **UI Component Library**: Material UI v6 (`@mui/material`), Emotion, `@mui/icons-material`
- **HTTP Client & Router**: Axios, React Router DOM v7
- **Design Aesthetic**: Glassmorphic UI, Plus Jakarta Sans Font, Responsive 100% Tiếng Việt

### DevOps & Deployment
- **Containerization**: Docker, Multi-stage Dockerfiles
- **Orchestration**: Docker Compose (10 Containers)
- **Web Server**: Nginx Alpine

---

## 4. CẤU TRÚC THƯ MỤC DỰ ÁN

```text
BTL_MSCNPTPM/
├── docs/                      # Tài liệu thiết kế hệ thống
├── prompts/                   # Kịch bản các bước phát triển (Prompts 01 -> 10)
└── english-lms/               # Mã nguồn chính
    ├── discovery-server/      # Eureka Discovery Server (Port 8761)
    ├── config-server/         # Spring Cloud Config Server (Port 8888)
    ├── api-gateway/           # API Gateway - Routing, JWT Filter, CORS (Port 8080)
    ├── user-service/          # Quản lý Đăng ký, Đăng nhập, JWT, Hồ sơ (Port 8081)
    ├── course-service/        # Quản lý Khóa học, Bài học, Đăng ký học (Port 8082)
    ├── ai-service/            # Chat AI, Grammar Checker, Quiz Generator (Port 8083)
    ├── frontend/              # Giao diện React Material UI (Port 3000)
    ├── docker-compose.yml     # File khởi chạy 10 Docker Containers
    ├── pom.xml                # Parent Maven Multi-Module POM
    ├── PROJECT_REVIEW.md      # Báo cáo đánh giá dự án
    └── README.md              # Tài liệu hướng dẫn sử dụng
```

---

## 5. HƯỚNG DẪN KHỞI CHẠY HỆ THỐNG

### Cách 1: Khởi chạy bằng Docker Compose (Khuyên dùng)

Yêu cầu: Đã cài đặt **Docker** và **Docker Compose**.

```bash
cd english-lms

# Build và khởi chạy tất cả 10 containers ngầm
docker compose up -d --build

# Kiểm tra danh sách các container đang chạy
docker compose ps
```

### Cách 2: Khởi chạy thủ công từng Service (Local Development)

Yêu cầu: Java 21, Maven 3.9+, Node.js 20+, PostgreSQL running (user_db, course_db, ai_db).

```bash
cd english-lms

# 1. Biên dịch toàn bộ Multi-Module Project
mvn clean install

# 2. Khởi chạy từng dịch vụ theo đúng thứ tự:
# Terminal 1: Discovery Server
cd discovery-server && mvn spring-boot:run

# Terminal 2: Config Server
cd config-server && mvn spring-boot:run

# Terminal 3: API Gateway
cd api-gateway && mvn spring-boot:run

# Terminal 4: User Service
cd user-service && mvn spring-boot:run

# Terminal 5: Course Service
cd course-service && mvn spring-boot:run

# Terminal 6: AI Service
cd ai-service && mvn spring-boot:run

# Terminal 7: React Frontend
cd frontend && npm install && npm run dev
```

---

## 6. DANH SÁCH REST API GOVI DÙNG

| Service | Method | Endpoint Path | Quyền truy cập | Mô tả |
|---|---|---|---|---|
| **Auth** | `POST` | `/api/v1/auth/register` | Public | Đăng ký tài khoản học viên mới |
| **Auth** | `POST` | `/api/v1/auth/login` | Public | Đăng nhập & lấy JWT Access Token |
| **User** | `GET` | `/api/v1/users/profile` | Authenticated | Xem thông tin hồ sơ học viên |
| **User** | `PUT` | `/api/v1/users/profile` | Authenticated | Cập nhật tên & ảnh đại diện |
| **Course** | `GET` | `/api/v1/courses` | Public | Danh sách tất cả khóa học |
| **Course** | `GET` | `/api/v1/courses/{id}` | Public | Chi tiết khóa học theo ID |
| **Course** | `GET` | `/api/v1/courses/{id}/lessons` | Public | Danh sách bài học của khóa học |
| **Course** | `POST` | `/api/v1/courses` | ADMIN | Tạo mới khóa học |
| **Enroll** | `POST` | `/api/v1/enrollments` | Authenticated | Đăng ký tham gia khóa học |
| **Enroll** | `GET` | `/api/v1/enrollments/my-courses` | Authenticated | Danh sách khóa học đã đăng ký |
| **AI** | `POST` | `/api/v1/ai/chat` | Authenticated | Hỏi đáp trực tiếp với AI |
| **AI** | `POST` | `/api/v1/ai/grammar` | Authenticated | Sửa lỗi ngữ pháp & giải thích |
| **AI** | `POST` | `/api/v1/ai/quiz` | Authenticated | Sinh bài tập trắc nghiệm tự động |
| **AI** | `GET` | `/api/v1/ai/history` | Authenticated | Xem lịch sử tương tác AI |

---

## 7. SWAGGER UI & TÀI LIỆU API

Mỗi service đều được tích hợp **OpenAPI 3 / Swagger UI** kèm nút **Authorize** để thử nghiệm Bearer Token:
- **API Gateway**: `http://localhost:8080/swagger-ui.html`
- **User Service**: `http://localhost:8081/swagger-ui.html`
- **Course Service**: `http://localhost:8082/swagger-ui.html`
- **AI Service**: `http://localhost:8083/swagger-ui.html`
- **Eureka Dashboard**: `http://localhost:8761`

---

## 8. THÀNH VIÊN NHÓM THỰC HIỆN

- **Đề tài**: Xây dựng hệ thống quản lý khóa học tiếng Anh trực tuyến tích hợp trí tuệ nhân tạo theo kiến trúc Microservice.
- **Học phần**: Bài tập lớn Môi trường & Thiết kế phần mềm.
