# HƯỚNG DẪN TRIỂN KHAI VÀ VẬN HÀNH HỆ THỐNG (SYSTEM DEPLOYMENT GUIDE)
**Dự án:** English LMS – Hệ thống Quản lý Khóa học Tiếng Anh Trực tuyến Tích hợp AI  
**Mô hình triển khai:** Kiến trúc lai (Hybrid Deployment Architecture): Frontend trên Vercel + Backend Microservices trên Docker Compose / VPS  
**Phiên bản:** 1.2 • **Ngày cập nhật:** 17/09/2026  

---

## 1. TỔNG QUAN KIẾN TRÚC TRIỂN KHAI (DEPLOYMENT ARCHITECTURE)

```
                       [ TRÌNH DUYỆT NGƯỜI DÙNG ]
                                  |
                                  | HTTPS (Public Internet)
                                  v
                +------------------------------------+
                |   VERCEL EDGE NETWORK (Frontend)   |
                |   React 18 + Vite 6 (SPA Hosting)  |
                +------------------------------------+
                                  |
                                  | HTTPS API Requests
                                  v
+-------------------------------------------------------------------------+
|                  HẠ TẦNG BACKEND (DOCKER COMPOSE / VPS)                 |
|                                                                         |
|   +-----------------------------------------------------------------+   |
|   |                  SPRING CLOUD API GATEWAY (Port 8080)           |   |
|   |     - Routing, JWT Verification, Stripping Client Identity      |   |
|   +-----------------------------------------------------------------+   |
|            |                        |                       |           |
|            v                        v                       v           |
|   +-----------------+      +-----------------+     +----------------+   |
|   |  USER SERVICE   |      | COURSE SERVICE  |     |   AI SERVICE   |   |
|   |   (Port 8081)   |      |   (Port 8082)   |     |  (Port 8083)   |   |
|   +-----------------+      +-----------------+     +----------------+   |
|            |                        |                       |           |
|            v                        v                       v           |
|   +-----------------------------------------------------------------+   |
|   |               POSTGRESQL 16 CLUSTER (Database-per-Service)      |   |
|   |         user_db (5432) | course_db (5432) | ai_db (5432)        |   |
|   +-----------------------------------------------------------------+   |
+-------------------------------------------------------------------------+
```

---

## 2. YÊU CẦU TIÊN QUYẾT (PREREQUISITES)

### 2.1. Yêu cầu môi trường máy chủ
- **Hệ điều hành:** Linux (Ubuntu 22.04 LTS / Debian 12) hoặc Windows Server / Windows 11 với Docker Desktop.
- **Phần mềm tối thiểu:**
  - Docker Engine 24.0+ và Docker Compose v2.20+
  - Node.js 20.x LTS và npm 10.x (để build frontend local nếu cần)
  - OpenJDK 21 LTS (nếu build native không qua Docker)
- **Cấu hình phần cứng tối thiểu:**
  - CPU: 2 Cores
  - RAM: 4 GB (khuyến nghị 8 GB để chạy đầy đủ stack microservices)
  - Bộ nhớ lưu trữ: Tối thiểu 10 GB SSD trống.

---

## 3. MA TRẬN BIẾN MÔI TRƯỜNG (ENVIRONMENT VARIABLES MATRIX)

> [!CAUTION]
> Tuyệt đối không commit các file `.env` chứa mật khẩu hoặc khóa bí mật thực tế vào Git. Sử dụng `.env.example` làm mẫu cấu hình.

### 3.1. Biến môi trường Backend (Chỉ lưu trên Server / Docker / GitHub Secrets)
| Tên biến môi trường | Giá trị mẫu / Định dạng | Mục đích | Mức độ nhạy cảm |
| :--- | :--- | :--- | :---: |
| `POSTGRES_USER` | `postgres` | Tài khoản quản trị PostgreSQL | Bảo mật |
| `POSTGRES_PASSWORD` | `your_strong_postgres_password` | Mật khẩu cơ sở dữ liệu PostgreSQL | **Rất nhạy cảm** |
| `JWT_SECRET` | `base64_or_hex_string_min_256_bits` | Khóa bí mật dùng để ký và xác thực JWT | **Rất nhạy cảm** |
| `GEMINI_API_KEY` | `AIzaSy...` | Khóa API dịch vụ Google Gemini AI | **Rất nhạy cảm** |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | `http://discovery-server:8761/eureka/` | Địa chỉ đăng ký dịch vụ Eureka | Nội bộ |

### 3.2. Biến môi trường Frontend (Cấu hình trên Vercel Project Settings)
| Tên biến môi trường | Giá trị Preview | Giá trị Production | Ghi chú |
| :--- | :--- | :--- | :--- |
| `VITE_API_BASE_URL` | `https://api-staging.yourdomain.com` | `https://api.yourdomain.com` | URL công khai của API Gateway (HTTPS). Không bao giờ trỏ tới localhost trên production. |
| `VITE_APP_NAME` | `English LMS (Staging)` | `English LMS` | Tên hiển thị của ứng dụng |

---

## 4. QUY TRÌNH TRIỂN KHAI BACKEND VỚI DOCKER COMPOSE

### 4.1. Chuẩn bị file cấu hình môi trường
Tạo file `.env` tại thư mục gốc từ file mẫu:
```bash
cp .env.example .env
# Chỉnh sửa các giá trị bí mật trong .env bằng nano hoặc vim
```

