# ENGLISH LMS - TEST EXECUTION & AUDIT REPORT

**Ngày thực thi kiểm thử**: 10/08/2026  
**Môi trường hệ thống**: Windows 11, JDK 21 (Eclipse Temurin), Node.js v20.x, Maven 3.9.x, Docker Desktop v27.x.

---

## 1. Ma Trận Coverage Chức Năng → Test (Functional Test Matrix)

| Nhóm Chức Năng | Thành Phần Được Kiểm Thử | Tên Test Case / File Test | Kết Quả |
| :--- | :--- | :--- | :--- |
| **Xác thực & Định danh** | Đăng ký & Đăng nhập tài khoản | `UserServiceTest.register_Success`, `UserSecurityTest.login_Success` | ✅ PASS |
| | JWT Validation & Expired Token | `JwtUtilTest.validateToken_Success`, `UserSecurityTest.invalidJwt_Forbidden` | ✅ PASS |
| | Phân quyền RBAC (STUDENT / ADMIN) | `UserSecurityTest.studentCannotAccessAdmin` | ✅ PASS |
| **Khóa học & Bài học** | Xem Danh mục & Chi tiết Khóa học | `CourseServiceTest.getAllCourses_Success`, `getCourseById_Success` | ✅ PASS |
| | Kiểm soát rò rỉ thông tin Lesson | `LessonServiceImplTest.getLessonsByCourseId_ReturnsSummaryOnly` | ✅ PASS |
| | Quy tắc xóa Khóa học (409 Conflict) | `CourseServiceTest.deleteCourse_WithLessons_ThrowsConflictException` | ✅ PASS |
| | Giới hạn Học thử 5 Bài miễn phí | `OrderServiceTest.trialLessonAccess_Success`, `paidLessonAccess_RequiresPurchase` | ✅ PASS |
| **Đăng ký & Tiến độ** | Đăng ký học phần (Enrollment) | `CourseServiceTest.enroll_Success`, `enroll_AlreadyEnrolled_ThrowsException` | ✅ PASS |
| | Đánh dấu Hoàn thành Bài học | `OrderServiceTest.completeLesson_Success`, `completeLesson_MismatchedCourse_Throws400` | ✅ PASS |
| | Khóa tiến độ tối đa 100% | `ProgressServiceImplTest.updateEnrollmentProgressFromRecords_CappedAt100` | ✅ PASS |
| **Thanh toán & Đơn hàng** | Tạo Đơn hàng & Tính giá | `OrderServiceTest.createOrder_Success` | ✅ PASS |
| | VNPay Callback Idempotency | `OrderServiceTest.handleVnPayCallback_IdempotentSuccess` | ✅ PASS |
| | Thống kê Doanh thu Admin | `OrderServiceTest.getRevenueReport_Success` | ✅ PASS |
| **Trợ lý AI Assistant** | Chat AI Session Continuity | `AiServiceTest.chat_WithSessionId_Success` | ✅ PASS |
| | Chuẩn hóa Grammar Check JSON | `AiServiceTest.grammarCheck_ParsesStrict8Fields` | ✅ PASS |
| | Trắc nghiệm Quiz 4 lựa chọn (Index 0..3) | `AiServiceTest.generateQuiz_ParsesNumericIndex` | ✅ PASS |
| | Phân quyền Lịch sử Chat AI | `AiSecurityTest.studentCanOnlyAccessOwnHistory`, `adminCanAccessAllHistory` | ✅ PASS |

---

## 2. Bảng Tổng Hợp Kết Quả Test Tự Động (Test Execution Summary)

| Microservice Module | Tổng Số Test | PASS | FAIL | SKIP | Thời Gian Chạy |
| :--- | :---: | :---: | :---: | :---: | :---: |
| `user-service` | 7 | 7 | 0 | 0 | 4.75s |
| `course-service` | 19 | 19 | 0 | 0 | 5.90s |
| `ai-service` | 37 | 37 | 0 | 0 | 6.07s |
| `api-gateway` | 2 | 2 | 0 | 0 | 1.20s |
| **TỔNG CỘNG BACKEND** | **65** | **65** | **0** | **0** | **17.92s** |

---

## 3. Kiểm Thử Frontend & Docker Configuration

1. **Frontend Production Build (`npm run build`)**:
   - Quá trình biên dịch Vite: **SUCCESS**
   - Đã biên dịch **11,635 modules** thành công không phát sinh lỗi syntax hay import broken.
2. **Cấu hình Docker Compose (`docker compose config --quiet`)**:
   - Kiểm tra cú pháp YAML và biến môi trường: **SUCCESS (0 Errors)**.

---

## 4. Các Lỗi Đã Phát Hiện Và Khắc Phục (Discovered & Fixed Issues)

1. **MapStruct Ambiguity**: Phát hiện lỗi ambiguous mapping trong `CourseMapper.java` khi MapStruct không phân biệt được giữa `toLessonResponse` và `toLessonSummaryResponse`. Đã khắc phục bằng `@Named("toFull")`, `@Named("toSummary")` và `@IterableMapping`.
2. **Khóa học chứa bài học/học viên bị xóa**: Phát hiện quy trình cũ cho phép xóa trực tiếp khóa học gây orphan data. Đã khắc phục trong `CourseServiceImpl.deleteCourse` ném lỗi HTTP 409 Conflict.
3. **Cú pháp biến môi trường Docker Compose**: Phát hiện cú pháp `${VAR:default}` gây lỗi ngắt dòng trong Compose spec v2+. Đã chuẩn hóa thành `${VAR:-default}`.
4. **Giới hạn tiến độ khóa học**: Đảm bảo thuật toán tính tiến độ luôn được chặn trần tối đa `100%` (`Math.min(100, pct)`).

---

## 5. Giới Hạn & Hạng Mục Không Kiểm Chứng Trực Tiếp trong Test Tự Động

- **Không gọi Google Gemini API thật**: Hệ thống kiểm thử tự động sử dụng Mock WebClient/TestStubs để đảm bảo không phụ thuộc vào kết nối mạng ngoài hoặc hết quota API Key.
- **VNPay Sandbox IPN**: Được kiểm thử qua signature verification và mock HTTP callback trong `OrderServiceTest`.

---

## 6. Lệnh Tái Kiểm Thử (Rerun Commands)

```bash
# 1. Chạy toàn bộ Unit & Integration Test của Backend Java
mvn clean test

# 2. Kiểm thử riêng từng Microservice
mvn test -pl user-service
mvn test -pl course-service
mvn test -pl ai-service

# 3. Kiểm thử Frontend Build
cd frontend
npm ci
npm run build

# 4. Kiểm thử Cấu hình Docker Compose
docker compose config --quiet

# 5. Chạy Automation Smoke Test Script (Cần Docker Stack đang bật)
python scripts/smoke-test.py http://localhost:8080
```
