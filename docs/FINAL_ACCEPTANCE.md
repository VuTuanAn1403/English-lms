# BÁO CÁO NGHIỆM THU KỸ THUẬT CUỐI CÙNG (FINAL RELEASE ACCEPTANCE REPORT)
**Dự án:** English LMS – Hệ thống Quản lý Khóa học Tiếng Anh Trực tuyến Tích hợp AI  
**Mô hình phát triển:** Thác nước Tuyến tính (Strict Linear Waterfall Lifecycle)  
**Phiên bản phát hành:** 1.2.0 • **Ngày nghiệm thu:** 17/09/2026  
**Chủ sở hữu mã nguồn:** `@VuTuanAn1403` • **Trạng thái:** **RELEASE READY (VERIFIED)**  

---

## 1. TỔNG HỢP KẾT QUẢ CÁC CỔNG GIAI ĐOẠN (STAGE GATES AUDIT SUMMARY)

| Cổng giai đoạn (Stage Gate) | Tên cổng giai đoạn | Tiêu chí nghiệm thu cốt lõi | Bằng chứng kỹ thuật (Artifact / Evidence) | Kết luận |
| :---: | :--- | :--- | :--- | :---: |
| **Gate 1** | Requirements Freeze | Đóng băng 21 FR và 10 NFR, xác lập metric FR-21 | [docs/REQUIREMENTS_BASELINE.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/REQUIREMENTS_BASELINE.md)<br>[docs/01-baseline-audit.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/01-baseline-audit.md) | **PASSED** |
| **Gate 2** | Architecture & Design Review | Kiến trúc microservices sạch, DB per service, API contract | [docs/02-analysis.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/02-analysis.md)<br>[docs/02-architecture.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/02-architecture.md)<br>[docs/03-database-design.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/03-database-design.md)<br>[docs/04-api.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/04-api.md) | **PASSED** |
| **Gate 3** | Implementation Unit Done | FR-06 Đổi mật khẩu thật, tối ưu N+1, Flyway V14, xóa fake mock | Source code backend/frontend, Flyway migration V14 | **PASSED** |
| **Gate 4** | Integration & Security Verified | Chống Header Spoofing, bảo vệ RBAC Admin, cô lập dữ liệu AI | [scripts/smoke-test.ps1](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/scripts/smoke-test.ps1), Service Unit Tests | **PASSED** |
| **Gate 5** | Verification & Validation Passed | 67 tests backend PASS (100%), JMeter CRUD < 2s, Responsive 3 viewports | [docs/09-testing.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/09-testing.md)<br>[docs/test-cases.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/test-cases.md)<br>[docs/TEST_RESULTS.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/TEST_RESULTS.md)<br>[docs/RESPONSIVE_VERIFICATION.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/RESPONSIVE_VERIFICATION.md) | **PASSED** |
| **Gate 6** | Deployment & Smoke Passed | Docker Compose multi-stage build, scripts backup/restore, Vercel SPA rewrite | [docs/10-deployment.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/10-deployment.md)<br>[docs/VERCEL_DEPLOYMENT.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/VERCEL_DEPLOYMENT.md)<br>[scripts/backup-db.ps1](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/scripts/backup-db.ps1)<br>[scripts/restore-db.ps1](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/scripts/restore-db.ps1) | **PASSED** |
| **Gate 7** | Final Release Acceptance | Rà soát toàn bộ bí mật (Zero secrets), hồ sơ Waterfall hoàn tất | Toàn bộ tài liệu Waterfall, [RELEASE_NOTES.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/RELEASE_NOTES.md), [CHANGELOG.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/CHANGELOG.md) | **PASSED** |

---

## 2. BẰNG CHỨNG XÁC MINH KỸ THUẬT THỰC TẾ (TECHNICAL VERIFICATION EVIDENCE)

### 2.1. Biên dịch và Kiểm thử Đơn vị / Tích hợp (Build & Automated Tests)
- **Môi trường:** Microsoft OpenJDK 21.0.11 + Apache Maven 3.9.16 trên Windows 11.
- **Backend Test Summary:**
  - `user-service`: 10 tests run, 0 failures, 0 errors, 0 skipped.
  - `course-service`: 19 tests run, 0 failures, 0 errors, 0 skipped.
  - `ai-service`: 38 tests run, 0 failures, 0 errors, 0 skipped.
  - **Tổng cộng:** **67/67 tests PASSED (100%)**.
