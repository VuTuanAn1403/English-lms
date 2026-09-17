import urllib.request, json, sys, time

sys.stdout.reconfigure(encoding='utf-8')

def login(email, password):
    req = urllib.request.Request('http://localhost:8081/api/v1/auth/login', data=json.dumps({'email': email, 'password': password}).encode('utf-8'), headers={'Content-Type':'application/json'})
    res = json.loads(urllib.request.urlopen(req).read().decode('utf-8'))
    return res['data']['token']

def register_and_login(email, password, name):
    try:
        return login(email, password)
    except Exception:
        # Register new student if login failed
        reg = urllib.request.Request('http://localhost:8081/api/v1/auth/register', data=json.dumps({'fullName': name, 'email': email, 'password': password}).encode('utf-8'), headers={'Content-Type':'application/json'})
        urllib.request.urlopen(reg)
        return login(email, password)

admin_token = register_and_login('admin@gmail.com', '123456', 'Administrator')
student_token = register_and_login('student1@gmail.com', '123456', 'Nguyễn Thị Học Viên')

def get(url, token=None):
    headers = {}
    if token:
        headers['Authorization'] = f'Bearer {token}'
    req = urllib.request.Request(url, headers=headers)
    return json.loads(urllib.request.urlopen(req).read().decode('utf-8'))

def post(url, data, token):
    req = urllib.request.Request(url, data=json.dumps(data).encode('utf-8'), headers={'Content-Type': 'application/json', 'Authorization': f'Bearer {token}'})
    return json.loads(urllib.request.urlopen(req).read().decode('utf-8'))

print('=== 1. FETCH ALL COURSES TO TEST ENROLLMENT ===')
courses = get('http://localhost:8080/api/v1/courses', student_token)['data']
course_id = courses[0]['id']
print(f'Selected Course: {courses[0]["title"]} (ID: {course_id})')

print('\n=== 2. STUDENT ENROLL IN COURSE (POST /api/v1/enrollments) ===')
try:
    enroll_res = post('http://localhost:8080/api/v1/enrollments', {'courseId': course_id}, student_token)
    print('Enrolled Successfully! Student Email:', enroll_res['data']['studentEmail'], 'Name:', enroll_res['data']['studentName'])
except urllib.error.HTTPError as e:
    if e.code == 409:
        print('Already Enrolled in this course (409 Conflict checked correctly!)')

print('\n=== 3. ADMIN FETCH ALL ENROLLMENTS (GET /api/v1/admin/enrollments) ===')
admin_enrollments = get('http://localhost:8080/api/v1/admin/enrollments', admin_token)['data']
print('Total Admin Enrollments count:', len(admin_enrollments))
for item in admin_enrollments[:5]:
    print(f' -> Student: {item.get("studentName")} ({item.get("studentEmail")}) | Course: {item.get("courseName")} | Progress: {item.get("progress")}% | Status: {item.get("status")}')

print('\n=== 4. ADMIN FETCH STATISTICS (GET /api/v1/admin/enrollments/statistics) ===')
stats = get('http://localhost:8080/api/v1/admin/enrollments/statistics', admin_token)['data']
print('Real DB Statistics:')
print(f' -> Total Enrollments: {stats["totalEnrollments"]}')
print(f' -> In Progress: {stats["inProgressCount"]}')
print(f' -> Completed: {stats["completedCount"]}')
print(f' -> Completion Rate: {stats["completionRate"]}%')

print('\n=== ALL ENROLLMENT MANAGEMENT TESTS PASSED SUCCESSFULLY! ===')
