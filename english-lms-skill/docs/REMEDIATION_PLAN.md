# ENGLISH LMS - REMEDIATION PLAN & FEATURE SPECIFICATIONS

## 1. Scope & System Goals
BTL môn MSCNPTPM: Xây dựng hệ thống quản lý học tập tiếng Anh English LMS tích hợp AI Assistant, Quản lý Khóa học, Mua/Thanh toán khóa học & Thống kê doanh thu Admin.

---

## 2. Key Features Implemented

### Section I: Security & Authentication (Completed)
- JWT validation with real `userId` payload across all microservices.
- RBAC authorization (`ROLE_STUDENT`, `ROLE_ADMIN`).
- Rate limiting filter on sensitive endpoints.

### Section II: AI Service & History (Completed)
- Fixed session & memory duplication.
- Session-based conversation tracking with `DELETE /api/v1/ai/sessions/{sessionId}`.
- Grammar check 8-field schema & Quiz 4-choice strict index (0..3) validation.

### Section III: Course Purchase, Payment & 5-Lesson Free Trial Limit (Completed)
1. **Course Pricing & Trial Access Domain**:
   - `price`, `sale_price`, `currency`, `published` added via Flyway Migration `V11`.
   - Free courses (`isFree = true`) accessible to all enrolled students.
   - Paid courses (`price > 0`):
     - Lessons 1..5 (`lessonOrder` index 0..4): Free Trial accessible to registered students (`TRIAL` status).
     - Lessons 6+ (`lessonOrder` index 5+): Access restricted to `ACTIVE` or `LEGACY_FREE` enrollments.
     - Unauthorized access returns HTTP 403 Forbidden with `errorCode: "COURSE_PURCHASE_REQUIRED"`.

2. **Order & Payment Engine**:
   - Implemented `CourseOrder` & `PaymentTransaction` in `course-service`.
   - Prices enforced strictly by backend database (frontend price payload ignored).
   - Prevents duplicate purchases (`COURSE_ALREADY_OWNED` 409 Conflict).
   - Order expiry: 15 minutes.

3. **Payment Gateways & Idempotency**:
   - **VNPay Sandbox**: Integrated `vnp_Version: 2.1.0` with HMAC-SHA512 checksum validation.
   - **Mock Payment**: Supported for local/demo profile testing without real credentials.
   - Idempotent callback & IPN processing: Repeating callbacks do not re-create enrollments or duplicate revenue calculations.

4. **Admin Revenue Analytics**:
   - Endpoint `GET /api/v1/admin/revenue` with date range filters (`from`, `to`) and grouping (`DAY`/`MONTH`).
   - Revenue metrics: Total Revenue (PAID orders only), Total Paid Orders, Distinct Students Count, Average Order Value (AOV), Pending & Failed Order Counts.
   - Top Courses by revenue breakdown.
   - Paginated transaction table with search & status filters (`GET /api/v1/admin/orders`).

### Section IV: Course Study Flow, Access Control & Data Consistency (Completed)
1. **LessonView Page & Content Protection**:
   - `LessonView.jsx` fetches real lesson details by `lessonId` from URL (`GET /api/v1/lessons/{lessonId}`).
   - Static fallback dummy objects removed completely.
   - Differentiates HTTP statuses: Loading, 401 Unauthorized (Redirect Login), 403 Forbidden (Purchase Required Dialog), 404 Not Found, 500 Internal Error.
   - Public course lesson list (`GET /api/v1/courses/{courseId}/lessons`) returns lesson summaries ONLY without leaking full markdown content, video URLs, or PDF links. Full content requires authenticated `/api/v1/lessons/{id}` endpoint.
   - Lesson content route `/courses/:courseId/lessons/:lessonId` protected with `ClientProtectedRoute`.

2. **Progress Completion Integrity**:
   - UI status updates to "completed" ONLY upon HTTP 200/201 response from `POST /api/v1/progress/complete`.
   - If API fails, UI keeps previous state and displays error notification (no silent fake success).
   - Backend validates that `lessonId` belongs to target `courseId` (`AppException` 400 if mismatched).
   - Progress percentage strictly capped at 100% max.

3. **Media Rendering**:
   - YouTube URLs parsed and converted to safe embed URLs (`https://www.youtube.com/embed/{ID}`).
   - Embedded using safe `<iframe>` with full screen and security attributes.
   - Empty videoUrl renders "Bài học chưa có video bài giảng". Text placeholder strings removed.
   - PDF/Document links rendered as downloadable `<Button href={docUrl} target="_blank" rel="noopener noreferrer">`.
   - Nullable media fields handle missing attachments gracefully without fake `example.com` URLs.

4. **Course Level Standardization**:
   - Standardized course level enum/strings in backend to `BEGINNER`, `INTERMEDIATE`, `ADVANCED`.
   - Created Flyway Migration `V12__normalize_course_levels_and_media.sql`.
   - Frontend maps values to Vietnamese labels: `BEGINNER` -> "Cơ bản", `INTERMEDIATE` -> "Trung cấp", `ADVANCED` -> "Nâng cao".

5. **Course Deletion Policy**:
   - Blocked deletion of courses containing lessons or enrolled students (`CourseServiceImpl.deleteCourse`).
   - Returns HTTP 409 Conflict with clear Vietnamese error message.

6. **Frontend Auth & Environment Variables**:
   - Axios interceptor in `api.js` automatically clears token and redirects to `/login` on HTTP 401 without infinite loops.
   - Environment variable `VITE_API_BASE_URL` supported with default fallback `http://localhost:8080`.

### Section V: Docker Multi-Stage Build, Performance Indexing & Packaging (Completed)
1. **Multi-stage Docker Build**:
   - Converted all Java service Dockerfiles (`discovery-server`, `config-server`, `api-gateway`, `user-service`, `course-service`, `ai-service`) to multi-stage Maven builds (`maven:3.9-eclipse-temurin-21-alpine` -> `eclipse-temurin:21-jre-alpine`).
   - Root build context used to compile multi-module Maven source code (`mvn -pl <module> -am package -DskipTests`).
   - Runtime container runs as non-root user `appuser`.
   - `docker compose up --build` runs directly from a clean git repository without pre-built host `target/*.jar` files.

2. **Config Server & Gateway Environments**:
   - `${CONFIG_SERVER_URL:http://localhost:8888}` & `${EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE:http://localhost:8761/eureka/}` configurable dynamically.
   - Gateway CORS configured with allowed methods (including `PATCH`), configurable `CORS_ALLOWED_ORIGINS`, credential forwarding enabled, no wildcard `*` with credentials.

3. **Performance Optimization & Database Indexes**:
   - Flyway Migration `V13__add_performance_indexes.sql` in `course-service` (indexes on `enrollments(user_id, course_id)`, `enrollments(student_email, course_id)`, `enrollments(status)`, `learning_progress(user_id, course_id, lesson_id)`).
   - Flyway Migration `V2__add_chat_history_indexes.sql` in `ai-service` (indexes on `chat_histories(user_id, session_id)` and lookup queries).

4. **Repository Hygiene & Safe Submission Scripts**:
   - Updated `.gitignore` & created `.dockerignore`.
   - Created safe packaging scripts `scripts/package-submission.ps1` (PowerShell) and `scripts/package-submission.sh` (Bash) to produce `english-lms-submission.zip` without deleting source files.
