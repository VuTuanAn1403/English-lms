# KẾ HOẠCH VÀ QUY TRÌNH THU HỒI PHIÊN BẢN (ROLLBACK PROCEDURE)
**Dự án:** English LMS – Hệ thống Quản lý Khóa học Tiếng Anh Trực tuyến Tích hợp AI  
**Mục tiêu:** Khôi phục nhanh chóng trạng thái ổn định của hệ thống khi phát sinh sự cố nghiêm trọng (Blocker/Critical) sau khi triển khai phiên bản mới  
**Phiên bản:** 1.2 • **Ngày cập nhật:** 17/09/2026  

---

## 1. TIÊU CHÍ KÍCH HOẠT QUY TRÌNH THU HỒI (ROLLBACK TRIGGER CRITERIA)

Quy trình thu hồi khẩn cấp (Rollback) được kích hoạt ngay lập tức khi xuất hiện một trong các dấu hiệu sau trong vòng 60 phút sau khi phát hành:
1. **Lỗi nghiêm trọng cấp độ 1 (Blocker):** Tỷ lệ lỗi 5xx vượt quá 5% trên toàn hệ thống hoặc API Gateway không thể định tuyến request.
2. **Lỗ hổng bảo mật khẩn cấp:** Rò rỉ thông tin cá nhân giữa các người dùng, bypass xác thực JWT, hoặc phát hiện secret bị lộ trên môi trường công khai.
3. **Mất mát toàn vẹn dữ liệu:** Cơ sở dữ liệu xảy ra lỗi xung đột nghiêm trọng, migration thất bại dẫn đến sập schema hoặc deadlock kéo dài.
4. **Trải nghiệm người dùng gián đoạn hoàn toàn:** Người dùng không thể đăng nhập, giao diện trên Vercel gặp sự cố crash trắng trang (White Screen of Death).

---

## 2. QUY TRÌNH THU HỒI FRONTEND TRÊN VERCEL (INSTANT ROLLBACK)

Nhờ kiến trúc bất biến của Vercel (Immutable Deployments), việc thu hồi frontend có thể thực hiện gần như tức thì (< 30 giây):

```
[ Gặp sự cố Production ] 
       |
       v
[ Vercel Project Dashboard ] -> [ Deployments Tab ]
       |
       v
[ Tìm phiên bản ổn định trước đó (Ví dụ: Deployment v1.1) ]
       |
       v
[ Chọn dấu 3 chấm (...) ] -> [ Instant Rollback / Assign to Production Domain ]
       |
       v
[ Hệ thống chuyển hướng 100% traffic về bản cũ - Hoàn tất trong 15s ]
```

**Thao tác kiểm tra sau khi rollback:**
1. Mở trình duyệt ẩn danh, truy cập URL chính thức của Vercel.
2. Kiểm tra mã SHA commit hoặc phiên bản hiển thị tại footer.
3. Xác nhận chức năng đăng nhập và duyệt khóa học hoạt động bình thường.

---

## 3. QUY TRÌNH THU HỒI BACKEND MICROSERVICES VÀ DOCKER COMPOSE

### 3.1. Thu hồi phiên bản mã nguồn và container
Nếu bản phát hành mới gây lỗi ở tầng dịch vụ backend:
```bash
# 1. Chuyển mã nguồn về commit hoặc Git Tag ổn định trước đó
git checkout v1.1.0

# 2. Khởi động lại các container với mã nguồn ổn định
docker compose down
docker compose up --build -d

# 3. Kiểm tra trạng thái sức khỏe
docker compose ps
./scripts/smoke-test.ps1 -GatewayUrl "http://localhost:8080"
```

### 3.2. Thu hồi lược đồ và phục hồi dữ liệu PostgreSQL
Nếu sự cố liên quan đến lỗi di chuyển dữ liệu (Flyway migration):
1. **Dừng toàn bộ dịch vụ backend** để ngăn ghi dữ liệu không nhất quán:
   ```bash
   docker compose stop api-gateway user-service course-service ai-service
   ```
2. **Khôi phục cơ sở dữ liệu từ bản sao lưu an toàn** được tạo trước khi phát hành:
   ```powershell
   ./scripts/restore-db.ps1 -BackupDir "./backups/pre-release-v1.2.0" -VerifySandbox
   ```
3. **Khởi động lại toàn bộ stack dịch vụ:**
   ```bash
   docker compose start
   ```

---

## 4. XỬ LÝ HẬU SỰ CỐ (POST-INCIDENT ACTIONS)

1. **Thông báo nội bộ:** Gửi thông báo đến toàn bộ các bên liên quan (Product Owner, Giảng viên, Học viên) về việc hệ thống đã khôi phục trạng thái ổn định.
2. **Mở Defect khẩn cấp (Hotfix Issue):** Ghi nhận chi tiết nguyên nhân gốc rễ (Root Cause Analysis - RCA), các log lỗi tại thời điểm xảy ra sự cố.
3. **Thực hiện quy trình Hotfix:** Tạo nhánh `hotfix/issue-description` từ nhánh `main` hiện tại, khắc phục lỗi, chạy kiểm thử hồi quy đầy đủ và chỉ phát hành lại khi Stage Gate được phê duyệt lại.
