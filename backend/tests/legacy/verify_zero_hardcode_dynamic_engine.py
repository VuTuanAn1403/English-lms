import urllib.request, json, sys, time

sys.stdout.reconfigure(encoding='utf-8')
time.sleep(3)

def login(email, password):
    req = urllib.request.Request('http://localhost:8081/api/v1/auth/login', data=json.dumps({'email': email, 'password': password}).encode('utf-8'), headers={'Content-Type':'application/json'})
    res = json.loads(urllib.request.urlopen(req).read().decode('utf-8'))
    return res['data']['token']

def register_and_login(email, password, name):
    try:
        return login(email, password)
    except Exception:
        reg = urllib.request.Request('http://localhost:8081/api/v1/auth/register', data=json.dumps({'fullName': name, 'email': email, 'password': password}).encode('utf-8'), headers={'Content-Type':'application/json'})
        urllib.request.urlopen(reg)
        return login(email, password)

def get(url, token=None):
    headers = {}
    if token:
        headers['Authorization'] = f'Bearer {token}'
    req = urllib.request.Request(url, headers=headers)
    return json.loads(urllib.request.urlopen(req).read().decode('utf-8'))

def post(url, data, token):
    req = urllib.request.Request(url, data=json.dumps(data).encode('utf-8'), headers={'Content-Type': 'application/json', 'Authorization': f'Bearer {token}'})
    return json.loads(urllib.request.urlopen(req).read().decode('utf-8'))

def delete(url, token):
    req = urllib.request.Request(url, headers={'Authorization': f'Bearer {token}'}, method='DELETE')
    return json.loads(urllib.request.urlopen(req).read().decode('utf-8'))

admin_token = login('admin@gmail.com', '123456')

print("====================================================================")
print("  PREPARATION: REGISTER FRESH STUDENT & ENROLL IN COURSE WITH 15 LESSONS")
print("====================================================================")
student_email = "student_dynamic_cases@gmail.com"
student_token = register_and_login(student_email, "123456", "Học Viên Case Study")

courses = get('http://localhost:8080/api/v1/courses', student_token)['data']
course = courses[0] # English Communication
course_id = course['id']

lessons = get(f'http://localhost:8080/api/v1/courses/{course_id}/lessons', student_token)['data']
initial_lesson_count = len(lessons)
print(f"Course: '{course['title']}' | Initial Lessons Count: {initial_lesson_count}")

# Enroll
try:
    post('http://localhost:8080/api/v1/enrollments', {'courseId': course_id}, student_token)
except urllib.error.HTTPError:
    pass

# Complete all initial lessons
for l in lessons:
    post('http://localhost:8080/api/v1/progress/complete', {'courseId': course_id, 'lessonId': l['id']}, student_token)

progress1 = get(f'http://localhost:8080/api/v1/progress/course/{course_id}', student_token)['data']
print(f"--> Initial Status: {progress1['completedLessons']}/{progress1['totalLessons']} ({progress1['progressPercent']}%) | Status: {progress1['status']}")
assert progress1['progressPercent'] == 100, "Should be 100% completed"
assert progress1['status'] == "COMPLETED"

print("\n====================================================================")
print("  CASE 1: ADMIN ADDS 16TH LESSON -> PROGRESS AUTOMATICALLY DROPS TO 93%")
print("====================================================================")
new_lesson_req = {
    'courseId': course_id,
    'title': 'Bài 16: Dynamic Engine Test Lesson',
    'content': 'Nội dung bài học thử nghiệm tính động',
    'lessonOrder': initial_lesson_count + 1
}
new_lesson = post('http://localhost:8080/api/v1/lessons', new_lesson_req, admin_token)['data']
new_lesson_id = new_lesson['id']
print(f"Created Lesson 16 (ID: {new_lesson_id})")

# Fetch progress again (No DB restart/re-enrollment needed!)
progress2 = get(f'http://localhost:8080/api/v1/progress/course/{course_id}', student_token)['data']
expected_pct2 = int((initial_lesson_count * 100) / (initial_lesson_count + 1))
print(f"--> Updated Progress after Lesson Add: {progress2['completedLessons']}/{progress2['totalLessons']} ({progress2['progressPercent']}%) | Status: {progress2['status']}")
assert progress2['completedLessons'] == initial_lesson_count
assert progress2['totalLessons'] == initial_lesson_count + 1
assert progress2['progressPercent'] == expected_pct2, f"Expected {expected_pct2}%, got {progress2['progressPercent']}%"
assert progress2['status'] == "IN_PROGRESS", "Status should dynamically change back to IN_PROGRESS"

print("\n====================================================================")
print("  CASE 2: ADMIN DELETES UNCOMPLETED LESSON -> PROGRESS AUTOMATICALLY RE-BOUNCES TO 100%")
print("====================================================================")
delete(f'http://localhost:8080/api/v1/lessons/{new_lesson_id}', admin_token)
print(f"Deleted Lesson 16 (ID: {new_lesson_id})")

progress3 = get(f'http://localhost:8080/api/v1/progress/course/{course_id}', student_token)['data']
print(f"--> Updated Progress after Lesson Delete: {progress3['completedLessons']}/{progress3['totalLessons']} ({progress3['progressPercent']}%) | Status: {progress3['status']}")
assert progress3['completedLessons'] == initial_lesson_count
assert progress3['totalLessons'] == initial_lesson_count
assert progress3['progressPercent'] == 100
assert progress3['status'] == "COMPLETED", "Status should dynamically change back to COMPLETED"

print("\n====================================================================")
print("  CASE 3: COURSE WITH 0 LESSONS -> PROGRESS IS 0%, NO DIVIDE BY ZERO")
print("====================================================================")
empty_course_req = {
    'title': 'Empty Course Test - Khóa Học Rỗng',
    'description': 'Khóa học chưa có bài học nào để kiểm thử tính toán 0',
    'level': 'BEGINNER',
    'imageUrl': 'https://images.unsplash.com/photo-1546410531-bb4caa6b424d'
}
empty_course = post('http://localhost:8080/api/v1/courses', empty_course_req, admin_token)['data']
empty_course_id = empty_course['id']
print(f"Created Empty Course (ID: {empty_course_id})")

empty_enroll = post('http://localhost:8080/api/v1/enrollments', {'courseId': empty_course_id}, student_token)['data']
print(f"--> Enrolled in Empty Course:")
print(f"   Completed Lessons: {empty_enroll['completedLessons']}")
print(f"   Total Lessons: {empty_enroll['totalLessons']}")
print(f"   Progress: {empty_enroll['progress']}%")
print(f"   Status: {empty_enroll['status']}")

assert empty_enroll['completedLessons'] == 0
assert empty_enroll['totalLessons'] == 0
assert empty_enroll['progress'] == 0
assert empty_enroll['status'] == "NOT_STARTED"

# Clean up empty test course
delete(f'http://localhost:8080/api/v1/courses/{empty_course_id}', admin_token)
print("Cleaned up empty test course.")

print("\n====================================================================")
print("  ALL CASE STUDIES & REGRESSION TESTS PASSED 100% PERFECTLY!")
print("====================================================================")
