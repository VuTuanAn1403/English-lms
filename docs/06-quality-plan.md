# KẾ HOẠCH BẢO ĐẢM CHẤT LƯỢNG PHẦN MỀM (SOFTWARE QUALITY ASSURANCE PLAN - SQAP)
**Dự án:** English LMS – Hệ thống Quản lý Khóa học Tiếng Anh Trực tuyến Tích hợp AI  
**Tiêu chuẩn áp dụng:** IEEE 730-2014 & ISO/IEC 25010:2011  
**Mô hình phát triển:** Thác nước (Waterfall Lifecycle)  
**Phiên bản tài liệu:** 1.2 • **Ngày cập nhật:** 17/09/2026  

---

## 1. MỤC TIÊU VÀ PHẠM VI (PURPOSE & SCOPE)

### 1.1. Mục tiêu
Kế hoạch Bảo đảm Chất lượng Phần mềm (SQAP) này thiết lập các nguyên tắc, quy trình, tiêu chuẩn kỹ thuật và tiêu chí nghiệm thu áp dụng cho toàn bộ vòng đời phát triển Waterfall của dự án English LMS, nhằm:
- Đảm bảo hệ thống đáp ứng đầy đủ 21 yêu cầu chức năng (FR-01 đến FR-21) và 10 nhóm yêu cầu phi chức năng (NFR-01 đến NFR-10).
- Tuân thủ cấu trúc chất lượng sản phẩm theo tiêu chuẩn quốc tế ISO/IEC 25010.
- Kiểm soát chất lượng thông qua 7 cổng giai đoạn (Stage Gates) nghiêm ngặt, ngăn ngừa lỗi chuyển dịch qua các pha sau (Defect Leakage Prevention).
- Đảm bảo tính minh bạch, khả năng truy vết nguồn gốc (Traceability) từ yêu cầu đến mã nguồn và bằng chứng kiểm thử.

### 1.2. Phạm vi áp dụng
- Toàn bộ các dịch vụ Backend Spring Boot Microservices (`api-gateway`, `config-server`, `discovery-server`, `user-service`, `course-service`, `ai-service`).
- Ứng dụng Frontend React/Vite SPA và cấu hình triển khai Vercel.
- Cơ sở dữ liệu PostgreSQL và các tập lệnh di chuyển lược đồ Flyway.
- Hạ tầng triển khai container hóa Docker Compose và quy trình CI/CD GitHub Actions.

---

## 2. KHUNG TIÊU CHUẨN CHẤT LƯỢNG ISO/IEC 25010

Hệ thống English LMS được đánh giá dựa trên 8 đặc tính chất lượng của ISO/IEC 25010:

| Đặc tính chất lượng (Characteristic) | Phân nhóm (Sub-characteristic) | Chỉ số mục tiêu (Target Metric) | Phương pháp kiểm tra (Verification Method) |
| :--- | :--- | :--- | :--- |
| **1. Functional Suitability** (Tính phù hợp chức năng) | Functional Completeness | 100% bao phủ 21 FRs | Automated Unit & Integration Tests, Manual QA |
| | Functional Correctness | Tỷ lệ lỗi chức năng nghiêm trọng = 0 | Testing per Test Cases Catalog |
| | Functional Appropriateness | Đáp ứng chính xác nghiệp vụ LMS & AI | Business Flow Validation |
| **2. Performance Efficiency** (Hiệu năng vận hành) | Time Behaviour | API CRUD < 2.0 giây (p95), AI Endpoint có timeout và streaming | JMeter Load Test Plan (Concurrency 20-50 threads) |
| | Resource Utilization | RAM backend < 512MB/service, CPU ổn định dưới tải | Docker stats & JVM JMX metrics |
| | Capacity | Khả năng xử lý tối thiểu 50 CCU trên môi trường tiêu chuẩn | Stress Test qua JMeter |
| **3. Compatibility** (Tính tương thích) | Co-existence | Độc lập giữa các Microservices qua REST/JSON | Service Isolation Testing |
| | Interoperability | API Gateway định tuyến và trao đổi JSON chuẩn UTF-8 | API Contract & Integration Tests |
| **4. Usability** (Tính khả dụng) | Appropriateness Recognisability | Giao diện tiếng Việt nhất quán, nhãn rõ ràng | UI/UX Inspection & Usability Tasks |
| | Learnability | Luồng học viên/quản trị trực quan, dễ thao tác | Task Completion Time (< 3 phút cho luồng chính) |
| | User Error Protection | Disable button khi submitting, thông báo lỗi cụ thể | Negative & Boundary Testing |
| | Accessibility / Responsive | Hỗ trợ 3 kích thước: 375x667, 768x1024, 1366x768 | Viewport DevTools & Responsive Verification |
| **5. Reliability** (Độ tin cậy) | Maturity | Không phát sinh lỗi NullPointer/Crash chưa kiểm soát | Global Exception Handling Validation |
| | Fault Tolerance | AI external timeout/failure không gây sập service | Circuit Breaker / Graceful Fallback (HTTP 502/504) |
| | Recoverability | Khôi phục dữ liệu từ bản sao lưu < 15 phút, RPO < 24h | Backup & Restore Verification Scripts |
| **6. Security** (Bảo mật) | Confidentiality | JWT Authentication, BCrypt hash, không lộ secret | Security Audit, JWT Validation Test |
| | Integrity | Chặn Header Spoofing (`X-User-*`), Database Unique Constraint | Pen-test mô phỏng giả mạo header, DB Migration test |
| | Non-repudiation | Ghi log đăng nhập, thao tác nhạy cảm với Correlation ID | Centralized Audit Logs Verification |
| | Accountability | RBAC phân quyền Admin/Student nghiêm ngặt | Negative Role Privilege Escalation Tests |
| **7. Maintainability** (Khả năng bảo trì) | Modularity | Microservices tách biệt cơ sở dữ liệu (Database-per-Service) | Architecture Review |
| | Reusability | Component frontend chuẩn hóa, DTO backend tái sử dụng | Code Inspection |
| | Analysability | Correlation ID (`X-Correlation-Id`) xuyên suốt các log | Centralized Log Querying |
| | Modifiability | Quản lý lược đồ cơ sở dữ liệu qua Flyway migrations | Flyway Validate & Migrate Clean Runs |
| | Testability | Độc lập kiểm thử tầng Service và Controller bằng Mockito | Unit Test Suite Coverage |
| **8. Portability** (Tính khả chuyển) | Adaptability | Docker Compose multi-stage build chạy độc lập máy chủ | Clean Host Docker Build Verification |
| | Installability | Quy trình cài đặt tự động qua scripts và CI/CD | GitHub Actions CI & Vercel Auto-deploy |

