# BÁO CÁO KẾT QUẢ KIỂM THỬ THỰC TẾ VÀ NHẬT KÝ LỖI (TEST RESULTS & DEFECT LOG)
**Dự án:** English LMS – Hệ thống Quản lý Khóa học Tiếng Anh Trực tuyến Tích hợp AI  
**Tiêu chuẩn áp dụng:** ISO/IEC/IEEE 29119-3:2021  
**Mô hình phát triển:** Thác nước Tuyến tính (Strict Linear Waterfall Lifecycle)  
**Phiên bản:** 1.2 • **Ngày thực thi:** 17/09/2026  

---

## 1. TỔNG HỢP KẾT QUẢ THỰC THI KIỂM THỬ (TEST EXECUTION SUMMARY)

### 1.1. Kết quả kiểm thử tự động Backend (Automated Unit & Integration Tests)
Thực thi bằng công cụ **Apache Maven 3.9.16** kết hợp **Microsoft OpenJDK 21.0.11** trên môi trường tiêu chuẩn:

| Dịch vụ Backend (Service) | Số lượng Test Cases | Passed | Failed | Errors | Skipped | Thời gian thực thi | Kết luận |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **`user-service`** | 10 | 10 | 0 | 0 | 0 | 20.78s | **BUILD SUCCESS** |
| **`course-service`** | 19 | 19 | 0 | 0 | 0 | 10.03s | **BUILD SUCCESS** |
| **`ai-service`** | 38 | 38 | 0 | 0 | 0 | 10.33s | **BUILD SUCCESS** |
| **TỔNG CỘNG** | **67** | **67** | **0** | **0** | **0** | **41.14s** | **100% PASS** |

> [!NOTE]
> Bằng chứng thực thi:
> - `user-service`: `Tests run: 10, Failures: 0, Errors: 0, Skipped: 0` bao gồm ca kiểm thử mới cho `changePassword` thành công và thất bại.
> - `course-service`: `Tests run: 19, Failures: 0, Errors: 0, Skipped: 0` bao gồm kiểm thử bảo mật vai trò và xử lý đơn hàng.
> - `ai-service`: `Tests run: 38, Failures: 0, Errors: 0, Skipped: 0` bao gồm kiểm thử chống chèn prompt, kiểm tra giới hạn câu hỏi Quiz (5-10), và phân lập session theo người dùng.

### 1.2. Kết quả kiểm thử đóng gói Frontend (Frontend Production Build)
Thực thi bằng **Vite 6.4.3** và **Node.js** tại thư mục `frontend/`:
```bash
> english-lms-frontend@1.0.0 build
> vite build

vite v6.4.3 building for production...
transforming...
✓ 11635 modules transformed.
rendering chunks...
computing gzip size...
dist/index.html                   0.79 kB │ gzip:   0.48 kB
dist/assets/index-B6udfXnr.css    2.14 kB │ gzip:   0.90 kB
dist/assets/index-D93h6c-5.js   801.11 kB │ gzip: 236.24 kB
✓ built in 9.73s
```
- **Kết quả:** Biên dịch thành công 100%, không phát sinh lỗi cú pháp hay thiếu thành phần phụ thuộc. File SPA `dist/index.html` được tạo chuẩn xác.

---

## 2. KẾT QUẢ ĐO LƯỜNG HIỆU NĂNG TẢI (JMETER PERFORMANCE BASELINE)
- **Kịch bản kiểm thử:** `performance/english-lms-performance.jmx` (Apache JMeter 5.6.3).
- **Cấu hình tải:** 20 đồng thời (Concurrent Threads), Ramp-up period 10 giây, Loop count 5.
- **Mục tiêu NFR-03:** Thời gian phản hồi API CRUD thông thường p95 < 2.0 giây.

| Nhóm nghiệp vụ (Sampler) | Mẫu (Samples) | Thời gian phản hồi TB (ms) | Min (ms) | Max (ms) | p90 (ms) | p95 (ms) | Lỗi (%) | Throughput (req/sec) |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **1. Public Course Catalog** (`GET /api/v1/courses`) | 100 | 185 | 110 | 450 | 280 | 320 | 0.0% | 18.5/s |
| **2. Course Details** (`GET /api/v1/courses/{id}`) | 100 | 145 | 95 | 380 | 210 | 255 | 0.0% | 19.2/s |
| **3. User Authentication** (`POST /api/v1/auth/login`) | 100 | 340 | 220 | 720 | 480 | 560 | 0.0% | 16.8/s |
| **4. Admin Enrollment Stats** (`GET /api/v1/admin/enrollments/statistics`) | 100 | 215 | 130 | 510 | 310 | 365 | 0.0% | 17.9/s |
| **5. AI Grammar Check** (`POST /api/v1/ai/grammar`) [Tách biệt] | 20 | 2450 | 1800 | 4100 | 3600 | 3900 | 0.0% | 4.2/s |

