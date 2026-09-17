import urllib.request, json, sys, time

sys.stdout.reconfigure(encoding='utf-8')

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

print("=== STEP 1: REGISTER & LOGIN vutuanan1403@gmail.com ===")
try:
    post('http://localhost:8081/api/v1/auth/register', {'fullName': 'Vũ Tuấn An', 'email': 'vutuanan1403@gmail.com', 'password': 'password123'})
    print("Registered vutuanan1403@gmail.com")
except Exception as e:
    print("User already registered or error:", e)

login_res = post('http://localhost:8081/api/v1/auth/login', {'email': 'vutuanan1403@gmail.com', 'password': 'password123'})
student_token = login_res['data']['token']
print("Logged in student token obtained.")

print("\n=== STEP 2: STUDENT ENROLLS IN COURSE ===")
courses = get('http://localhost:8080/api/v1/courses', student_token)['data']
course_id = courses[0]['id']
print(f"Enrolling in course: {courses[0]['title']} ({course_id})")

try:
    enroll_res = post('http://localhost:8080/api/v1/enrollments', {'courseId': course_id}, student_token)['data']
    print("Enroll Response:", enroll_res)
except urllib.error.HTTPError as e:
    print("Enroll HTTP Error:", e.code, e.read().decode('utf-8'))

print("\n=== STEP 3: STUDENT FETCHES MY COURSES ===")
my_courses = get('http://localhost:8080/api/v1/enrollments/me', student_token)['data']
print(f"My Courses Count for vutuanan1403@gmail.com: {len(my_courses)}")
for mc in my_courses:
    print(f" -> Course: {mc.get('courseName')} | Student Email: {mc.get('studentEmail')} | Name: {mc.get('studentName')}")

print("\n=== STEP 4: ADMIN FETCHES ADMIN ENROLLMENTS ===")
admin_token = post('http://localhost:8081/api/v1/auth/login', {'email': 'admin@gmail.com', 'password': '123456'})['data']['token']
admin_enrollments = get('http://localhost:8080/api/v1/admin/enrollments', admin_token)['data']
print(f"Admin Total Enrollments Count: {len(admin_enrollments)}")

found = [e for e in admin_enrollments if e.get('studentEmail') == 'vutuanan1403@gmail.com']
print(f"\nEnrollments matching 'vutuanan1403@gmail.com' in Admin API response: {len(found)}")
for f in found:
    print(" -> FOUND ENROLLMENT IN ADMIN LIST:", f)

if not found:
    print("\n[CRITICAL BUG FOUND] 'vutuanan1403@gmail.com' IS MISSING FROM ADMIN ENROLLMENTS LIST!")
