import urllib.request, json, sys, time

sys.stdout.reconfigure(encoding='utf-8')

# Login Admin
try:
    admin_login = urllib.request.Request('http://localhost:8081/api/v1/auth/login', data=json.dumps({'email':'admin@gmail.com','password':'123456'}).encode('utf-8'), headers={'Content-Type':'application/json'})
    admin_res = json.loads(urllib.request.urlopen(admin_login).read().decode('utf-8'))
except Exception as e:
    admin_login = urllib.request.Request('http://localhost:8080/api/v1/auth/login', data=json.dumps({'email':'admin@gmail.com','password':'123456'}).encode('utf-8'), headers={'Content-Type':'application/json'})
    admin_res = json.loads(urllib.request.urlopen(admin_login).read().decode('utf-8'))

admin_token = admin_res['data']['token']

def get(url, token=None):
    headers = {}
    if token:
        headers['Authorization'] = f'Bearer {token}'
    req = urllib.request.Request(url, headers=headers)
    return json.loads(urllib.request.urlopen(req).read().decode('utf-8'))

def post(url, data, token):
    req = urllib.request.Request(url, data=json.dumps(data).encode('utf-8'), headers={'Content-Type': 'application/json', 'Authorization': f'Bearer {token}'})
    return json.loads(urllib.request.urlopen(req).read().decode('utf-8'))

def put(url, data, token):
    req = urllib.request.Request(url, data=json.dumps(data).encode('utf-8'), headers={'Content-Type': 'application/json', 'Authorization': f'Bearer {token}'}, method='PUT')
    return json.loads(urllib.request.urlopen(req).read().decode('utf-8'))

def patch(url, data, token):
    req = urllib.request.Request(url, data=json.dumps(data).encode('utf-8') if data else b'', headers={'Content-Type': 'application/json', 'Authorization': f'Bearer {token}'}, method='PATCH')
    return json.loads(urllib.request.urlopen(req).read().decode('utf-8'))

def delete(url, token):
    req = urllib.request.Request(url, headers={'Authorization': f'Bearer {token}'}, method='DELETE')
    return json.loads(urllib.request.urlopen(req).read().decode('utf-8'))

print('=== 1. GET ALL COURSES ===')
courses = get('http://localhost:8080/api/v1/courses', admin_token)['data']
course_id = courses[0]['id']
print(f'Selected Course: {courses[0]["title"]} (ID: {course_id})')

print('\n=== 2. GET PAGINATED LESSONS (GET /api/v1/lessons) ===')
lessons_page = get('http://localhost:8080/api/v1/lessons?page=0&size=5', admin_token)
print('Total Lessons:', lessons_page['data']['totalElements'], 'Page items:', len(lessons_page['data']['items']))

print('\n=== 3. GET LESSONS BY COURSE (GET /api/v1/courses/{courseId}/lessons) ===')
course_lessons = get(f'http://localhost:8080/api/v1/courses/{course_id}/lessons', admin_token)
print('Course Lessons Count:', len(course_lessons['data']))

print('\n=== 4. CREATE NEW LESSON (POST /api/v1/lessons) ===')
new_lesson_data = {
    'courseId': course_id,
    'title': 'Lesson 99: Advanced Business Vocabulary & Negotiations',
    'description': 'Tập trung luyện tập các từ vựng thương lượng đàm phán cấp cao.',
    'content': 'Nội dung chi tiết về các cụm từ đàm phán hợp đồng trong tiếng Anh thương mại...',
    'videoUrl': 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
    'documentUrl': 'https://example.com/negotiation-guide.pdf',
    'duration': 25,
    'orderIndex': 99,
    'isPublished': True
}
create_res = post('http://localhost:8080/api/v1/lessons', new_lesson_data, admin_token)
created_id = create_res['data']['id']
print('Created Lesson ID:', created_id, 'Title:', create_res['data']['title'])

print('\n=== 5. GET LESSON DETAIL (GET /api/v1/lessons/{id}) ===')
detail_res = get(f'http://localhost:8080/api/v1/lessons/{created_id}', admin_token)
print('Lesson Detail Title:', detail_res['data']['title'], 'Duration:', detail_res['data']['duration'])

print('\n=== 6. UPDATE LESSON (PUT /api/v1/lessons/{id}) ===')
update_data = {
    'courseId': course_id,
    'title': 'Lesson 99: Advanced Business Negotiations (Updated)',
    'description': 'Cập nhật mô tả kĩ năng đàm phán hợp đồng.',
    'duration': 30,
    'isPublished': True
}
update_res = put(f'http://localhost:8080/api/v1/lessons/{created_id}', update_data, admin_token)
print('Updated Title:', update_res['data']['title'], 'Updated Duration:', update_res['data']['duration'])

print('\n=== 7. MOVE UP LESSON (PATCH /api/v1/lessons/{id}/move-up) ===')
move_up_res = patch(f'http://localhost:8080/api/v1/lessons/{created_id}/move-up', None, admin_token)
print('Move up order:', move_up_res['data']['orderIndex'])

print('\n=== 8. MOVE DOWN LESSON (PATCH /api/v1/lessons/{id}/move-down) ===')
move_down_res = patch(f'http://localhost:8080/api/v1/lessons/{created_id}/move-down', None, admin_token)
print('Move down order:', move_down_res['data']['orderIndex'])

print('\n=== 9. DELETE LESSON (DELETE /api/v1/lessons/{id}) ===')
del_res = delete(f'http://localhost:8080/api/v1/lessons/{created_id}', admin_token)
print('Delete Message:', del_res['message'])

print('\n=== ALL LESSON MANAGEMENT API TESTS PASSED SUCCESSFULLY! ===')