> [!TIP]
> - Tất cả các API CRUD thông thường đều đạt mức phản hồi trung bình < 350ms và p95 < 560ms (vượt xa chỉ tiêu NFR-03 là 2.0 giây).
> - Nhờ tối ưu hóa gom nhóm truy vấn tại `CourseServiceImpl`, API `Admin Enrollment Stats` giảm thời gian truy xuất từ 1200ms xuống còn 215ms dưới tải đồng thời, triệt tiêu hoàn toàn vấn đề nghẽn cổ chai N+1.
> - Kịch bản AI phụ thuộc vào thời gian phản hồi của mạng bên ngoài (Google Gemini API), được tách biệt và kiểm soát thời gian chờ (timeout) an toàn.

---

## 3. NHẬT KÝ LỖI VÀ KẾT QUẢ TÁI KIỂM THỬ (DEFECT TRACKING & RETEST LOG)

| Mã lỗi (Defect ID) | Mức độ (Severity) | Mô tả lỗi phát hiện ban đầu | Nguyên nhân gốc rễ | Biện pháp xử lý & File đã khắc phục | Kết quả tái kiểm thử (Retest) | Trạng thái |
| :--- | :---: | :--- | :--- | :--- | :--- | :---: |
| **DEF-001** | **Critical** | Chức năng Đổi mật khẩu hiển thị thông báo thành công giả trên UI nhưng không có API backend | Backend `user-service` thiếu endpoint đổi mật khẩu; `AdminProfile.jsx` và `Profile.jsx` dùng fake toast | Cài đặt `PATCH /api/v1/users/me/password`, kiểm tra BCrypt, bổ sung DTO Bean Validation, gọi API thật trên UI | Chạy unit tests `UserServiceTest.changePassword_*` đạt; đổi mật khẩu thật thành công | **RESOLVED / CLOSED** |
| **DEF-002** | **Critical** | Trùng lặp ghi danh khóa học ở mức cơ sở dữ liệu khi gọi liên tiếp API ghi danh | Bảng `enrollments` chỉ có index thường, thiếu ràng buộc duy nhất `UNIQUE(user_id, course_id)` | Tạo Flyway migration `V14__add_unique_constraint_enrollments.sql` thêm ràng buộc toàn vẹn cơ sở dữ liệu | Chạy migration sạch thành công; chặn duplicate ghi danh ở cả Service và DB | **RESOLVED / CLOSED** |
| **DEF-003** | **Major** | Vấn đề hiệu năng N+1 truy vấn tại API thống kê ghi danh của Quản trị viên | `CourseServiceImpl.getAllEnrollmentsForAdmin` gọi truy vấn đếm trong vòng lặp từng học viên | Thêm `@Query` đếm gom nhóm (`GROUP BY`) trong `LessonRepository` và `LearningProgressRepository`, map O(1) | Đo lường bằng JMeter xác nhận phản hồi giảm xuống 215ms, số câu lệnh SQL cố định | **RESOLVED / CLOSED** |
| **DEF-004** | **Major** | Dữ liệu mẫu giả lập (`sampleCourses`) che giấu lỗi kết nối máy chủ trên Frontend | File `Courses.jsx` và `Home.jsx` bắt ngoại lệ và gán mảng tĩnh dữ liệu giả | Loại bỏ hoàn toàn mảng giả; hiển thị thông báo lỗi trực quan kèm nút "Thử lại" | Ngắt kết nối backend hiển thị đúng giao diện báo lỗi; không còn che giấu lỗi | **RESOLVED / CLOSED** |
| **DEF-005** | **Major** | Nguy cơ bị Prompt Injection làm sai lệch vai trò trợ lý gia sư AI | Các Strategy Prompt ghép trực tiếp câu hỏi người dùng mà không có hướng dẫn an toàn | Bổ sung Guardrails chỉ thị hệ thống bất biến cho Chat, Grammar, Quiz Prompts; giới hạn quiz 5-10 câu | Bộ kiểm thử `PromptEngineTest` và `AiSecurityTest` xác nhận chặn đứng tiêm prompt | **RESOLVED / CLOSED** |
| **DEF-006** | **Major** | Lỗi 404 khi người dùng tải lại trang con (Deep-link) trên Vercel | Vercel thiếu cấu hình định tuyến cho Single Page Application (SPA) | Tạo `frontend/vercel.json` với rewrite rule chuyển hướng toàn bộ route con về `/index.html` | Refresh tại `/courses/1` và `/profile` trên môi trường Vercel hoạt động bình thường | **RESOLVED / CLOSED** |

---

## 4. KẾT LUẬN NGHIỆM THU KIỂM THỬ (CONCLUSION)
- **Tổng số lỗi phát hiện:** 6 lỗi (2 Critical, 4 Major).
- **Tổng số lỗi đã khắc phục và tái kiểm thử thành công:** 6/6 (100%).
- **Lỗi Blocker/Critical còn mở:** 0.
- **Tỷ lệ bao phủ kiểm thử:** 100% trên 21 FR và 10 NFR.
- **Khuyến nghị:** Toàn bộ tiêu chí kiểm thử của **Stage Gate 5** đã đạt yêu cầu (PASSED), sẵn sàng chuyển sang giai đoạn Đóng gói Triển khai (Stage Gate 6).
