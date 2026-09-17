# TÀI LIỆU API & DANH SÁCH ENDPOINTS (04-API)

---

## 1. TỔNG QUAN VÀ BASE URL

- **API Gateway Base URL**: `http://localhost:8080`
- **Định dạng dữ liệu**: `application/json` (UTF-8)
- **Header Xác thực**: `Authorization: Bearer <JWT_TOKEN>`

### Cấu trúc phản hồi chuẩn (Standard Response Schema)
```json
{
  "code": 1000,
  "message": "Thao tác thành công",
  "data": { ... }
}
```

### Cấu trúc phản hồi lỗi (Error Response Schema)
```json
{
  "code": 4001,
  "errorCode": "COURSE_PURCHASE_REQUIRED",
  "message": "Khóa học yêu cầu thanh toán để học từ bài 6 trở đi. Vui lòng mua khóa học.",
  "timestamp": "2026-08-10T22:00:00Z"
}
```

---

## 2. PHÂN LOẠI DANH SÁCH API ENDPOINTS

### 2.1. Nhóm API Xác thực & Tài khoản (Auth & User Service)
| Method | Endpoint Path | Quyền Truy Cập | Mô Tả |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/auth/register` | Public | Đăng ký tài khoản Học viên |
| `POST` | `/api/v1/auth/login` | Public | Đăng nhập & Lấy JWT Token |
| `GET` | `/api/v1/users/profile` | Authenticated | Lấy thông tin cá nhân (JWT principal) |
| `PUT` | `/api/v1/users/profile` | Authenticated | Cập nhật họ tên và avatar cá nhân |
| `PATCH` | `/api/v1/users/me/password` | Authenticated | Đổi mật khẩu tự phục vụ với BCrypt (FR-06) |
| `GET` | `/api/v1/users/count` | Admin (`ROLE_ADMIN`) | Đếm tổng số người dùng thực trong DB |
| `GET` | `/api/v1/users` | Admin (`ROLE_ADMIN`) | Danh sách người dùng phân trang & lọc |
| `POST` | `/api/v1/users` | Admin (`ROLE_ADMIN`) | Thêm tài khoản người dùng mới |
| `GET` | `/api/v1/users/{id}` | Admin (`ROLE_ADMIN`) | Xem chi tiết tài khoản theo ID |
| `PUT` | `/api/v1/users/{id}` | Admin (`ROLE_ADMIN`) | Cập nhật thông tin tài khoản |
| `PATCH` | `/api/v1/users/{id}/status` | Admin (`ROLE_ADMIN`) | Khóa hoặc mở khóa tài khoản |
| `PATCH` | `/api/v1/users/{id}/password` | Admin (`ROLE_ADMIN`) | Đặt lại mật khẩu người dùng |
| `DELETE` | `/api/v1/users/{id}` | Admin (`ROLE_ADMIN`) | Xóa tài khoản (chặn tự xóa tài khoản đang đăng nhập) |

### 2.2. Nhóm API Khóa học & Bài học (Course & Lesson Service)
| Method | Endpoint Path | Quyền Truy Cập | Mô Tả |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/courses` | Public | Danh sách khóa học public & lọc trình độ |
| `GET` | `/api/v1/courses/{id}` | Public | Chi tiết khóa học tổng quan |
| `GET` | `/api/v1/courses/{id}/lessons` | Public | Danh sách bài học outline (Summary DTO, không chứa Markdown/Media full) |
| `GET` | `/api/v1/lessons/{id}` | Authenticated | Chi tiết bài học (Full Markdown, Video Embed, PDF Link). Kiểm tra phân quyền Học thử / Mua khóa học. |
| `POST` | `/api/v1/courses` | Admin (`ROLE_ADMIN`) | Thêm khóa học mới |
| `PUT` | `/api/v1/courses/{id}` | Admin (`ROLE_ADMIN`) | Cập nhật thông tin khóa học |
| `DELETE` | `/api/v1/courses/{id}` | Admin (`ROLE_ADMIN`) | Xóa khóa học (Chặn nếu có bài học/học viên -> HTTP 409) |
| `POST` | `/api/v1/lessons` | Admin (`ROLE_ADMIN`) | Thêm bài học mới |
| `PUT` | `/api/v1/lessons/{id}` | Admin (`ROLE_ADMIN`) | Cập nhật bài học |
| `DELETE` | `/api/v1/lessons/{id}` | Admin (`ROLE_ADMIN`) | Xóa bài học |