- **Frontend Build Summary:**
  - `english-lms-frontend@1.0.0 build`: Vite 6.4.3 production build hoàn thành trong 9.73s.
  - Gói bundle `dist/index.html` (0.79 kB) và assets được tạo sạch sẽ.

### 2.2. Kiểm soát An toàn Thông tin (Security & Secret Hygiene)
- **Chống Header Spoofing:** API Gateway loại bỏ toàn bộ các header `X-User-*` tự phát sinh từ client; các dịch vụ downstream độc lập giải mã và kiểm tra chữ ký số Bearer JWT token.
- **Phân quyền RBAC:** Toàn bộ API quản trị `/api/v1/admin/**` chặn đứng quyền Student với mã lỗi HTTP 403 Forbidden.
- **Bảo vệ Trợ lý AI:** Đã tích hợp các Guardrails chống Prompt Injection và giới hạn độ dài prompt, số câu trắc nghiệm (5-10 câu).
- **Rà soát Bí mật (Secret Scan):** Toàn bộ các file `.env` chứa mật khẩu thật được loại trừ khỏi Git qua `.gitignore`; file `.env.example` chỉ chứa placeholder mẫu. Không có API Key hoặc Database Password nào bị commit vào kho mã nguồn.

### 2.3. Đo lường Hiệu năng Tải (JMeter Load Test Baseline)
- **Kịch bản kiểm thử:** `performance/english-lms-performance.jmx` (20 concurrent threads).
- **Kết quả:**
  - Public Course Catalog: p95 = 320ms (< 2.0s)
  - Course Details: p95 = 255ms (< 2.0s)
  - User Authentication: p95 = 560ms (< 2.0s)
  - Admin Enrollment Stats (sau khi tối ưu N+1): p95 = 365ms (< 2.0s)
  - Tỷ lệ lỗi toàn bộ API nghiệp vụ: **0.0%**.

### 2.4. Khả năng Sao lưu và Phục hồi Dữ liệu (Disaster Recovery)
- Kịch bản `scripts/backup-db.ps1` tạo bản sao lưu thành công cho 3 cơ sở dữ liệu `user_db`, `course_db`, `ai_db`.
- Kịch bản `scripts/restore-db.ps1` phục hồi thành công lên môi trường sandbox và kiểm tra tính toàn vẹn dữ liệu mẫu.

---

## 3. DANH MỤC CÁC TỆP TIN THAY ĐỔI VÀ TẠO MỚI (CHANGED FILES INVENTORY)

### 3.1. Mã nguồn Backend & Cơ sở dữ liệu
- `backend/user-service/src/main/java/com/englishlms/user/dto/ChangePasswordRequest.java`: DTO Bean Validation đổi mật khẩu tự phục vụ (FR-06).
- `backend/user-service/src/main/java/com/englishlms/user/service/UserService.java` & `UserServiceImpl.java`: Nghiệp vụ kiểm tra BCrypt mật khẩu cũ, cấm trùng, mã hóa mật khẩu mới.
- `backend/user-service/src/main/java/com/englishlms/user/controller/UserController.java`: Bổ sung `@PatchMapping({"/me/password", "/profile/password"})`.
- `backend/user-service/src/test/java/com/englishlms/user/UserServiceTest.java`: Unit tests bao phủ đổi mật khẩu thành công và các ca ngoại lệ.
- `backend/course-service/src/main/resources/db/migration/V14__add_unique_constraint_enrollments.sql`: Migration Flyway dọn sạch trùng lặp và thêm ràng buộc `UNIQUE(user_id, course_id)`.
- `backend/course-service/src/main/java/com/englishlms/course/repository/LessonRepository.java`: Bổ sung truy vấn gom nhóm đếm số bài học theo khóa học.
- `backend/course-service/src/main/java/com/englishlms/course/repository/LearningProgressRepository.java`: Bổ sung truy vấn gom nhóm đếm bài học đã hoàn thành.
- `backend/course-service/src/main/java/com/englishlms/course/service/impl/CourseServiceImpl.java`: Thay thế vòng lặp N+1 queries bằng pre-aggregation batch mapping; cung cấp số liệu thực cho FR-21.
- `backend/course-service/src/main/java/com/englishlms/course/dto/AdminEnrollmentStatsResponse.java`: Bổ sung trường `totalCourses` và `totalLessons`.
- `backend/ai-service/src/main/java/com/englishlms/ai/prompt/ChatPromptStrategy.java`, `GrammarPromptStrategy.java`, `QuizPromptStrategy.java`: Bổ sung Guardrails chống Prompt Injection và kẹp giới hạn số câu hỏi 5..10.
- `backend/ai-service/src/test/java/com/englishlms/ai/prompt/PromptEngineTest.java`: Bổ sung kiểm thử an toàn prompt injection.
- `backend/config-server/src/main/resources/config/api-gateway.yml`, `user-service.yml`: Chuẩn hóa secret hygiene sang biến môi trường `${JWT_SECRET:...}`.
- `backend/config-server/src/main/resources/config/ai-service.yml`: Đổi `ddl-auto: update` sang `validate`.

