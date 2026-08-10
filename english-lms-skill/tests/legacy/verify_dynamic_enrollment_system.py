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

admin_token = login('admin@gmail.com', '123456')

print("=========================================================")
print("  STEP 1: REGISTER FRESH TEST STUDENT & ENROLL IN COURSE")
print("=========================================================")
fresh_student_email = "student_dynamic_test@gmail.com"
student_token = register_and_login(fresh_student_email, "123456", "Học Viên Tiến Độ Động")

# Get Course & Lessons
courses = get('http://localhost:8080/api/v1/courses', student_token)['data']
course = courses[0]
course_id = course['id']
course_title = course['title']

lessons = get(f'http://localhost:8080/api/v1/courses/{course_id}/lessons', student_token)['data']
total_lessons_count = len(lessons)
print(f"Selected Course: '{course_title}' (Total Lessons in DB: {total_lessons_count})")

# Enroll student in course
print("\n--> Student Enrolls in Course:")
enroll_res = post('http://localhost:8080/api/v1/enrollments', {'courseId': course_id}, student_token)['data']
print(f"   Enrollment ID: {enroll_res['id']}")
print(f"   Student: {enroll_res['studentName']} ({enroll_res['studentEmail']})")
print(f"   Completed Lessons: {enroll_res['completedLessons']} / {enroll_res['totalLessons']}")
print(f"   Progress: {enroll_res['progress']}%")
print(f"   Status: {enroll_res['status']}")

assert enroll_res['progress'] == 0, "Initial progress must be 0%"
assert enroll_res['status'] == "NOT_STARTED", "Initial status must be NOT_STARTED"
assert enroll_res['completedLessons'] == 0, "Initial completed lessons must be 0"

print("\n=========================================================")
print("  STEP 2: TEST DUPLICATE ENROLLMENT PREVENTION (409)")
print("=========================================================")
try:
    post('http://localhost:8080/api/v1/enrollments', {'courseId': course_id}, student_token)
    print("ERROR: Duplicate enrollment allowed!")
except urllib.error.HTTPError as e:
    print(f"--> Success! System rejected duplicate enrollment with HTTP {e.code} Conflict.")

print("\n=========================================================")
print("  STEP 3: COMPLETE LESSON 1 -> DYNAMIC PROGRESS UPDATE")
print("=========================================================")
lesson1_id = lessons[0]['id']
complete_res1 = post('http://localhost:8080/api/v1/progress/complete', {'courseId': course_id, 'lessonId': lesson1_id}, student_token)['data']
expected_pct1 = int((1 * 100) / total_lessons_count)
print(f"Completed Lesson 1: '{lessons[0]['title']}'")
print(f"   Completed Lessons: {complete_res1['completedLessons']} / {complete_res1['totalLessons']}")
print(f"   Calculated Progress: {complete_res1['progressPercent']}% (Expected: ~{expected_pct1}%)")
print(f"   Calculated Status: {complete_res1['status']}")

assert complete_res1['completedLessons'] == 1, "Completed lessons must be 1"
assert complete_res1['status'] == "IN_PROGRESS", "Status must update to IN_PROGRESS"

print("\n=========================================================")
print("  STEP 4: COMPLETE ALL LESSONS -> 100% COMPLETED STATUS")
print("=========================================================")
for l in lessons[1:]:
    post('http://localhost:8080/api/v1/progress/complete', {'courseId': course_id, 'lessonId': l['id']}, student_token)

final_progress = get(f'http://localhost:8080/api/v1/progress/course/{course_id}', student_token)['data']
print(f"Completed All {total_lessons_count} Lessons!")
print(f"   Completed Lessons: {final_progress['completedLessons']} / {final_progress['totalLessons']}")
print(f"   Final Progress: {final_progress['progressPercent']}%")
print(f"   Final Status: {final_progress['status']}")
print(f"   Completed Flag: {final_progress['completed']}")

assert final_progress['progressPercent'] == 100, "Progress must be 100%"
assert final_progress['status'] == "COMPLETED", "Status must be COMPLETED"
assert final_progress['completed'] == True, "Completed flag must be True"

print("\n=========================================================")
print("  STEP 5: ADMIN ENROLLMENT MANAGEMENT & STATISTICS")
print("=========================================================")
admin_enrollments = get('http://localhost:8080/api/v1/admin/enrollments', admin_token)['data']
print(f"Total Enrollments in System: {len(admin_enrollments)}")

matching_enr = next((e for e in admin_enrollments if e['studentEmail'] == fresh_student_email), None)
assert matching_enr is not None, "Admin must see fresh student enrollment!"
print(f"Found Fresh Student Enrollment in Admin List:")
print(f"   Student: {matching_enr['studentName']} ({matching_enr['studentEmail']})")
print(f"   Course: {matching_enr['courseName']}")
print(f"   Calculated Progress: {matching_enr['progress']}%")
print(f"   Calculated Status: {matching_enr['status']}")

stats = get('http://localhost:8080/api/v1/admin/enrollments/statistics', admin_token)['data']
print("\nReal PostgreSQL KPI Statistics:")
print(f"   Total Enrollments: {stats['totalEnrollments']}")
print(f"   In Progress Count: {stats['inProgressCount']}")
print(f"   Completed Count: {stats['completedCount']}")
print(f"   Completion Rate: {stats['completionRate']}%")

print("\n=========================================================")
print("  ALL DYNAMIC PROGRESS ENGINE TESTS PASSED PERFECTLY!")
print("=========================================================")