### 4.2. Xây dựng và khởi chạy các dịch vụ
Sử dụng multi-stage build để biên dịch mã nguồn trực tiếp trong container mà không cần file `.jar` có sẵn trên máy host:
```bash
# Xây dựng các images từ mã nguồn sạch và khởi chạy ngầm
docker compose up --build -d
```

### 4.3. Kiểm tra trạng thái hoạt động (Healthcheck)
Kiểm tra xem toàn bộ các container đã khởi động ở trạng thái healthy:
```bash
docker compose ps
```
Thứ tự khởi động tự động:
1. `postgres` (healthy sau khi database sẵn sàng)
2. `discovery-server` (Eureka)
3. `config-server` (cung cấp cấu hình cho microservices)
4. `api-gateway`, `user-service`, `course-service`, `ai-service`

### 4.4. Thực thi kịch bản kiểm tra nhanh (Smoke Test)
Chạy script kiểm tra sức khỏe và chống giả mạo quyền hạn tự động:
```powershell
./scripts/smoke-test.ps1 -GatewayUrl "http://localhost:8080"
```
Kết quả mong đợi:
- Toàn bộ endpoint Actuator Health trả về `UP`.
- Kiểm tra Header Spoofing trực tiếp trả về `HTTP 401/403` (Bảo mật thành công).

---

## 5. QUY TRÌNH TRIỂN KHAI FRONTEND TRÊN VERCEL

### 5.1. Cấu hình dự án trên Vercel
1. Đăng nhập vào [Vercel Dashboard](https://vercel.com).
2. Chọn **Add New Project** -> Chọn repository GitHub `VuTuanAn1403/English-lms`.
3. Trong phần **Project Settings**:
   - **Framework Preset:** `Vite`
   - **Root Directory:** `frontend` (Bắt buộc chỉ định đúng thư mục frontend)
   - **Build Command:** `npm run build`
   - **Output Directory:** `dist`
   - **Install Command:** `npm ci`
4. Trong phần **Environment Variables**:
   - Thêm `VITE_API_BASE_URL`: Điền URL HTTPS công khai của API Gateway.
   - Thêm `VITE_APP_NAME`: `English LMS`.

### 5.2. Cấu hình Single Page Application (SPA) Routing
File `frontend/vercel.json` đã được tạo sẵn để xử lý định tuyến phía client:
```json
{
  "$schema": "https://openapi.vercel.sh/vercel.json",
  "rewrites": [
    {
      "source": "/(.*)",
      "destination": "/index.html"
    }
  ]
}
```
Cấu hình này đảm bảo khi người dùng truy cập trực tiếp hoặc tải lại (F5) các trang con như `/courses/1`, `/profile`, `/admin/users`, máy chủ Vercel sẽ trả về `index.html` để React Router xử lý thay vì báo lỗi HTTP 404.

---

## 6. QUY TRÌNH SAO LƯU VÀ PHỤC HỒI DỮ LIỆU (BACKUP & RESTORE)

Hệ thống cung cấp sẵn các công cụ tự động hóa trong thư mục `scripts/`:
- **Sao lưu toàn bộ cơ sở dữ liệu:**
  ```powershell
  ./scripts/backup-db.ps1
  ```
  Tập lệnh sẽ trích xuất 3 tệp tin SQL độc lập: `user_db_*.sql`, `course_db_*.sql`, `ai_db_*.sql` vào thư mục `backups/`.
- **Phục hồi cơ sở dữ liệu:**
  ```powershell
  ./scripts/restore-db.ps1 -BackupDir "./backups" -VerifySandbox
  ```
  Tập lệnh hỗ trợ tham số `-VerifySandbox` để tự động kiểm tra tính toàn vẹn của dữ liệu mẫu sau khi nạp lại.
- Xem chi tiết tại: [docs/backup-restore.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/backup-restore.md).

---

## 7. XỬ LÝ SỰ CỐ VÀ KHẮC PHỤC LỖI THƯỜNG GẶP (TROUBLESHOOTING)

| Hiện tượng | Nguyên nhân có thể | Cách khắc phục |
| :--- | :--- | :--- |
| **Vercel mở được nhưng báo lỗi mạng khi gọi API** | `VITE_API_BASE_URL` trỏ tới `localhost` hoặc HTTP chưa bảo mật | Đổi biến môi trường trên Vercel thành URL HTTPS công khai, kích hoạt Redeploy |
| **Lỗi 404 khi F5 trang con trên Vercel** | Thiếu file `frontend/vercel.json` rewrite | Kiểm tra file `frontend/vercel.json` đã được push lên GitHub |
| **Lỗi CORS khi gọi từ Vercel tới Gateway** | Gateway chưa cho phép domain `*.vercel.app` | Cấu hình `allowed-origins` trong cấu hình Spring Cloud Gateway khớp với domain Vercel |
| **Database container không khởi động** | Trùng cổng 5432 với PostgreSQL cài cục bộ | Dừng dịch vụ PostgreSQL cục bộ hoặc đổi cổng ánh xạ host trong `docker-compose.yml` |
| **Lỗi Flyway Checksum Mismatch** | Đã sửa đổi file migration cũ đã từng thực thi | Không sửa migration cũ; tạo migration mới (ví dụ V14) để cập nhật lược đồ |
