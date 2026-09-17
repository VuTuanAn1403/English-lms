# QUY CHUẨN MÃ NGUỒN VÀ TIÊU CHUẨN PHÁT TRIỂN (05-CODING-STANDARD)

---

## 1. QUY CHUẨN ĐẶT TÊN VÀ CẤU TRÚC GÓI (PACKAGE & NAMING CONVENTIONS)

### 1.1. Backend Java Spring Boot
- **Package Base**: `com.englishlms.<service_name>` (Ví dụ: `com.englishlms.course`, `com.englishlms.user`, `com.englishlms.ai`).
- **Phân lớp chuẩn**:
  - `config`: Lớp cấu hình Spring Bean (`SecurityConfig`, `OpenApiConfig`).
  - `controller`: Lớp tiếp nhận REST API Request.
  - `dto`: Data Transfer Object chứa định dạng Request/Response.
  - `entity`: JPA Entity định nghĩa bảng Database.
  - `exception`: Lớp chứa Ngoại lệ tùy chỉnh & `GlobalExceptionHandler`.
  - `mapper`: Interface chuyển đổi giữa Entity và DTO (MapStruct).
  - `repository`: Spring Data JPA Interfaces.
  - `service` / `service.impl`: Interface & Class triển khai logic nghiệp vụ.

### 1.2. Quy tắc đặt tên Lớp & Phương thức
- Class Name: PascalCase (Ví dụ: `CourseServiceImpl`, `LessonResponse`).
- Method / Field Name: camelCase (Ví dụ: `getLessonsByCourseId`, `studentEmail`).
- Database Table Name: snake_case số nhiều (Ví dụ: `courses`, `learning_progress`).
- DB Column Name: snake_case (Ví dụ: `created_at`, `student_email`).

---

## 2. QUY CHUẨN XỬ LÝ LỖI & DTO VALIDATION (ERROR HANDLING)

### 2.1. DTO Validation Constraints
- Sử dụng Jakarta Validation Annotations (`@NotNull`, `@NotBlank`, `@Email`, `@Min`, `@Max`, `@Size`).
- Mọi Controller Request Body phải khai báo annotation `@Valid` trước DTO parameter.

### 2.2. Xử lý Lỗi Tập trung (Global Exception Handling)
- Tất cả microservices sử dụng `@RestControllerAdvice` định nghĩa `GlobalExceptionHandler`.
- Mọi ngoại lệ trả về Client phải tuân thủ chuẩn JSON Schema:
  ```json
  {
    "code": 4001,
    "errorCode": "COURSE_PURCHASE_REQUIRED",
    "message": "Thông báo lỗi tiếng Việt rõ ràng",
    "timestamp": "2026-08-10T22:00:00Z"
  }
  ```
- **Tuyệt đối KHÔNG che giấu lỗi bằng dữ liệu giả**: Nếu API lỗi hoặc bị từ chối quyền, giữ nguyên trạng thái lỗi và trả đúng HTTP Status Code (401, 403, 404, 409, 500).Không dùng `try-catch` nuốt exception để trả về dummy payload.

---

## 3. QUY CHUẨN BẢO MẬT & DATABASE MIGRATION

- **JWT Processing**:
  - Chữ ký JWT ký bằng HMAC-SHA256 (`Keys.hmacShaKeyFor`).
  - Không đọc `userId` từ Request Param hay Path Variable của client nếu đó là dữ liệu cá nhân nhạy cảm; luôn trích xuất `userId` chính thức từ JWT Token Claims.
- **Database Migration (Flyway)**:
  - Tất cả câu lệnh SQL trong Migration file phải dùng chữ HOA cho từ khóa SQL (`CREATE TABLE`, `ALTER TABLE`, `ADD COLUMN`).
  - Mỗi Migration file gắn nhãn phiên bản tăng dần `V1__...`, `V2__...`, `V13__...`.

---

## 4. FRONTEND CODING STANDARDS (REACT + VITE)

- **Cấu trúc thư mục**:
  - `components`: Các UI Component tái sử dụng (`CourseCard`, `ChatBox`, `Loading`).
  - `contexts`: React Context quản lý Global State (`AuthContext`).
  - `pages`: Các trang ứng dụng tương ứng với Route.
  - `services/api.js`: Axios Instance trung tâm tích hợp Interceptor và baseURL.
- **Quản lý Token**:
  - Axios Interceptor tự động gắn `Authorization: Bearer <token>` vào mọi Request.
  - Khi gặp phản hồi 401 Unauthorized, interceptor tự động xóa Token khỏi `localStorage` và điều hướng về `/login` một cách an toàn.
