# BẢNG CHỈ MỤC KIỂM THỬ HỆ THỐNG (TEST INDEX MANIFEST)

> **Mục đích**: Tập hợp toàn bộ chỉ mục kiểm thử của hệ thống English LMS, bao gồm Backend Unit/Security Tests (JUnit 5), API Tests (Python), Integration & Smoke Tests (Docker Stack) và Frontend Production Build Check.

---

## 📋 MA TRẬN TỔNG HỢP TOÀN BỘ TEST CASES

| Tên Test Case | Đường Dẫn File | Module | Loại Test | Chức Năng Được Kiểm Tra | Framework | Môi Trường | Cần Docker? | Gọi Service Ngoài? | Lệnh Chạy Thực Tế | Trạng Thái |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :---: | :---: | :--- | :---: |
| `register_Success` | `user-service/src/test/java/com/englishlms/user/UserServiceTest.java` | `user-service` | Unit | Đăng ký Học viên mới & Mã hóa BCrypt | JUnit 5 + Mockito | JDK 21 | Không | Không | `mvn test -pl user-service` | ✅ PASS |
| `register_DuplicateEmail_ThrowsException` | `user-service/src/test/java/com/englishlms/user/UserServiceTest.java` | `user-service` | Unit | Chặn đăng ký email trùng lặp | JUnit 5 + Mockito | JDK 21 | Không | Không | `mvn test -pl user-service` | ✅ PASS |
| `login_Success` | `user-service/src/test/java/com/englishlms/user/security/UserSecurityTest.java` | `user-service` | Security | Đăng nhập hợp lệ & Tạo JWT Token | JUnit 5 + MockMvc | JDK 21 | Không | Không | `mvn test -pl user-service` | ✅ PASS |
| `login_InvalidCredentials_Returns401` | `user-service/src/test/java/com/englishlms/user/security/UserSecurityTest.java` | `user-service` | Security | Đăng nhập sai mật khẩu trả về 401 | JUnit 5 + MockMvc | JDK 21 | Không | Không | `mvn test -pl user-service` | ✅ PASS |
| `studentCannotAccessAdminApi` | `user-service/src/test/java/com/englishlms/user/security/UserSecurityTest.java` | `user-service` | Security | Phân quyền RBAC từ chối STUDENT gọi Admin API (403) | JUnit 5 + MockMvc | JDK 21 | Không | Không | `mvn test -pl user-service` | ✅ PASS |
| `getAllCourses_Success` | `course-service/src/test/java/com/englishlms/course/CourseServiceTest.java` | `course-service` | Unit | Truy vấn danh sách khóa học | JUnit 5 + Mockito | JDK 21 | Không | Không | `mvn test -pl course-service` | ✅ PASS |
| `getCourseById_Success` | `course-service/src/test/java/com/englishlms/course/CourseServiceTest.java` | `course-service` | Unit | Xem chi tiết khóa học | JUnit 5 + Mockito | JDK 21 | Không | Không | `mvn test -pl course-service` | ✅ PASS |
| `deleteCourse_WithLessons_ThrowsConflictException` | `course-service/src/test/java/com/englishlms/course/CourseServiceTest.java` | `course-service` | Unit | Quy tắc chặn xóa khóa học đã có bài học (409 Conflict) | JUnit 5 + Mockito | JDK 21 | Không | Không | `mvn test -pl course-service` | ✅ PASS |
| `deleteCourse_WithEnrollments_ThrowsConflictException` | `course-service/src/test/java/com/englishlms/course/CourseServiceTest.java` | `course-service` | Unit | Quy tắc chặn xóa khóa học đã có học viên (409 Conflict) | JUnit 5 + Mockito | JDK 21 | Không | Không | `mvn test -pl course-service` | ✅ PASS |
| `enroll_Success` | `course-service/src/test/java/com/englishlms/course/CourseServiceTest.java` | `course-service` | Unit | Đăng ký học phần (Enrollment) | JUnit 5 + Mockito | JDK 21 | Không | Không | `mvn test -pl course-service` | ✅ PASS |
| `trialLessonAccess_Success` | `course-service/src/test/java/com/englishlms/course/service/OrderServiceTest.java` | `course-service` | Integration | Cho phép học thử 5 bài đầu tiên đối với tài khoản TRIAL | JUnit 5 + Mockito | JDK 21 | Không | Không | `mvn test -pl course-service` | ✅ PASS |
| `paidLessonAccess_RequiresPurchase` | `course-service/src/test/java/com/englishlms/course/service/OrderServiceTest.java` | `course-service` | Integration | Từ chối học bài 6+ khi chưa mua khóa học (403 Forbidden) | JUnit 5 + Mockito | JDK 21 | Không | Không | `mvn test -pl course-service` | ✅ PASS |
| `createOrder_Success` | `course-service/src/test/java/com/englishlms/course/service/OrderServiceTest.java` | `course-service` | Integration | Tạo đơn hàng mua khóa học | JUnit 5 + Mockito | JDK 21 | Không | Không | `mvn test -pl course-service` | ✅ PASS |
| `handleVnPayCallback_IdempotentSuccess` | `course-service/src/test/java/com/englishlms/course/service/OrderServiceTest.java` | `course-service` | Integration | Xử lý thanh toán VNPay bất biến (Idempotent Callback) | JUnit 5 + Mockito | JDK 21 | Không | Không | `mvn test -pl course-service` | ✅ PASS |
| `getRevenueReport_Success` | `course-service/src/test/java/com/englishlms/course/service/OrderServiceTest.java` | `course-service` | Integration | Thống kê Báo cáo Doanh thu Admin Analytics | JUnit 5 + Mockito | JDK 21 | Không | Không | `mvn test -pl course-service` | ✅ PASS |
| `completeLesson_Success` | `course-service/src/test/java/com/englishlms/course/service/OrderServiceTest.java` | `course-service` | Integration | Đánh dấu hoàn thành bài học & cập nhật tiến độ | JUnit 5 + Mockito | JDK 21 | Không | Không | `mvn test -pl course-service` | ✅ PASS |
| `completeLesson_MismatchedCourse_Throws400` | `course-service/src/test/java/com/englishlms/course/service/OrderServiceTest.java` | `course-service` | Integration | Kiểm tra bài học phải thuộc khóa học (400 Bad Request) | JUnit 5 + Mockito | JDK 21 | Không | Không | `mvn test -pl course-service` | ✅ PASS |
| `chat_WithSessionId_Success` | `ai-service/src/test/java/com/englishlms/ai/service/AiServiceTest.java` | `ai-service` | Unit | Chat AI duy trì ngữ cảnh theo SessionId | JUnit 5 + Mockito | JDK 21 | Không | Mocked | `mvn test -pl ai-service` | ✅ PASS |
| `grammarCheck_ParsesStrict8Fields` | `ai-service/src/test/java/com/englishlms/ai/service/AiServiceTest.java` | `ai-service` | Unit | Sửa lỗi Ngữ pháp trả về JSON 8 trường chuẩn | JUnit 5 + Mockito | JDK 21 | Không | Mocked | `mvn test -pl ai-service` | ✅ PASS |
| `generateQuiz_ParsesNumericIndex` | `ai-service/src/test/java/com/englishlms/ai/service/AiServiceTest.java` | `ai-service` | Unit | Sinh Trắc nghiệm 4 lựa chọn với đáp án số (0..3) | JUnit 5 + Mockito | JDK 21 | Không | Mocked | `mvn test -pl ai-service` | ✅ PASS |
| `studentCanOnlyAccessOwnHistory` | `ai-service/src/test/java/com/englishlms/ai/security/AiSecurityTest.java` | `ai-service` | Security | Học viên chỉ được xem Lịch sử AI của chính mình | JUnit 5 + MockMvc | JDK 21 | Không | Không | `mvn test -pl ai-service` | ✅ PASS |
| `adminCanAccessAllHistory` | `ai-service/src/test/java/com/englishlms/ai/security/AiSecurityTest.java` | `ai-service` | Security | Admin xem và quản lý toàn bộ Lịch sử AI hệ thống | JUnit 5 + MockMvc | JDK 21 | Không | Không | `mvn test -pl ai-service` | ✅ PASS |
| `validateToken_Success` | `api-gateway/src/test/java/com/englishlms/gateway/JwtUtilTest.java` | `api-gateway` | Unit | Kiểm tra tính hợp lệ Chữ ký JWT Token tại Gateway | JUnit 5 | JDK 21 | Không | Không | `mvn test -pl api-gateway` | ✅ PASS |
| `smoke_test_suite_12_steps` | `tests/smoke/smoke-test.py` | `tests` | Smoke / E2E | Smoke Test 12 bước end-to-end trên Docker Stack | Python Requests | Python 3.8+ | **Có** | Mocked/Safe | `python tests/smoke/smoke-test.py` | ✅ PASS |
| `api_test_suite` | `tests/api/api-test-suite.py` | `tests` | API | Kiểm thử các REST API Endpoints | Python unittest | Python 3.8+ | Không | Không | `python tests/api/api-test-suite.py` | ✅ PASS |
| `frontend_production_build` | `frontend/package.json` | `frontend` | Build / UI | Kiểm thử đóng gói Production Bundle của Frontend | Vite / Node.js | Node 20.x | Không | Không | `cd frontend && npm run build` | ✅ PASS |
| `docker_compose_config_check` | `docker-compose.yml` | `root` | Config | Kiểm tra cú pháp và biến môi trường Compose | Docker Compose | Docker v24+ | Không | Không | `docker compose config --quiet` | ✅ PASS |

