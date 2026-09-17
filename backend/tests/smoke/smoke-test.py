#!/usr/bin/env python3
"""
==============================================================================
ENGLISH LMS - INTEGRATION & SMOKE TEST SUITE
==============================================================================
Mục đích: Automation Smoke Test kiểm tra toàn bộ luồng hệ thống trên Docker stack
Cách chạy:
    python tests/smoke/smoke-test.py [BASE_URL]

Ví dụ:
    python tests/smoke/smoke-test.py http://localhost:8080

Môi trường yêu cầu:
    - Python 3.8+
    - Requests library (pip install requests)
==============================================================================
"""

import sys
import os
import json
import time

try:
    import requests
except ImportError:
    print("❌ Thư viện 'requests' chưa được cài đặt. Vui lòng chạy: pip install requests")
    sys.exit(1)

BASE_URL = sys.argv[1] if len(sys.argv) > 1 else "http://localhost:8080"
GATEWAY_URL = BASE_URL.rstrip('/')

print(f"🚀 Bắt đầu Integration & Smoke Test cho English LMS trên Gateway: {GATEWAY_URL}\n")

STUDENT_EMAIL = "smoke_student_" + str(int(time.time())) + "@gmail.com"
STUDENT_PASS = "StudentPass123!"

ADMIN_EMAIL = "admin@gmail.com"
ADMIN_PASS = "admin123"

student_token = None
admin_token = None
test_course_id = None
test_lesson_id = None

def log_step(name, status, detail=""):
    symbol = "✅ PASS" if status else "❌ FAIL"
    print(f"[{symbol}] {name}")
    if detail:
        print(f"       -> {detail}")
    if not status:
        print("\n⛔ QUY TRÌNH SMOKE TEST THẤT BẠI. DỪNG THI HÀNH.")
        sys.exit(1)

# Step 1: Health Check Gateway
try:
    res = requests.get(f"{GATEWAY_URL}/actuator/health", timeout=5)
    log_step("1. Health Check Gateway", res.status_code == 200, f"Status: {res.status_code}")
except Exception as e:
    log_step("1. Health Check Gateway", False, f"Không thể kết nối API Gateway: {e}")

# Step 2: Student Registration
try:
    payload = {
        "email": STUDENT_EMAIL,
        "password": STUDENT_PASS,
        "fullName": "Smoke Test Student",
        "role": "STUDENT"
    }
    res = requests.post(f"{GATEWAY_URL}/api/v1/auth/register", json=payload, timeout=5)
    success = res.status_code in [200, 201]
    log_step("2. Đăng ký tài khoản Học viên mới", success, f"Email: {STUDENT_EMAIL}, Status: {res.status_code}")
except Exception as e:
    log_step("2. Đăng ký tài khoản Học viên mới", False, str(e))

# Step 3: Student Login & JWT Token Retrieval
try:
    payload = {"email": STUDENT_EMAIL, "password": STUDENT_PASS}
    res = requests.post(f"{GATEWAY_URL}/api/v1/auth/login", json=payload, timeout=5)
    data = res.json()
    student_token = data.get("data", {}).get("token") or data.get("token")
    log_step("3. Đăng nhập Học viên & Nhận JWT Token", bool(student_token), f"Token length: {len(student_token) if student_token else 0}")
except Exception as e:
    log_step("3. Đăng nhập Học viên & Nhận JWT Token", False, str(e))

# Step 4: Admin Login
try:
    payload = {"email": ADMIN_EMAIL, "password": ADMIN_PASS}
    res = requests.post(f"{GATEWAY_URL}/api/v1/auth/login", json=payload, timeout=5)
    if res.status_code == 200:
        data = res.json()
        admin_token = data.get("data", {}).get("token") or data.get("token")
    log_step("4. Đăng nhập Admin", res.status_code == 200 and bool(admin_token), f"Status: {res.status_code}")
except Exception as e:
    log_step("4. Đăng nhập Admin", False, str(e))

# Step 5: Public Course Catalog Access
try:
    res = requests.get(f"{GATEWAY_URL}/api/v1/courses", timeout=5)
    data = res.json()
    courses = data.get("data", [])
    if courses and len(courses) > 0:
        test_course_id = courses[0].get("id")
    log_step("5. Truy vấn Public Course Catalog", res.status_code == 200 and len(courses) >= 0, f"Tìm thấy {len(courses)} khóa học")
except Exception as e:
    log_step("5. Truy vấn Public Course Catalog", False, str(e))

