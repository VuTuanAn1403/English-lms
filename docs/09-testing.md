# KẾ HOẠCH KIỂM THỬ HỆ THỐNG (SOFTWARE TEST PLAN - STP)
**Dự án:** English LMS – Hệ thống Quản lý Khóa học Tiếng Anh Trực tuyến Tích hợp AI  
**Tiêu chuẩn áp dụng:** ISO/IEC/IEEE 29119:2022 Software Testing (Part 1, 2, 3)  
**Mô hình phát triển:** Thác nước Tuyến tính (Strict Linear Waterfall Lifecycle)  
**Phiên bản:** 1.2 • **Ngày cập nhật:** 17/09/2026  

---

## 1. TỔNG QUAN VÀ PHẠM VI KIỂM THỬ (TEST SCOPE & OBJECTIVES)

### 1.1. Mục tiêu kiểm thử
Xác nhận và thẩm định (Verification & Validation) toàn diện hệ thống English LMS nhằm:
- Đảm bảo 100% các yêu cầu chức năng (FR-01 đến FR-21) hoạt động chính xác theo đặc tả.
- Đảm bảo các yêu cầu phi chức năng (NFR-01 đến NFR-10) đạt chỉ số mục tiêu: hiệu năng p95 < 2.0s, an toàn bảo mật không lộ secret, kiểm soát phân quyền nghiêm ngặt, tương thích responsive trên 3 kích thước màn hình.
- Xác nhận tính toàn vẹn của dữ liệu sau migration, khả năng sao lưu và phục hồi thảm họa (Disaster Recovery).

### 1.2. Phạm vi kiểm thử (In-Scope)
- **Kiểm thử đơn vị (Unit Testing):** Tầng Service, Repository, DTO Validation, Security Utils, Prompt Strategies.
- **Kiểm thử tích hợp (Integration Testing):** API Gateway routing, xác thực Bearer JWT, phương thức bảo mật `@PreAuthorize("hasRole('ADMIN')")`, Flyway schema migrations, cơ sở dữ liệu PostgreSQL.
- **Kiểm thử an toàn thông tin (Security Testing):** Chống Header Spoofing (`X-User-*`), cách ly quyền truy cập dữ liệu giữa các người dùng (Data Isolation), chống chèn prompt trái phép (Prompt Injection Guardrails).
- **Kiểm thử hiệu năng (Performance Testing):** Apache JMeter kịch bản Catalog, Auth, Admin Stats, AI isolation.
- **Kiểm thử giao diện & tính khả dụng (UI/UX & Responsive Testing):** Luồng học viên, luồng quản trị viên, kiểm thử tương thích 375x667, 768x1024, 1366x768.
- **Kiểm thử phục hồi thảm họa (Disaster Recovery Testing):** Kịch bản sao lưu `scripts/backup-db.ps1` và phục hồi `scripts/restore-db.ps1`.

### 1.3. Ngoài phạm vi (Out-of-Scope)
- Kiểm thử thâm nhập sâu cấp hạ tầng mạng vật lý (Physical Network Penetration Testing).
- Kiểm thử các dịch vụ bên ngoài độc quyền của Google Gemini API vượt quá ngưỡng giới hạn hạn ngạch (Quota limits).

---

## 2. MÔI TRƯỜNG VÀ CÔNG CỤ KIỂM THỬ (TEST ENVIRONMENT & TOOLS)

| Loại công cụ | Tên công cụ / Phiên bản | Mục đích sử dụng |
| :--- | :--- | :--- |
| **Test Framework** | JUnit 5, Mockito, AssertJ | Kiểm thử đơn vị và tích hợp Backend Spring Boot |
| **Build & Execution** | Apache Maven 3.9.x, OpenJDK 21 | Quản lý phụ thuộc và thực thi test suite |
| **API & Security Test** | PowerShell 5.1 / 7.x (`scripts/smoke-test.ps1`), cURL | Smoke test gateway, kiểm tra Header Spoofing |
| **Performance Tool** | Apache JMeter 5.6.3 (`performance/english-lms-performance.jmx`) | Đo lường độ trễ, throughput và tải đồng thời |
| **Database Engine** | PostgreSQL 16 (Docker container) | Kiểm tra ràng buộc duy nhất và Flyway migrations |
| **Frontend Testing** | Vite 6.4.x, Browser DevTools | Build production, kiểm tra responsive viewports |