---

## 📂 NHẬT KÝ QUẢN LÝ TÀI LIỆU VÀ SCRIPT LỊCH SỬ (LEGACY ARCHIVE LOG)

| Script Cũ | Thao Tác Quản Lý | Lý Do Quản Lý / Thay Thế |
| :--- | :--- | :--- |
| `tests/test_all_step_requirements.py` | Di chuyển vào `tests/legacy/` | Tham chiếu endpoint cũ `/ai/grammar` & hardcode học viên `vutuanan`. Đã thay thế bằng `tests/smoke/smoke-test.py`. |
| `tests/test_enrollment_management.py` | Di chuyển vào `tests/legacy/` | Đã hợp nhất vào `tests/smoke/smoke-test.py` và `OrderServiceTest.java`. |
| `tests/test_lesson_management.py` | Di chuyển vào `tests/legacy/` | Đã hợp nhất vào `LessonServiceImplTest` và `smoke-test.py`. |
| `tests/test_spring_ai_coach_verification.py` | Di chuyển vào `tests/legacy/` | Đã hợp nhất vào `AiServiceTest.java`. |
| `tests/test_step11_step12_student_model.py` | Di chuyển vào `tests/legacy/` | Đã thay thế bởi `UserServiceTest.java`. |
| `tests/test_student_vutuanan.py` | Di chuyển vào `tests/legacy/` | Script chứa dữ liệu học viên cứng. Đã chuyển sang dữ liệu tự sinh linh hoạt trong `smoke-test.py`. |
| `tests/test_vutuanan_sync_verification.py` | Di chuyển vào `tests/legacy/` | Đã thay thế bởi `TEST_REPORT.md` và `smoke-test.py`. |
| `tests/verify_dynamic_enrollment_system.py` | Di chuyển vào `tests/legacy/` | Đã hợp nhất vào `OrderServiceTest.java`. |
| `tests/verify_zero_hardcode_dynamic_engine.py` | Di chuyển vào `tests/legacy/` | Đã hợp nhất vào `CourseServiceTest.java`. |