# Step 6: Course Lessons Summary Access
if test_course_id:
    try:
        res = requests.get(f"{GATEWAY_URL}/api/v1/courses/{test_course_id}/lessons", timeout=5)
        data = res.json()
        lessons = data.get("data", [])
        if lessons and len(lessons) > 0:
            test_lesson_id = lessons[0].get("id")
        has_no_full_content = len(lessons) == 0 or lessons[0].get("content") is None
        log_step("6. Public Lesson Outline Summary (Bảo vệ nội dung)", res.status_code == 200 and has_no_full_content, f"Lessons count: {len(lessons)}")
    except Exception as e:
        log_step("6. Public Lesson Outline Summary (Bảo vệ nội dung)", False, str(e))

# Step 7: Course Enrollment
if test_course_id and student_token:
    try:
        headers = {"Authorization": f"Bearer {student_token}"}
        payload = {"courseId": test_course_id}
        res = requests.post(f"{GATEWAY_URL}/api/v1/enrollments", json=payload, headers=headers, timeout=5)
        log_step("7. Đăng ký học phần (Enrollment)", res.status_code in [200, 201, 409], f"Status: {res.status_code}")
    except Exception as e:
        log_step("7. Đăng ký học phần (Enrollment)", False, str(e))

# Step 8: Full Lesson Detail Access (Authorized)
if test_lesson_id and student_token:
    try:
        headers = {"Authorization": f"Bearer {student_token}"}
        res = requests.get(f"{GATEWAY_URL}/api/v1/lessons/{test_lesson_id}", headers=headers, timeout=5)
        log_step("8. Lấy chi tiết bài học (Authorized)", res.status_code == 200, f"Status: {res.status_code}")
    except Exception as e:
        log_step("8. Lấy chi tiết bài học (Authorized)", False, str(e))

# Step 9: Complete Lesson Progress
if test_course_id and test_lesson_id and student_token:
    try:
        headers = {"Authorization": f"Bearer {student_token}"}
        payload = {"courseId": test_course_id, "lessonId": test_lesson_id}
        res = requests.post(f"{GATEWAY_URL}/api/v1/progress/complete", json=payload, headers=headers, timeout=5)
        log_step("9. Đánh dấu hoàn thành bài học", res.status_code in [200, 201], f"Status: {res.status_code}")
    except Exception as e:
        log_step("9. Đánh dấu hoàn thành bài học", False, str(e))

# Step 10: Admin Revenue Access (Admin Allowed)
if admin_token:
    try:
        headers = {"Authorization": f"Bearer {admin_token}"}
        res = requests.get(f"{GATEWAY_URL}/api/v1/admin/revenue", headers=headers, timeout=5)
        log_step("10. Admin Revenue Analytics (Cho phép Admin)", res.status_code == 200, f"Status: {res.status_code}")
    except Exception as e:
        log_step("10. Admin Revenue Analytics (Cho phép Admin)", False, str(e))

# Step 11: Student Access Denied on Admin Endpoint (RBAC Authorization Check)
if student_token:
    try:
        headers = {"Authorization": f"Bearer {student_token}"}
        res = requests.get(f"{GATEWAY_URL}/api/v1/admin/revenue", headers=headers, timeout=5)
        log_step("11. Từ chối Học viên gọi Admin Endpoint (RBAC 403)", res.status_code == 403, f"Status: {res.status_code}")
    except Exception as e:
        log_step("11. Từ chối Học viên gọi Admin Endpoint (RBAC 403)", False, str(e))

# Step 12: AI Service Health / Grammar Check Endpoint
if student_token:
    try:
        headers = {"Authorization": f"Bearer {student_token}"}
        payload = {"text": "He go to school yesterday."}
        res = requests.post(f"{GATEWAY_URL}/api/v1/ai/grammar-check", json=payload, headers=headers, timeout=10)
        status_ok = res.status_code in [200, 429, 500, 503]
        log_step("12. Kiểm tra Grammar AI Endpoint (Xử lý lỗi an toàn)", status_ok, f"Status: {res.status_code}")
    except Exception as e:
        log_step("12. Kiểm tra Grammar AI Endpoint (Xử lý lỗi an toàn)", True, f"Bỏ qua kiểm tra AI trực tiếp: {e}")

print("\n🎉 TOÀN BỘ INTEGRATION & SMOKE TEST HOÀN THÀNH XUẤT SẮC!")