---

## 3. CÁC CẤP ĐỘ KIỂM THỬ (TEST LEVELS)

### 3.1. Cấp độ 1: Kiểm thử Đơn vị (Unit Testing)
- Thực thi độc lập trên từng microservice: `user-service`, `course-service`, `ai-service`.
- Cô lập các phụ thuộc bên ngoài bằng Mockito (`@Mock`, `@InjectMocks`).
- Phạm vi kiểm tra: Logic nghiệp vụ (đổi mật khẩu, tạo đơn hàng, tính tiến độ), validation DTO, chiến lược prompt AI.

### 3.2. Cấp độ 2: Kiểm thử Tích hợp (Integration Testing)
- Kiểm tra sự tương tác giữa Spring Data JPA và PostgreSQL database.
- Kiểm tra tính hợp lệ của Flyway Migrations (V1 đến V14).
- Kiểm tra luồng định tuyến và lọc token của API Gateway đến các service phía sau.

### 3.3. Cấp độ 3: Kiểm thử Hệ thống (System Testing)
- Kiểm thử end-to-end các ca nghiệp vụ hoàn chỉnh: Đăng ký -> Đăng nhập -> Duyệt khóa học -> Ghi danh -> Học bài -> Trợ lý AI -> Thống kê tiến độ.
- Kiểm tra giao diện Quản trị viên: Quản lý người dùng, khóa học, bài học, thống kê số liệu thực tế.

### 3.4. Cấp độ 4: Kiểm thử Chấp nhận Người dùng (User Acceptance Testing - UAT)
- Nghiệm thu dựa trên danh mục 21 FR và 10 NFR.
- Xác nhận các tiêu chí hiển thị tiếng Việt, không còn dữ liệu mẫu giả lập (`sampleCourses`), thông báo lỗi thân thiện.

---

## 4. TIÊU CHÍ VÀO VÀ TIÊU CHÍ DỪNG (ENTRY & EXIT CRITERIA)

### 4.1. Tiêu chí vào (Entry Criteria)
- Mã nguồn đã được biên dịch thành công (`mvn clean compile` không có lỗi cú pháp).
- Cơ sở dữ liệu và các container dịch vụ đã khởi động và kiểm tra sức khỏe thành công (Healthcheck: UP).
- Tài liệu kiểm thử và danh mục ca kiểm thử (`docs/test-cases.md`) đã được phê duyệt tại Gate 4.

### 4.2. Tiêu chí dừng / Tiêu chí Đạt (Exit Criteria)
- 100% các ca kiểm thử trong `docs/test-cases.md` được thực thi và ghi nhận kết quả.
- Tỷ lệ đạt (Pass Rate) của các ca kiểm thử chức năng và bảo mật đạt 100%.
- Không còn bất kỳ lỗi nào thuộc mức độ **Blocker** hoặc **Critical**.
- Thời gian phản hồi đo lường qua JMeter đối với các API CRUD thông thường p95 < 2.0s.
- Báo cáo kết quả kiểm thử (`docs/TEST_RESULTS.md`) hoàn tất và sẵn sàng cho nghiệm thu tại Gate 5.

---

## 5. MA TRẬN QUẢN TRỊ RỦI RO KIỂM THỬ (TEST RISK MATRIX)

| Rủi ro (Risk) | Khả năng | Tác động | Giải pháp giảm thiểu (Mitigation Strategy) |
| :--- | :---: | :---: | :--- |
| **Giới hạn Quota Google Gemini API** | Trung bình | Cao | Tách biệt kịch bản kiểm thử AI thành thread group riêng trong JMeter với pacing hợp lý; sử dụng mock response khi kiểm thử unit. |
| **Chạy song song gây xung đột DB** | Thấp | Trung bình | Sử dụng cơ chế transaction rollback trong test hoặc dùng cơ sở dữ liệu test biệt lập (H2/test containers). |
| **Mạng chập chờn khi build Vercel** | Thấp | Thấp | Kiểm tra cấu hình build Vite sạch sẽ tại local trước khi trigger CI/CD pipeline. |
