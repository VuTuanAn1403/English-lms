# TÀI LIỆU CƠ SỞ DỮ LIỆU & MIGRATION (03-DATABASE)

---

## 1. SƠ ĐỒ THỰC THỂ NGUYÊN THỂ (MERMAID ERD DIAGRAM)

```mermaid
erDiagram
    users ||--o{ enrollments : "đăng ký"
    users ||--o{ course_orders : "đặt mua"
    users ||--o{ learning_progress : "thực hiện"
    users ||--o{ chat_histories : "tương tác AI"

    courses ||--o{ lessons : "chứa"
    courses ||--o{ enrollments : "có"
    courses ||--o{ course_orders : "được mua"

    course_orders ||--o{ payment_transactions : "thanh toán"
    lessons ||--o{ learning_progress : "được ghi nhận"

    users {
        uuid id PK
        string email UK
        string password
        string full_name
        string role
        string status
        timestamp created_at
    }

    courses {
        uuid id PK
        string title
        string description
        string level
        numeric price
        numeric sale_price
        string currency
        boolean published
        timestamp created_at
    }

    lessons {
        uuid id PK
        uuid course_id FK
        string title
        text content
        string video_url
        string pdf_url
        integer lesson_order
        timestamp created_at
    }

    enrollments {
        uuid id PK
        uuid user_id FK
        uuid course_id FK
        string student_email
        string student_name
        string status
        timestamp enrolled_at
    }

    course_orders {
        uuid id PK
        string order_code UK
        uuid user_id FK
        uuid course_id FK
        numeric amount
        string status
        timestamp created_at
    }

    payment_transactions {
        uuid id PK
        uuid order_id FK
        string transaction_code
        string payment_gateway
        numeric amount
        string status
        timestamp created_at
    }

    learning_progress {
        uuid id PK
        uuid user_id FK
        uuid course_id FK
        uuid lesson_id FK
        boolean completed
        timestamp completed_at
    }

    chat_histories {
        uuid id PK
        uuid user_id FK
        string session_id
        string prompt_type
        text user_prompt
        text ai_response
        timestamp created_at
    }
```

---

## 2. DANH SÁCH BẢNG CƠ SỞ DỮ LIỆU THEO MICROSERVICES

### 2.1. Database `user_db` (User Service)
- **Bảng `users`**:
  - `id` (UUID, Primary Key)
  - `email` (VARCHAR 255, Unique Key, Not Null)
  - `password` (VARCHAR 255, BCrypt Hashed)
  - `full_name` (VARCHAR 255)
  - `role` (VARCHAR 50, Giá trị: `ROLE_STUDENT`, `ROLE_ADMIN`)
  - `status` (VARCHAR 50, Giá trị: `ACTIVE`, `SUSPENDED`)
  - `created_at` (TIMESTAMP)

### 2.2. Database `course_db` (Course Service)
- **Bảng `courses`**:
  - `id` (UUID, Primary Key)
  - `title` (VARCHAR 255, Not Null)
  - `description` (TEXT)
  - `level` (VARCHAR 50, Chuẩn hóa: `BEGINNER`, `INTERMEDIATE`, `ADVANCED`)
  - `price` (DECIMAL 12,2, Mặc định `0.00`)
  - `sale_price` (DECIMAL 12,2)
  - `currency` (VARCHAR 10, Mặc định `VND`)
  - `published` (BOOLEAN, Mặc định `true`)

- **Bảng `lessons`**:
  - `id` (UUID, Primary Key)
  - `course_id` (UUID, Foreign Key tham chiếu `courses.id`)
  - `title` (VARCHAR 255, Not Null)
  - `content` (TEXT, Nội dung bài học Markdown)
  - `video_url` (VARCHAR 500, Link YouTube Embed)
  - `pdf_url` (VARCHAR 500, Link tài liệu PDF)
  - `document_url` (VARCHAR 500)
  - `lesson_order` (INTEGER, Thứ tự bài học 1, 2, 3...)

- **Bảng `enrollments`**:
  - `id` (UUID, Primary Key)
  - `user_id` (UUID, Foreign Key)
  - `course_id` (UUID, Foreign Key)
  - `student_email` (VARCHAR 255)
  - `student_name` (VARCHAR 255)
  - `status` (VARCHAR 50, Giá trị: `TRIAL`, `ACTIVE`, `LEGACY_FREE`, `EXPIRED`, `CANCELLED`)
  - **Index Hiệu năng (Migration V13)**:
    - `idx_enrollments_user_course` `(user_id, course_id)`
    - `idx_enrollments_email_course` `(student_email, course_id)`
    - `idx_enrollments_status` `(status)`

- **Bảng `course_orders`**:
  - `id` (UUID, Primary Key)
  - `order_code` (VARCHAR 100, Unique Key)
  - `user_id` (UUID, Foreign Key)
  - `course_id` (UUID, Foreign Key)
  - `amount` (DECIMAL 12,2)
  - `status` (VARCHAR 50, Giá trị: `PENDING`, `PAID`, `CANCELLED`, `EXPIRED`)

- **Bảng `payment_transactions`**:
  - `id` (UUID, Primary Key)
  - `order_id` (UUID, Foreign Key tham chiếu `course_orders.id`)
  - `transaction_code` (VARCHAR 100)
  - `payment_gateway` (VARCHAR 50, Giá trị: `VNPAY`, `MOCK`)
  - `amount` (DECIMAL 12,2)
  - `status` (VARCHAR 50, Giá trị: `SUCCESS`, `FAILED`)

- **Bảng `learning_progress`**:
  - `id` (UUID, Primary Key)
  - `user_id` (UUID, Foreign Key)
  - `course_id` (UUID, Foreign Key)
  - `lesson_id` (UUID, Foreign Key)
  - `completed` (BOOLEAN, Mặc định `true`)
  - **Index Hiệu năng (Migration V13)**:
    - `idx_learning_progress_user_course_lesson` `(user_id, course_id, lesson_id)`
    - `idx_learning_progress_completed` `(completed)`

### 2.3. Database `ai_db` (AI Service)
- **Bảng `chat_histories`**:
  - `id` (UUID, Primary Key)
  - `user_id` (UUID, Foreign Key)
  - `session_id` (VARCHAR 100, Mã phiên hội thoại)
  - `prompt_type` (VARCHAR 50, Giá trị: `CHAT`, `GRAMMAR`, `QUIZ`)
  - `user_prompt` (TEXT)
  - `ai_response` (TEXT)
  - `created_at` (TIMESTAMP)
  - **Index Hiệu năng (Migration V2)**:
    - `idx_chat_histories_user_session` `(user_id, session_id)`
    - `idx_chat_histories_lookup` `(user_id, session_id, prompt_type, created_at)`

---

## 3. QUY TẮC MIGRATION (FLYWAY MIGRATION POLICY)

- **Quy tắc Append-Only**: Tuyệt đối **KHÔNG** chỉnh sửa các file Migration đã thực thi trong lịch sử (`V1__...` đến `V10__...`).
- Tất cả các nâng cấp cấu trúc Database, chuẩn hóa dữ liệu hoặc bổ sung Index đều phải tạo file Migration mới có thứ tự tăng dần (Ví dụ: `V11__add_course_pricing_orders_payments.sql`, `V12__normalize_course_levels_and_media.sql`, `V13__add_performance_indexes.sql`).
