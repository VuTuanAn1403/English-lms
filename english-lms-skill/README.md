# ENGLISH LMS - HỆ THỐNG QUẢN LÝ HỌC TẬP TIẾNG ANH TÍCH HỢP AI ASSISTANT

> **Dự án Bài Tập Lớn cuối kỳ môn Môi trường và Công cụ Lập trình phần mềm (MSCNPTPM)**  
> Hệ thống quản lý học tập tiếng Anh kiến trúc Microservices (Spring Boot & React), tích hợp Trợ lý AI Assistant (Google Gemini), quản lý khóa học, học thử 5 bài đầu, mua & thanh toán khóa học và thống kê doanh thu Admin.

---

## 📌 1. TỔNG QUAN SẢN PHẨM & ĐỐI TƯỢNG SỬ DỤNG

### 1.1. Mục tiêu hệ thống
English LMS được xây dựng nhằm cung cấp nền tảng học tiếng Anh trực tuyến hiện đại cho học viên và công cụ quản trị khóa học/doanh thu cho người quản trị:
- **Dành cho Học viên (Student)**: Tìm kiếm khóa học, xem nội dung bài học, học thử miễn phí 5 bài đầu tiên đối với các khóa học trả phí, thanh toán qua cổng VNPay/Mock Payment để mở khóa toàn bộ khóa học, đánh dấu tiến độ học tập và luyện tập giao tiếp / sửa lỗi ngữ pháp / sinh bài tập trắc nghiệm tự động cùng Trợ lý AI.
- **Dành cho Quản trị viên (Admin)**: Quản lý danh mục khóa học, quản lý danh sách học viên, quản lý đơn hàng/giao dịch thanh toán, theo dõi tiến độ tổng quan và xem báo cáo thống kê doanh thu chi tiết.

### 1.2. Tính năng Trợ lý AI thực tế (AI Features Scope)
- **Chat AI Assistant (Hỏi đáp & Luyện tập)**: Trò chuyện trực tiếp cùng AI về bài học, tự động duy trì ngữ cảnh theo `sessionId` ổn định.
- **Grammar Check (Sửa lỗi Ngữ pháp)**: Phân tích đoạn văn bản tiếng Anh và trả về phản hồi định dạng JSON gồm 8 trường dữ liệu (Original, Corrected, General Feedback, Error Type, Detailed Explanation, CEFR Level, Grammar Score, Corrected Sentences).
- **Quiz Generator (Sinh bài tập trắc nghiệm)**: Tự động sinh bài tập trắc nghiệm 4 lựa chọn với đáp án đúng dạng chỉ số số `correctAnswer` (0, 1, 2, 3) và giải thích chi tiết.
- **Lịch sử cuộc hội thoại (AI History)**: Lưu trữ và cho phép học viên tra cứu lại các lượt tương tác AI của chính mình.

*(Lưu ý: Hệ thống không triển khai kiến trúc RAG, VectorStore hay Recommendation Engine)*.

---

## 🛠️ 2. CÔNG NGHỆ SỬ DỤNG (TECH STACK & VERSIONS)

### Backend Microservices
- **Java**: OpenJDK 21 (Eclipse Temurin 21)
- **Framework**: Spring Boot `3.4.1`, Spring Cloud `2024.0.0`
- **Security**: Spring Security 6 + JJWT `0.12.6` (HMAC-SHA256)
- **ORM & Database Migration**: Spring Data JPA / Hibernate 6, Flyway Migration
- **AI Integration**: Spring AI `1.0.0-M4` (OpenAI / Gemini API Compatible)
- **Build Tool**: Apache Maven 3.9.x (Multi-module Architecture)

### Frontend Web App
- **Core**: React `18.3.1`, Vite `6.4.3`, React Router DOM `6.28.0`
- **UI Framework**: Material UI (MUI) `6.1.10`, Emotion `11.13.5`
- **HTTP Client**: Axios `1.7.9` (Tích hợp JWT Interceptor & 401 Redirect Handler)

### Infrastructure & Databases
- **Databases**: PostgreSQL 16 (Tách riêng database độc lập cho từng microservice: `user_db`, `course_db`, `ai_db`)
- **Service Discovery**: Spring Cloud Netflix Eureka Server (Port 8761)
- **Centralized Configuration**: Spring Cloud Config Server (Port 8888)
- **API Gateway**: Spring Cloud Gateway (Port 8080)
- **Containerization**: Docker Compose (Multi-stage Maven & Node builds)

