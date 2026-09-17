# HƯỚNG DẪN QUY TRÌNH SAO LƯU VÀ PHỤC HỒI CƠ SỞ DỮ LIỆU (BACKUP & RESTORE)

> **Mã định danh:** NFR-08 (Tính toàn vẹn dữ liệu & Phục hồi sau thảm họa)  
> **Áp dụng cho:** PostgreSQL (Multi-database: `user_db`, `course_db`, `ai_db`)  
> **Phiên bản tài liệu:** 1.2

---

## 1. TỔNG QUAN CHIẾN LƯỢC SAO LƯU (BACKUP STRATEGY)

Hệ thống English LMS áp dụng mô hình cơ sở dữ liệu riêng biệt theo từng microservice (Database-per-Service):
- `user_db`: Chứa bảng `users` (thông tin định danh, mật khẩu băm BCrypt, vai trò, avatar).
- `course_db`: Chứa bảng `courses`, `lessons`, `enrollments`, `learning_progress`, `course_orders`, `payment_transactions`.
- `ai_db`: Chứa bảng `chat_histories`, `chat_sessions`.

Mọi thay đổi cấu trúc dữ liệu được quản lý tập trung và versioning thông qua công cụ **Flyway Migrations**. Quy trình sao lưu dữ liệu logic (Logical Backup) được thực hiện định kỳ bằng công cụ `pg_dump` tạo ra các file SQL script độc lập, đảm bảo khả năng tái tạo dữ liệu trên môi trường mới một cách hoàn chỉnh.

---

## 2. QUY TRÌNH THỰC HIỆN SAO LƯU (BACKUP PROCEDURE)

### 2.1. Sao lưu tự động bằng PowerShell Script
Chạy script tại thư mục gốc của dự án:
```powershell
.\scripts\backup-db.ps1
```

Script sẽ tự động:
1. Tạo thư mục `backups/` nếu chưa tồn tại.
2. Kiểm tra container Docker `english-lms-postgres` hoặc kết nối trực tiếp đến PostgreSQL tại `localhost:5432`.
3. Lần lượt dump 3 database `user_db`, `course_db`, `ai_db` ra file dạng:
   - `backups/user_db_backup_YYYYMMDD_HHMMSS.sql`
   - `backups/course_db_backup_YYYYMMDD_HHMMSS.sql`
   - `backups/ai_db_backup_YYYYMMDD_HHMMSS.sql`
4. Kiểm tra kích thước file (file size > 0 KB) và thông báo kết quả.

### 2.2. Lệnh sao lưu thủ công qua Docker Compose
```bash
# Sao lưu user_db
docker exec -t english-lms-postgres pg_dump -U postgres -d user_db --clean --if-exists > backups/user_db.sql

# Sao lưu course_db
docker exec -t english-lms-postgres pg_dump -U postgres -d course_db --clean --if-exists > backups/course_db.sql

# Sao lưu ai_db
docker exec -t english-lms-postgres pg_dump -U postgres -d ai_db --clean --if-exists > backups/ai_db.sql
```

---

## 3. QUY TRÌNH PHỤC HỒI DỮ LIỆU (RESTORE PROCEDURE)

### 3.1. Phục hồi tự động bằng PowerShell Script
```powershell
# Tự động phục hồi file sao lưu mới nhất
.\scripts\restore-db.ps1

# Hoặc chỉ định rõ file sao lưu và CSDL đích:
.\scripts\restore-db.ps1 -BackupFile ".\backups\course_db_backup_20260917_233000.sql" -TargetDb "course_db"
```

### 3.2. Lệnh phục hồi thủ công
```bash
# Phục hồi vào container Docker
Get-Content backups/user_db.sql | docker exec -i english-lms-postgres psql -U postgres -d user_db
Get-Content backups/course_db.sql | docker exec -i english-lms-postgres psql -U postgres -d course_db
Get-Content backups/ai_db.sql | docker exec -i english-lms-postgres psql -U postgres -d ai_db
```

---

## 4. QUY TRÌNH XÁC MINH PHỤC HỒI TRÊN MÔI TRƯỜNG SANDBOX (VERIFICATION PROCEDURE)

Để đảm bảo file backup có giá trị và không bị lỗi dữ liệu ngầm, quy trình nghiệm thu yêu cầu thực hiện xác minh sandbox theo các bước sau:

1. **Tạo CSDL Sandbox kiểm thử:**
   ```sql
   CREATE DATABASE sandbox_course_db WITH TEMPLATE template0;
   ```
2. **Nạp file backup vào Sandbox:**
   ```bash
   Get-Content backups/course_db_backup_latest.sql | docker exec -i english-lms-postgres psql -U postgres -d sandbox_course_db
   ```
3. **Kiểm tra số lượng bản ghi và ràng buộc:**
   ```sql
   -- Đếm số lượng khóa học và bài học
   SELECT count(*) FROM courses;
   SELECT count(*) FROM lessons;
   
   -- Kiểm tra tính toàn vẹn ràng buộc Unique Enrollments (V14)
   SELECT user_id, course_id, count(*) 
   FROM enrollments 
   GROUP BY user_id, course_id 
   HAVING count(*) > 1;
   -- Kết quả mong đợi: 0 rows (Không có bản ghi trùng lặp)
   ```
4. **Dọn dẹp CSDL Sandbox sau khi hoàn tất kiểm tra:**
   ```sql
   DROP DATABASE sandbox_course_db;
   ```

---

## 5. KẾT LUẬN & ĐÁNH GIÁ ĐẠT TIÊU CHUẨN NFR-08
- [x] Quy trình sao lưu không làm gián đoạn các dịch vụ đang chạy.
- [x] Script tự động nhận diện môi trường Docker Container và môi trường Host.
- [x] Đã thử nghiệm phục hồi và đối chiếu dữ liệu mẫu thành công.
