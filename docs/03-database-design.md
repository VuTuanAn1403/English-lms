# THIẾT KẾ CƠ SỞ DỮ LIỆU (03-DATABASE-DESIGN)

> **Mô hình quy trình:** Thác Nước (Waterfall Lifecycle) - Pha 2 & 6: Thiết Kế & Quản Trị CSDL  
> **Dự án:** English LMS - Hệ thống quản lý học tập tiếng Anh trực tuyến  
> **Cơ sở dữ liệu:** PostgreSQL 16 • Quản lý phiên bản: Flyway Migrations (`V1..V14`)  
> **Phiên bản tài liệu:** 1.2 • Ngày: 17/09/2026

---

## 1. MÔ HÌNH DATABASE-PER-SERVICE
Để đảm bảo tính độc lập, khả năng mở rộng (NFR-07) và an toàn dữ liệu, English LMS phân chia ranh giới lưu trữ thành 3 cơ sở dữ liệu vật lý riêng biệt:

1. **`user_db` (User Service):** Lưu trữ thông tin tài khoản người dùng, mật khẩu đã mã hóa BCrypt, thông tin hồ sơ và vai trò người dùng (`STUDENT`, `ADMIN`).
2. **`course_db` (Course Service):** Lưu trữ thông tin khóa học, bài học, lượt ghi danh (enrollments), tiến độ hoàn thành bài học, đơn hàng và giao dịch thanh toán.
3. **`ai_db` (AI Service):** Lưu trữ lịch sử tương tác AI (Chat, Grammar Check, Quiz Generation) và quản lý phiên hội thoại (sessions).

---

## 2. SƠ ĐỒ THỰC THỂ LIÊN KẾT (ERD MERMAID DIAGRAM)

### 2.1. CSDL `course_db`
```mermaid
erDiagram
    COURSES ||--o{ LESSONS : "chứa (1..N)"
    COURSES ||--o{ ENROLLMENTS : "được ghi danh (1..N)"
    COURSES ||--o{ COURSE_ORDERS : "mua khóa học (1..N)"
    COURSE_ORDERS ||--o{ PAYMENT_TRANSACTIONS : "thực hiện giao dịch (1..N)"
    LESSONS ||--o{ LEARNING_PROGRESS : "theo dõi tiến độ (1..N)"

    COURSES {
        uuid id PK
        string title
        text description
        string level
        decimal price
        decimal sale_price
        string currency
        boolean is_free
        boolean published
        string image_url
        timestamp created_at
        timestamp updated_at
    }

    LESSONS {
        uuid id PK
        uuid course_id FK
        string title
        text content
        integer lesson_order
        integer duration
        string video_url
        string pdf_url
        boolean is_trial
        timestamp created_at
    }

    ENROLLMENTS {
        uuid id PK
        uuid user_id "Unique (user_id, course_id)"
        uuid course_id FK "Unique (user_id, course_id)"
        string student_email
        string student_name
        string status "ACTIVE, TRIAL, COMPLETED, IN_PROGRESS"
        integer progress
        integer completed_lessons
        integer total_lessons
        boolean completed
        timestamp enrolled_at
        timestamp completion_date
    }

    LEARNING_PROGRESS {
        uuid id PK
        uuid user_id
        uuid course_id
        uuid lesson_id FK
        boolean completed
        timestamp last_accessed_at
    }

    COURSE_ORDERS {
        uuid id PK
        uuid user_id
        uuid course_id FK
        string order_code
        decimal amount
        string currency
        string status "PENDING, PAID, FAILED, CANCELLED"
        string payment_gateway "VNPAY, MOCK"
        timestamp created_at
    }

    PAYMENT_TRANSACTIONS {
        uuid id PK
        uuid order_id FK
        string transaction_ref
        decimal amount
        string status "SUCCESS, FAILED"
        string gateway_response_code
        timestamp transaction_time
    }
```

### 2.2. CSDL `user_db` & `ai_db`
```mermaid
erDiagram
    USERS {
        uuid id PK
        string email UK
        string password "BCrypt Hash"
        string full_name
        string role "STUDENT, ADMIN"
        string status "ACTIVE, INACTIVE"
        string avatar
        timestamp created_at
        timestamp updated_at
    }

    CHAT_HISTORIES {
        uuid id PK
        string user_email "Indexed"
        uuid user_id
        string session_id "Indexed"
        string prompt_type "CHAT, GRAMMAR, QUIZ"
        text user_prompt
        text ai_response
        timestamp created_at "Indexed DESC"
    }
```

---

## 3. DANH SÁCH RÀNG BUỘC TOÀN VẸN & CHỈ MỤC HIỆU NĂNG (INDEXES & CONSTRAINTS)

### 3.1. Ràng buộc toàn vẹn (Integrity Constraints)
- **`uq_enrollments_user_course` (Flyway Migration V14):**
  ```sql
  ALTER TABLE enrollments ADD CONSTRAINT uq_enrollments_user_course UNIQUE (user_id, course_id);
  ```
  *Mục đích:* Ngăn chặn tuyệt đối việc một học viên bị tạo 2 bản ghi ghi danh trên cùng một khóa học khi gặp tình trạng gửi request đồng thời (Race Condition).
- **`uk_users_email`:**
  ```sql
  ALTER TABLE users ADD CONSTRAINT uk_users_email UNIQUE (email);
  ```
  *Mục đích:* Đảm bảo tính duy nhất của tài khoản định danh theo địa chỉ email.

### 3.2. Chỉ mục tối ưu hóa truy vấn (Performance Indexes - NFR-03)
- `idx_enrollments_user_course`: B-Tree index trên `(user_id, course_id)` phục vụ kiểm tra quyền học nhanh chóng.
- `idx_enrollments_email_course`: B-Tree index trên `(student_email, course_id)` phục vụ truy vấn ghi danh của học viên.
- `idx_learning_progress_user_course_lesson`: B-Tree index trên `(user_id, course_id, lesson_id)` phục vụ kiểm tra trạng thái hoàn thành bài học.
- `idx_chat_histories_user_session`: B-Tree index trên `(user_email, session_id, created_at DESC)` phục vụ trích xuất ngữ cảnh hội thoại AI.

---

## 4. CHÍNH SÁCH MIGRATION & JPA SCHEMA VALIDATION
1. **Flyway là nguồn sự thật duy nhất (Single Source of Truth):**
   - Mọi thay đổi schema đều phải tạo file migration mới với định dạng `V{version}__{description}.sql`.
   - Tuyệt đối không chỉnh sửa hoặc xóa các file migration cũ đã từng chạy ở bất kỳ môi trường nào.
2. **JPA ddl-auto Policy:**
   - Trong môi trường Docker Compose và Production: `spring.jpa.hibernate.ddl-auto = validate`.
   - Không sử dụng `ddl-auto = update` để tránh Hibernate tự ý sinh alter table làm sai lệch cấu trúc CSDL được kiểm soát bởi Flyway.