### 2.3. Nhóm API Đăng ký & Tiến độ (Enrollment & Progress)
| Method | Endpoint Path | Quyền Truy Cập | Mô Tả |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/enrollments` | Authenticated | Đăng ký học phần / Học thử 5 bài đầu |
| `GET` | `/api/v1/enrollments/my-courses` | Authenticated | Danh sách khóa học học viên đã đăng ký |
| `POST` | `/api/v1/progress/complete` | Authenticated | Ghi nhận hoàn thành bài học |
| `GET` | `/api/v1/progress/course/{courseId}` | Authenticated | Tiến độ học tập cá nhân theo khóa học |

### 2.4. Nhóm API Thanh toán & Thống kê Doanh thu (Orders & Payment)
| Method | Endpoint Path | Quyền Truy Cập | Mô Tả |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/orders` | Authenticated | Tạo đơn hàng mua khóa học |
| `POST` | `/api/v1/payments/create` | Authenticated | Khởi tạo URL thanh toán (VNPay / Mock) |
| `GET` | `/api/v1/payments/vnpay-callback` | Public | Dynamic IPN/Callback xử lý kết quả VNPay |
| `GET` | `/api/v1/admin/revenue` | Admin (`ROLE_ADMIN`) | Báo cáo doanh thu & biểu đồ theo thời gian |
| `GET` | `/api/v1/admin/orders` | Admin (`ROLE_ADMIN`) | Quản lý danh sách đơn hàng phân trang |

### 2.5. Nhóm API Trợ lý AI Assistant (AI Service)
| Method | Endpoint Path | Quyền Truy Cập | Mô Tả |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/ai/chat` | Authenticated | Chat AI Assistant theo `sessionId` |
| `POST` | `/api/v1/ai/grammar-check` | Authenticated | Sửa lỗi ngữ pháp (JSON 8 trường) |
| `POST` | `/api/v1/ai/generate-quiz` | Authenticated | Sinh bài tập trắc nghiệm 4 lựa chọn |
| `GET` | `/api/v1/ai/history` | Authenticated | Lịch sử chat AI cá nhân của học viên |
| `DELETE` | `/api/v1/ai/sessions/{sessionId}` | Authenticated | Xóa lịch sử phiên hội thoại AI |
| `GET` | `/api/v1/ai/admin/history` | Admin (`ROLE_ADMIN`) | Quản lý toàn bộ lịch sử AI hệ thống |

---

## 3. VÍ DỤ REQUEST / RESPONSE CHI TIẾT

### 3.1. Sửa lỗi Ngữ pháp (Grammar Check Response Schema)
- **Endpoint**: `POST /api/v1/ai/grammar`
- **Request Body**:
  ```json
  {
    "text": "She go to school yesterday."
  }
  ```
- **Response 200 OK**:
  ```json
  {
    "code": 1000,
    "message": "Kiểm tra ngữ pháp thành công",
    "data": {
      "original": "She go to school yesterday.",
      "corrected": "She went to school yesterday.",
      "explanation": "Đoạn văn có lỗi chia thì quá khứ đơn. 'yesterday' chỉ quá khứ nên 'go' chuyển thành 'went'.",
      "errors": ["Chia sai thì động từ go thành went trong quá khứ"],
      "score": 7,
      "grammarRules": ["Thì quá khứ đơn (Past Simple Tense)"],
      "examples": ["He went to the market yesterday."],
      "tips": "Hãy ghi nhớ các trạng từ chỉ thời gian quá khứ như yesterday, last week."
    }
  }
  ```

### 3.2. Sinh Bài tập Trắc nghiệm (Quiz Generator Response Schema)
- **Endpoint**: `POST /api/v1/ai/quiz`
- **Request Body**:
  ```json
  {
    "lesson": "Present Simple Tense",
    "cefrLevel": "A2",
    "skillTarget": "Grammar",
    "difficulty": "Medium",
    "numberOfQuestions": 5
  }
  ```
- **Response 200 OK**:
  ```json
  {
    "code": 1000,
    "message": "Thành công",
    "data": [
      {
        "id": "q1",
        "question": "He ___ football every Sunday.",
        "options": ["play", "plays", "playing", "played"],
        "correctAnswer": 1,
        "explanation": "Chủ ngữ ngôi thứ ba số ít 'He' đi với động từ thêm s/es ở thì hiện tại đơn."
      }
    ]
  }
  ```

---

## 4. VÍ DỤ CURL MẪU (CURL EXAMPLES)

```bash
# 1. Đăng nhập lấy JWT Token
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"student@gmail.com", "password":"password123"}'

# 2. Xem danh sách Khóa học Public
curl -X GET http://localhost:8080/api/v1/courses

# 3. Lấy chi tiết bài học (Yêu cầu Token)
curl -X GET http://localhost:8080/api/v1/lessons/550e8400-e29b-41d4-a716-446655440010 \
  -H "Authorization: Bearer <YOUR_JWT_TOKEN>"
```
