import urllib.request, json, sys, time

sys.stdout.reconfigure(encoding='utf-8')
time.sleep(3)

def post(url, data, token=None):
    headers = {'Content-Type': 'application/json'}
    if token:
        headers['Authorization'] = f'Bearer {token}'
    req = urllib.request.Request(url, data=json.dumps(data).encode('utf-8'), headers=headers)
    return json.loads(urllib.request.urlopen(req).read().decode('utf-8'))

def get(url, token=None):
    headers = {}
    if token:
        headers['Authorization'] = f'Bearer {token}'
    req = urllib.request.Request(url, headers=headers)
    return json.loads(urllib.request.urlopen(req).read().decode('utf-8'))

print("=========================================================")
print("  STEP 1: REGISTER & LOGIN vutuanan1403@gmail.com")
print("=========================================================")
vutuanan_email = "vutuanan1403@gmail.com"
vutuanan_pass = "123456"

try:
    post('http://localhost:8081/api/v1/auth/register', {'fullName': 'Vũ Tuấn An', 'email': vutuanan_email, 'password': vutuanan_pass})
    print(f"Registered fresh account: '{vutuanan_email}'")
except Exception:
    print(f"Account '{vutuanan_email}' already registered.")

student_token = post('http://localhost:8081/api/v1/auth/login', {'email': vutuanan_email, 'password': vutuanan_pass})['data']['token']
print(f"Logged in successfully! Token obtained for {vutuanan_email}")

print("\n=========================================================")
print("  STEP 2: ENROLL IN COURSE AS vutuanan1403@gmail.com")
print("=========================================================")
courses = get('http://localhost:8080/api/v1/courses', student_token)['data']
course_id = courses[0]['id']
course_title = courses[0]['title']
print(f"Selected Course: '{course_title}' (ID: {course_id})")

try:
    enroll_res = post('http://localhost:8080/api/v1/enrollments', {'courseId': course_id}, student_token)['data']
    print(f"--> Enrollment Created!")
    print(f"   Student Name: {enroll_res['studentName']}")
    print(f"   Student Email: {enroll_res['studentEmail']}")
    print(f"   Course: {enroll_res['courseName']}")
except urllib.error.HTTPError as e:
    if e.code == 409:
        print("--> Already enrolled in this course (409 Conflict checked correctly).")

print("\n=========================================================")
print("  STEP 3: STUDENT MY COURSES VERIFICATION (/api/v1/enrollments/me)")
print("=========================================================")
my_courses = get('http://localhost:8080/api/v1/enrollments/me', student_token)['data']
print(f"My Courses count for {vutuanan_email}: {len(my_courses)}")
for mc in my_courses:
    print(f" -> My Course: '{mc['courseName']}' | Email: {mc['studentEmail']} | Name: {mc['studentName']}")

assert len(my_courses) > 0, "My Courses must contain at least 1 course!"
assert my_courses[0]['studentEmail'] == vutuanan_email, f"Expected {vutuanan_email}, got {my_courses[0]['studentEmail']}"

print("\n=========================================================")
print("  STEP 4: ADMIN ENROLLMENT MANAGEMENT VERIFICATION")
print("=========================================================")
admin_token = post('http://localhost:8081/api/v1/auth/login', {'email': 'admin@gmail.com', 'password': '123456'})['data']['token']
admin_enrollments = get('http://localhost:8080/api/v1/admin/enrollments', admin_token)['data']

matching = [e for e in admin_enrollments if e.get('studentEmail') == vutuanan_email]
print(f"Found {len(matching)} enrollment(s) matching '{vutuanan_email}' in Admin Enrollment Management:")
for m in matching:
    print(f" -> Admin Record: Student '{m['studentName']}' ({m['studentEmail']}) | Course: '{m['courseName']}' | Progress: {m['progress']}% | Status: {m['status']}")

assert len(matching) > 0, f"CRITICAL: Admin MUST find enrollment for {vutuanan_email}!"
assert matching[0]['studentEmail'] == vutuanan_email, "Email in Admin list must match student email!"

print("\n=========================================================")
print("  STEP 5: COMPLETE LESSON & VERIFY IMMEDIATE ADMIN PROGRESS SYNC")
print("=========================================================")
lessons = get(f'http://localhost:8080/api/v1/courses/{course_id}/lessons', student_token)['data']
lesson1_id = lessons[0]['id']

# Student completes lesson 1
post('http://localhost:8080/api/v1/progress/complete', {'courseId': course_id, 'lessonId': lesson1_id}, student_token)
print(f"Student completed Lesson 1: '{lessons[0]['title']}'")

# Admin immediately fetches admin enrollments again
admin_enrollments_updated = get('http://localhost:8080/api/v1/admin/enrollments', admin_token)['data']
updated_match = next((e for e in admin_enrollments_updated if e.get('studentEmail') == vutuanan_email and e.get('courseId') == course_id), None)

print(f"--> Admin immediately sees updated progress:")
print(f"   Student: {updated_match['studentName']} ({updated_match['studentEmail']})")
print(f"   Updated Progress: {updated_match['progress']}%")
print(f"   Updated Status: {updated_match['status']}")

assert updated_match['progress'] > 0, "Admin progress must update immediately!"
assert updated_match['status'] == "IN_PROGRESS", "Admin status must update to IN_PROGRESS!"

print("\n=========================================================")
print("  STUDENT AND ADMIN DATA SYNCHRONIZATION TEST PASSED 100%!")
print("=========================================================")