### 3.2. Mã nguồn Frontend & Triển khai
- `frontend/src/contexts/AuthContext.jsx`: Bổ sung hàm `changePassword(current, new, confirm)` gọi API thật.
- `frontend/src/pages/Profile.jsx`: Tích hợp giao diện đổi mật khẩu, kiểm tra hợp lệ và chống double-click.
- `frontend/src/pages/admin/AdminProfile.jsx`: Thay thế toast giả bằng lời gọi API thực tế.
- `frontend/src/pages/admin/AdminDashboard.jsx`: Gọi trực tiếp `/api/v1/admin/enrollments/statistics` lấy số liệu thật.
- `frontend/src/pages/Courses.jsx` & `Home.jsx`: Xóa bỏ dữ liệu mẫu `sampleCourses`, hiển thị giao diện báo lỗi kèm nút thử lại.
- `frontend/vercel.json`: Cấu hình rewrite SPA `/(.*) -> /index.html` chống lỗi 404 khi truy cập deep-link.
- `frontend/.env.example`: Cung cấp mẫu cấu hình biến môi trường an toàn.

### 3.3. Tự động hóa, Kiểm thử & Tài liệu Waterfall
- `.gitignore`: Cập nhật loại trừ `backups/*.sql` và `performance/*.csv`.
- `.github/workflows/frontend-ci.yml`, `backend-ci.yml`, `security.yml`: Workflows CI/CD tự động hóa.
- `.github/PULL_REQUEST_TEMPLATE.md` & `.github/CODEOWNERS`: Quy chuẩn kiểm duyệt mã nguồn.
- `scripts/backup-db.ps1`, `restore-db.ps1`, `smoke-test.ps1`: Kịch bản sao lưu, phục hồi và kiểm tra nhanh.
- `performance/english-lms-performance.jmx`: Kịch bản đo lường hiệu năng Apache JMeter 5.6.3.
- Toàn bộ thư mục `docs/`: Chuẩn hóa 100% tài liệu theo mô hình Thác nước (Waterfall) và các tiêu chuẩn ISO/IEC/IEEE.

---

## 4. GIỚI HẠN ĐÃ BIẾT (KNOWN LIMITATIONS & FUTURE WORK)
1. **Google Gemini Quota Limits:** Dịch vụ AI phụ thuộc vào hạn ngạch miễn phí của Google Gemini API. Trong môi trường tải cao vượt mức hạn ngạch, dịch vụ sẽ phản hồi HTTP 429 Too Many Requests (đã được xử lý thông báo thân thiện trên frontend).
2. **Khuyến nghị nâng cấp tương lai:** Khi số lượng người dùng vượt quá 10,000 CCU, có thể xem xét bổ sung Redis Caching cho danh mục khóa học công khai và hàng đợi phân tán Kafka cho việc ghi nhận lịch sử tương tác AI.

---

## 5. KẾT LUẬN VÀ XÁC NHẬN NGHIỆM THU PHÁT HÀNH
Hệ thống **English LMS phiên bản 1.2.0** đáp ứng đầy đủ:
- **21/21 Yêu cầu chức năng (FR-01 đến FR-21).**
- **10/10 Nhóm yêu cầu phi chức năng (NFR-01 đến NFR-10).**
- **100% Tiêu chí nghiệm thu của 7 Cổng giai đoạn Waterfall.**

**TRẠNG THÁI CUỐI CÙNG: VERIFIED & READY FOR RELEASE.**
