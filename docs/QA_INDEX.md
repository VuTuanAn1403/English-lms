# MỤC LỤC HỒ SƠ CHẤT LƯỢNG VÀ TIÊU CHUẨN KỸ THUẬT (QA MASTER INDEX)
**Dự án:** English LMS – Hệ thống Quản lý Khóa học Tiếng Anh Trực tuyến Tích hợp AI  
**Tiêu chuẩn áp dụng:** ISO/IEC/IEEE 12207, IEEE 730, ISO/IEC 25010, ISO/IEC/IEEE 29119  
**Mô hình quy trình:** Thác nước Tuyến tính (Strict Linear Waterfall Lifecycle)  
**Phiên bản:** 1.2 • **Ngày cập nhật:** 17/09/2026  

---

## 1. BẢNG TRA CỨU HỒ SƠ KỸ THUẬT THEO TIÊU CHUẨN QUỐC TẾ

| Tiêu chuẩn quốc tế | Tên tiêu chuẩn / Lĩnh vực | Tài liệu hồ sơ trong dự án (Artifact Link) | Mô tả tóm tắt nội dung |
| :--- | :--- | :--- | :--- |
| **ISO/IEC/IEEE 12207:2017** | Software life cycle processes | [docs/08-development-plan.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/08-development-plan.md)<br>[docs/07-waterfall-gates.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/07-waterfall-gates.md) | Định nghĩa vòng đời Thác nước 7 pha tuyến tính và 7 cổng giai đoạn (Stage Gates) kiểm soát chất lượng. |
| **IEEE 730-2014** | Software Quality Assurance Processes | [docs/06-quality-plan.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/06-quality-plan.md) | Kế hoạch bảo đảm chất lượng phần mềm (SQAP), phân định vai trò, quy trình kiểm toán mã nguồn và tiêu chí chấp nhận. |
| **ISO/IEC 25010:2011** | Systems and software Quality Requirements and Evaluation (SQuaRE) | [docs/06-quality-plan.md#2-khung-tieu-chuan-chat-luong-isoiec-25010](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/06-quality-plan.md#2-khung-tieu-chuan-chat-luong-isoiec-25010) | Khung 8 đặc tính chất lượng: Tính phù hợp chức năng, hiệu năng, tương thích, khả dụng, tin cậy, an toàn, bảo trì, khả chuyển. |
| **ISO/IEC/IEEE 29119:2022** | Software Testing (Part 1, 2, 3) | [docs/09-testing.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/09-testing.md)<br>[docs/test-cases.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/test-cases.md)<br>[docs/TEST_RESULTS.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/TEST_RESULTS.md) | Kế hoạch kiểm thử (Test Plan), Danh mục kịch bản kiểm thử (Test Cases), Kết quả kiểm thử thực tế và Nhật ký lỗi (Defect Log). |
| **IEEE 1016-2009** | Information Technology - Systems Design - Software Design Descriptions | [docs/02-analysis.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/02-analysis.md)<br>[docs/02-architecture.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/02-architecture.md)<br>[docs/03-database-design.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/03-database-design.md)<br>[docs/04-api.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/04-api.md) | Mô tả kiến trúc microservices phân tán, thiết kế cơ sở dữ liệu per-service, ràng buộc toàn vẹn và hợp đồng RESTful API. |
| **ISO/IEC 27001 / OWASP** | Information Security Management / Application Security | [docs/01-baseline-audit.md#5-kiem-toan-bao-mat](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/01-baseline-audit.md)<br>[.github/workflows/security.yml](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/.github/workflows/security.yml) | Triệt tiêu lỗ hổng Header Spoofing, kiểm soát RBAC, bảo vệ chống Prompt Injection, kiểm soát secret hygiene. |

---

## 2. HỆ THỐNG TÀI LIỆU TOÀN BỘ VÒNG ĐỜI DỰ ÁN (PROJECT LIFECYCLE DIRECTORY)

### 2.1. Yêu cầu và Hiện trạng (Requirements & Baseline)
- [docs/01-baseline-audit.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/01-baseline-audit.md): Báo cáo kiểm toán hiện trạng hệ thống và kế hoạch xử lý lỗi tồn đọng.
- [docs/REQUIREMENTS_BASELINE.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/REQUIREMENTS_BASELINE.md): Danh mục 21 yêu cầu chức năng (FR) và 10 nhóm phi chức năng (NFR) kèm định nghĩa số liệu FR-21.
- [docs/TRACEABILITY_MATRIX.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/TRACEABILITY_MATRIX.md): Ma trận truy vết từ Yêu cầu -> Kiến trúc -> Mã nguồn -> Ca kiểm thử -> Bằng chứng nghiệm thu.

### 2.2. Phân tích và Thiết kế Kỹ thuật (Analysis & System Design)
- [docs/02-analysis.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/02-analysis.md): Phân tích ca sử dụng, kịch bản ngoại lệ và sơ đồ tuần tự hệ thống.
- [docs/02-architecture.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/02-architecture.md): Kiến trúc microservices, cổng API Gateway và vai trò triển khai Vercel.
- [docs/03-database-design.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/03-database-design.md): Thiết kế cơ sở dữ liệu tách biệt, ràng buộc duy nhất, chỉ mục tối ưu và chính sách Flyway.
- [docs/04-api.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/04-api.md): Đặc tả hợp đồng API đồng bộ với Controller backend.

### 2.3. Đảm bảo Chất lượng và Kiểm thử (QA & Testing)
- [docs/06-quality-plan.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/06-quality-plan.md): Kế hoạch bảo đảm chất lượng theo IEEE 730 và ISO/IEC 25010.
- [docs/07-waterfall-gates.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/07-waterfall-gates.md): 7 cổng giai đoạn phát triển và điều kiện đóng cổng.
- [docs/08-development-plan.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/08-development-plan.md): Kế hoạch chuyển đổi sang mô hình Thác nước tuyến tính.
- [docs/09-testing.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/09-testing.md): Kế hoạch kiểm thử hệ thống theo ISO/IEC/IEEE 29119.
- [docs/test-cases.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/test-cases.md): Danh mục kịch bản kiểm thử chi tiết cho 21 FR và 10 NFR.
- [docs/TEST_RESULTS.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/TEST_RESULTS.md): Kết quả kiểm thử thực tế và nhật ký xử lý lỗi.
- [docs/RESPONSIVE_VERIFICATION.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/RESPONSIVE_VERIFICATION.md): Báo cáo xác nhận hiển thị responsive trên Mobile, Tablet, Desktop.

### 2.4. Đóng gói, Vận hành và CI/CD (DevOps & Deployment)
- [docs/10-deployment.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/10-deployment.md): Hướng dẫn triển khai Docker Compose và kiến trúc Vercel.
- [docs/VERCEL_DEPLOYMENT.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/VERCEL_DEPLOYMENT.md): Hướng dẫn chi tiết triển khai Frontend lên Vercel.
- [docs/VERCEL_SMOKE_TEST.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/VERCEL_SMOKE_TEST.md): Checklist kiểm thử nhanh môi trường Vercel.
- [docs/GITHUB_SOURCE_CONTROL.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/GITHUB_SOURCE_CONTROL.md): Quy chuẩn quản lý mã nguồn GitHub và chiến lược rẽ nhánh.
- [docs/GITHUB_CI_CD.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/GITHUB_CI_CD.md): Đặc tả các luồng tự động hóa kiểm thử và kiểm toán bảo mật.
- [docs/RELEASE_PROCESS.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/RELEASE_PROCESS.md): Quy trình phát hành phiên bản và kiểm soát Stage Gate.
- [docs/ROLLBACK.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/ROLLBACK.md): Kịch bản và quy trình khôi phục phiên bản trước khi có sự cố.
- [docs/backup-restore.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/backup-restore.md): Hướng dẫn chi tiết quy trình sao lưu và phục hồi dữ liệu PostgreSQL.

### 2.5. Nghiệm thu và Bản quyền (Acceptance & Release)
- [docs/FINAL_ACCEPTANCE.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/docs/FINAL_ACCEPTANCE.md): Báo cáo nghiệm thu kỹ thuật tổng thể trước khi đóng dự án.
- [RELEASE_NOTES.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/RELEASE_NOTES.md): Ghi chú phát hành chính thức phiên bản 1.2.
- [CHANGELOG.md](file:///c:/Users/ASUS/BTL_MSCNPTPM/english-lms/CHANGELOG.md): Lịch sử thay đổi các phiên bản.