---

## 🏗️ 3. KIẾN TRÚC HỆ THỐNG VÀ BẢNG PORT (MICROSERVICES & PORTS)

```
[ Frontend (React/Vite) ]  <--->  [ API Gateway (8080) ]
                                          |
        +---------------------------------+---------------------------------+
        |                                 |                                 |
[ User Service (8081) ]        [ Course Service (8082) ]       [ AI Service (8083) ]
  (Database: user_db)           (Database: course_db)           (Database: ai_db)
        |                                 |                                 |
        +---------------------------------+---------------------------------+
                                          |
                +-------------------------+-------------------------+
                |                                                   |
    [ Config Server (8888) ]                            [ Discovery Server (8761) ]
```

| Container / Service Name | Công Dụng | Internal Port | External Port |
| :--- | :--- | :---: | :---: |
| `frontend` | Giao diện người dùng React / Nginx | 3000 | **3000** |
| `api-gateway` | Cổng API Gateway chính, Routing & JWT Filter | 8080 | **8080** |
| `discovery-server` | Eureka Service Registration & Discovery | 8761 | **8761** |
| `config-server` | Quản lý cấu hình tập trung | 8888 | **8888** |
| `user-service` | Quản lý Tài khoản, Đăng ký, Đăng nhập, Profile | 8081 | **8081** |
| `course-service` | Khóa học, Bài học, Học thử, Thanh toán, Doanh thu | 8082 | **8082** |
| `ai-service` | Trợ lý Chat AI, Grammar Check, Sinh Quiz & History | 8083 | **8083** |
| `postgres-user` | Cơ sở dữ liệu PostgreSQL người dùng (`user_db`) | 5432 | **5432** |
| `postgres-course` | Cơ sở dữ liệu PostgreSQL khóa học (`course_db`) | 5432 | **5433** |
| `postgres-ai` | Cơ sở dữ liệu PostgreSQL AI (`ai_db`) | 5432 | **5434** |

---

## 🚀 4. HƯỚNG DẪN KHỞI CHẠY HỆ THỐNG

### 4.1. Yêu cầu môi trường (Prerequisites)
- Docker Desktop version 24.0+ & Docker Compose v2.x
- JDK 21 (nếu chạy local không qua Docker)
- Node.js v20.x & npm 10.x (nếu chạy local frontend)
- Python 3.8+ (nếu chạy script kiểm thử tự động)

### 4.2. Khởi chạy bằng Docker Compose (Khuyên dùng)
Hệ thống sử dụng Dockerfile multi-stage tự động biên dịch mã nguồn từ thư mục gốc, **không yêu cầu build sẵn JAR**.

```bash
# 1. Clone repository
git clone <repository_url>
cd english-lms

# 2. Tạo file cấu hình môi trường từ mẫu
cp .env.example .env

# 3. Mở file .env và điền GEMINI_API_KEY (nếu muốn test AI thật)
# GEMINI_API_KEY=your_actual_gemini_key_here

# 4. Khởi chạy toàn bộ hệ thống
docker compose up --build
```
> Trạng thái kiểm tra sức khỏe: Mở trình duyệt truy cập `http://localhost:8080/actuator/health`. Khi Gateway trả về HTTP 200 OK, toàn bộ hệ thống đã sẵn sàng.

### 4.3. Biến môi trường quan trọng (Environment Variables)
Tất cả các biến được quản lý tập trung trong file `.env.example`:

| Biến Môi Trường | Mô Tả | Giá Trị Mặc Định |
| :--- | :--- | :--- |
| `GEMINI_API_KEY` | API Key kết nối Google Gemini AI Provider | `demo_key` |
| `GEMINI_MODEL` | Tên mô hình Gemini AI | `gemini-1.5-flash` |
| `GEMINI_BASE_URL` | Base URL API AI | `https://generativelanguage.googleapis.com/v1beta/openai` |
| `JWT_SECRET` | Khóa bí mật ký JWT Token (HMAC-SHA256) | `english-lms-jwt-secret-key-must-be-at-least-256-bits-long-for-security` |
| `POSTGRES_USER` | Tên người dùng Postgres | `postgres` |
| `POSTGRES_PASSWORD` | Mật khẩu Postgres | `postgres` |
| `CONFIG_SERVER_URL` | URL kết nối Config Server | `http://config-server:8888` |
| `EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE` | URL kết nối Discovery Server | `http://discovery-server:8761/eureka/` |
| `CORS_ALLOWED_ORIGINS` | Danh sách Origin được phép truy cập | `http://localhost:3000,http://localhost:5173` |
| `VITE_API_BASE_URL` | Endpoint API Gateway cho Frontend | `http://localhost:8080` |

---

## 🔑 5. TÀI KHOẢN DEMO SẴN CÓ (DEMO ACCOUNTS)

> ⚠️ **CẢNH BÁO AN TOÀN**: Các tài khoản dưới đây được tự động tạo sẵn qua cơ chế Migration / Seed Data chỉ dùng cho mục đích kiểm thử và demo bài tập lớn. Phải đổi mật khẩu hoặc xóa tài khoản seed khi triển khai thực tế!

| Vai Trò (Role) | Email | Mật Khẩu | Quyền Hạn |
| :--- | :--- | :--- | :--- |
| **Quản trị viên (ADMIN)** | `admin@gmail.com` | `admin123` | Phân quyền Admin toàn hệ thống, Quản lý khóa học, Quản lý học viên, Xem thống kê doanh thu |
| **Học viên Demo (STUDENT)** | `student@gmail.com` | `password123` | Đăng ký khóa học, Học thử 5 bài đầu, Thanh toán mua khóa học, Học tập & Chat AI |

---

## 🧪 6. HƯỚNG DẪN KIỂM THỬ (TESTING COMMANDS)

### 6.1. Chạy Backend Unit & Integration Tests
```bash
# Chạy toàn bộ 65 test cases trên tất cả microservices
mvn clean test

# Hoặc chạy kiểm thử theo từng service riêng biệt
mvn test -pl user-service
mvn test -pl course-service
mvn test -pl ai-service
```

### 6.2. Kiểm thử Frontend Production Build
```bash
cd frontend
npm ci
npm run build
```

### 6.3. Chạy Automation Smoke Test Script
```bash
# Đảm bảo Docker stack đang chạy tại http://localhost:8080
python scripts/smoke-test.py http://localhost:8080
```

---

## 📦 7. NÉN BẢN NỘP BÀI (SUBMISSION PACKAGING)

Để xuất file nộp bài chuẩn không chứa file thừa (`.git`, `node_modules`, `target`, `dist`, `.env`):
- **Windows (PowerShell)**:
  ```powershell
  .\scripts\package-submission.ps1
  ```
- **Linux / macOS (Bash)**:
  ```bash
  chmod +x ./scripts/package-submission.sh
  ./scripts/package-submission.sh
  ```
=> Tạo ra file `english-lms-submission.zip` tại thư mục gốc mà **không làm mất bất kỳ file mã nguồn nào**.

---

## ⚠️ 8. GIỚI HẠN HIỆN TẠI VÀ HƯỚNG PHÁT TRIỂN (LIMITATIONS & ROADMAP)

### 8.1. Giới hạn hiện tại (Known Limitations)
- Thanh toán VNPay sử dụng cổng Sandbox / Test Environment của NCB Bank.
- AI Provider phụ thuộc vào Quota và Rate Limit tự nhiên của Google Gemini API Key. Khi vượt quá quota, hệ thống sẽ trả về thông báo lỗi thân thiện thay vì làm treo ứng dụng.
- Dữ liệu phương tiện bài học (Video) hiện tại hỗ trợ URL dạng YouTube Embed (`https://www.youtube.com/embed/{ID}`).

### 8.2. Hướng phát triển tương lai (Future Roadmap - Chưa triển khai)
- Tích hợp mô hình tìm kiếm ngữ nghĩa nâng cao (RAG / VectorStore) cho kho tài liệu PDF lớn.
- Phát triển ứng dụng di động native (React Native / Flutter).
- Tích hợp cổng thanh toán trực tiếp qua MoMo QR và ZaloPay.