---

## 3. QUY TRÌNH KIỂM SOÁT VÀ ĐẢM BẢO CHẤT LƯỢNG (QA PROCESSES)

### 3.1. Phân định vai trò và trách nhiệm (Roles & Responsibilities)
- **Software Architect / Tech Lead:** Chịu trách nhiệm về tính toàn vẹn kiến trúc, chuẩn hóa API contracts, phê duyệt thiết kế database và Flyway migrations.
- **Backend Developers:** Cài đặt mã nguồn Spring Boot, viết unit test (JUnit 5, Mockito), đảm bảo không còn N+1 queries và tuân thủ security guidelines.
- **Frontend Developers:** Xây dựng giao diện React/Vite, xử lý SPA routing, tương thích responsive, đồng bộ hóa xử lý lỗi HTTP.
- **QA / Test Engineer:** Thiết kế Test Plan, Test Cases, kịch bản JMeter, thực thi kiểm thử hồi quy, ghi nhận Defect Log và lập Test Report.
- **DevOps / Release Manager:** Quản lý GitHub CI/CD, thiết lập Branch Rulesets, giám sát Vercel deployments và sao lưu/phục hồi cơ sở dữ liệu.

### 3.2. Quy trình kiểm tra mã nguồn (Code Review & Quality Gate)
1. **Static Analysis & Linting:**
   - Frontend: ESLint và kiểm tra cú pháp JSX, đảm bảo không có cảnh báo nghiêm trọng.
   - Backend: Kiểm tra cảnh báo compiler, chuẩn mực đặt tên Java, không có import thừa.
2. **Secret Scanning:**
   - Quét mã nguồn trước mỗi commit để loại bỏ hoàn toàn các secret thực tế (API Keys, JWT Secrets, Database Passwords).
3. **Automated Continuous Integration (GitHub Actions):**
   - Mọi Pull Request phải vượt qua các workflows: `frontend-ci`, `backend-ci`, `security`.
   - Bắt buộc kiểm tra 100% test cases unit/integration trước khi cho phép hợp nhất vào nhánh `main`.

---

## 4. QUẢN LÝ LỖI VÀ SỰ CỐ (DEFECT MANAGEMENT)

### 4.1. Phân loại mức độ nghiêm trọng (Severity Levels)
- **Blocker (Severity 1):** Hệ thống sập, dịch vụ không khởi động được, lỗ hổng bảo mật nghiêm trọng (như Header Spoofing cho phép leo quyền, lộ API key), mất mát dữ liệu.
- **Critical (Severity 2):** Chức năng cốt lõi bị gián đoạn hoàn toàn (không thể đăng nhập, không thể đổi mật khẩu, không thể đăng ký khóa học, N+1 query gây nghẽn DB nghiêm trọng).
- **Major (Severity 3):** Chức năng nghiệp vụ có lỗi logic hoặc lỗi giao diện gây cản trở trải nghiệm nhưng có cách xử lý tạm thời (workaround).
- **Minor (Severity 4):** Lỗi giao diện nhỏ, chính tả tiếng Việt, sai lệch lề responsive nhẹ ở kích thước dị biệt.

### 4.2. Tiêu chí chặn phát hành (Release Blocking Criteria)
- Không có bất kỳ lỗi **Blocker** hoặc **Critical** nào còn tồn đọng khi đóng pha Release Gate.
- Lỗi **Major** phải có phương án khắc phục được ghi nhận rõ trong Known Limitations.

---

## 5. HỒ SƠ VÀ TÀI LIỆU CHẤT LƯỢNG BẮT BUỘC (SQA ARTIFACTS)
1. **Kế hoạch kiểm thử (Test Plan):** `docs/09-testing.md` (ISO/IEC/IEEE 29119).
2. **Danh mục kịch bản kiểm thử (Test Cases):** `docs/test-cases.md`.
3. **Báo cáo kết quả kiểm thử (Test Results & Defect Log):** `docs/TEST_RESULTS.md`.
4. **Ma trận truy vết yêu cầu (Traceability Matrix):** `docs/TRACEABILITY_MATRIX.md`.
5. **Kế hoạch phục hồi dữ liệu (Data Recovery):** `docs/backup-restore.md`.
6. **Báo cáo nghiệm thu cuối cùng (Final Acceptance):** `docs/FINAL_ACCEPTANCE.md`.
