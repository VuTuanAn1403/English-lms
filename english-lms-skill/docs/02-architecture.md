# TÀI LIỆU KIẾN TRÚC HỆ THỐNG (02-ARCHITECTURE)

---

## 1. SƠ ĐỒ TỔNG QUAN KIẾN TRÚC (MERMAID SYSTEM DIAGRAM)

```mermaid
graph TD
    Client[React / Vite Frontend - Port 3000] -->|HTTPS / REST API| Gateway[API Gateway - Port 8080]
    
    subgraph Infrastructure Layer
        Gateway -->|Fetch Route Rules| ConfigServer[Config Server - Port 8888]
        Gateway -->|Service Discovery| Eureka[Discovery Server - Port 8761]
        UserSvc[User Service - Port 8081] <--> Eureka
        CourseSvc[Course Service - Port 8082] <--> Eureka
        AiSvc[AI Service - Port 8083] <--> Eureka
    end

    subgraph Business Microservices Layer
        Gateway -->|Routing /api/v1/auth, /users| UserSvc
        Gateway -->|Routing /api/v1/courses, /orders, /progress| CourseSvc
        Gateway -->|Routing /api/v1/ai| AiSvc
    end

    subgraph Data & Provider Layer
        UserSvc <---> DB_User[(PostgreSQL: user_db)]
        CourseSvc <---> DB_Course[(PostgreSQL: course_db)]
        AiSvc <---> DB_Ai[(PostgreSQL: ai_db)]
        AiSvc <---> Gemini[Google Gemini API Provider]
    end
```

---

## 2. THÀNH PHẦN HẠ TẦNG VÀ PHÂN VÙNG BẢO MẬT (TRUST BOUNDARY)

### 2.1. Discovery Server (Eureka Server - Port 8761)
Đóng vai trò Service Registry, tự động quản lý IP và trạng thái sức khỏe (Health Check) của tất cả microservices trong hệ thống. Đảm bảo API Gateway thực hiện cân bằng tải (Load Balancing `lb://`) linh hoạt.

### 2.2. Config Server (Port 8888)
Lưu trữ và phân phối cấu hình tập trung cho toàn bộ hệ thống từ thư mục cấu hình. Hỗ trợ thay đổi tham số môi trường động mà không cần Rebuild mã nguồn.

### 2.3. API Gateway (Port 8080) & Ranh giới Tin cậy (Trust Boundary)
- **Kiểm tra JWT tại Gateway**: Gateway kiểm tra định dạng Token và giải mã Signature đầu tiên đối với các request vào endpoint bảo mật.
- **Tại sao các Microservice bên trong vẫn phải Validate JWT?**: Theo nguyên tắc Zero-Trust Security, API Gateway chỉ đóng vai trò rào chắn ngoài. Các microservices (`user-service`, `course-service`, `ai-service`) vẫn phải độc lập trích xuất JWT Token, kiểm tra quyền hạn (`ROLE_STUDENT` / `ROLE_ADMIN`) và lấy `userId` chính xác từ JWT Claims nhằm ngăn chặn hành vi mạo danh thông tin giữa các service (Header Spoofing).

---

## 3. CÁC LUỒNG XỬ LÝ CHÍNH (SEQUENCE FLOWS)

### 3.1. Luồng Đăng nhập & Xác thực JWT (Login & Authentication Flow)
```mermaid
sequenceDiagram
    autonumber
    actor User as Học viên / Admin
    participant FE as React Frontend
    participant GW as API Gateway
    participant US as User Service
    participant DB as user_db

    User->>FE: Nhập Email & Password
    FE->>GW: POST /api/v1/auth/login
    GW->>US: Forward Request
    US->>DB: Tìm User theo Email
    DB-->>US: Trả về thông tin User & Password Hash
    US->>US: Khớp Password với BCryptEncoder
    US->>US: Tạo JWT Token chứa userId, email, roles
    US-->>FE: Trả về JWT Token & User Info
    FE->>FE: Lưu JWT Token vào LocalStorage
```

### 3.2. Luồng Đăng ký & Học thử Bài học (Trial & Enrollment Flow)
```mermaid
sequenceDiagram
    autonumber
    actor Student as Học viên
    participant FE as React Frontend
    participant GW as API Gateway
    participant CS as Course Service
    participant DB as course_db

    Student->>FE: Bấm "Học thử 5 bài đầu"
    FE->>GW: POST /api/v1/enrollments (Kèm Bearer JWT)
    GW->>CS: Validate JWT & Forward
    CS->>DB: Tạo Enrollment (Status: TRIAL)
    DB-->>CS: Thành công
    CS-->>FE: Trả về thông tin Enrollment
    Student->>FE: Truy cập Bài 6
    FE->>CS: GET /api/v1/lessons/{lessonId_6}
    CS->>CS: Kiểm tra Enrollment Status (TRIAL)
    CS-->>FE: HTTP 403 Forbidden (COURSE_PURCHASE_REQUIRED)
    FE->>Student: Hiển thị Hộp thoại Yêu cầu Mua khóa học
```

### 3.3. Luồng Chat AI Duy trì Session (AI Chat Session Flow)
```mermaid
sequenceDiagram
    autonumber
    actor Student as Học viên
    participant FE as React Frontend
    participant AS as AI Service
    participant DB as ai_db
    participant Gemini as Google Gemini API

    Student->>FE: Gửi tin nhắn Chat AI (SessionId: A)
    FE->>AS: POST /api/v1/ai/chat (sessionId: A, prompt)
    AS->>DB: Lấy Lịch sử Chat gần nhất của Session A
    DB-->>AS: Danh sách Messages cũ
    AS->>Gemini: Gửi System Prompt + History + User Prompt
    Gemini-->>AS: Trả về Phản hồi Text
    AS->>DB: Lưu 1 bản ghi duy nhất (Prompt + Response + SessionId A)
    AS-->>FE: Trả về AI Response Chat
```
