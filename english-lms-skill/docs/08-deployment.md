# TÀI LIỆU HƯỚNG DẪN TRIỂN KHAI VÀ ĐÓNG GÓI (08-DEPLOYMENT)

---

## 1. TRIỂN KHAI BẰNG DOCKER COMPOSE

### 1.1. Luồng Build Multi-Stage
Mọi Microservice Java được biên dịch trực tiếp trong Docker Container qua 2 công đoạn:
1. **Stage 1 (Builder)**: Sử dụng Image `maven:3.9-eclipse-temurin-21-alpine` nạp toàn bộ mã nguồn multi-module và biên dịch file `.jar` bằng lệnh `mvn -pl <module> -am package -DskipTests`.
2. **Stage 2 (Runner)**: Copy duy nhất file `.jar` đã build sang Image `eclipse-temurin:21-jre-alpine` siêu nhẹ, chạy dưới quyền `appuser` (non-root) để tối ưu bảo mật.

### 1.2. Các bước triển khai
```bash
# 1. Chuyển vào thư mục mã nguồn
cd english-lms

# 2. Tạo file biến môi trường
cp .env.example .env

# 3. Khởi chạy toàn bộ hệ thống
docker compose up --build -d

# 4. Theo dõi log dịch vụ
docker compose logs -f
```

---

## 2. HƯỚNG DẪN BẢO TRÌ VÀ SAO LƯU DỮ LIỆU (BACKUP & RESTORE)

### 2.1. Sao lưu Cơ sở dữ liệu PostgreSQL (Backup)
```bash
# Backup database user_db
docker exec -t postgres-user pg_dump -U postgres user_db > backup_user_db.sql

# Backup database course_db
docker exec -t postgres-course pg_dump -U postgres course_db > backup_course_db.sql

# Backup database ai_db
docker exec -t postgres-ai pg_dump -U postgres ai_db > backup_ai_db.sql
```

### 2.2. Phục hồi Cơ sở dữ liệu (Restore)
```bash
docker exec -i postgres-course psql -U postgres -d course_db < backup_course_db.sql
```

---

## 3. XUẤT BẢN NỘP BÀI AN TOÀN (SUBMISSION PACKAGING)

Dự án cung cấp sẵn 2 script tự động đóng gói bản nộp bài, đảm bảo **loại trừ các file tạm/file nhạy cảm** (`.git`, `node_modules`, `target`, `dist`, `.env`, IDE files) mà **không làm mất hay xóa file mã nguồn**:

- **Trên Windows (PowerShell)**:
  ```powershell
  .\scripts\package-submission.ps1
  ```
- **Trên Linux / macOS (Bash)**:
  ```bash
  chmod +x ./scripts/package-submission.sh
  ./scripts/package-submission.sh
  ```
=> Tạo ra file zip `english-lms-submission.zip` sẵn sàng để nộp bài.
