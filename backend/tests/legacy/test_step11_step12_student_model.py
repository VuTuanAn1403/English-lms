import urllib.request, json, sys

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

email = "vutuanan1403@gmail.com"
password = "123456"

token = post('http://localhost:8081/api/v1/auth/login', {'email': email, 'password': password})['data']['token']

print("=========================================================")
print("  TEST BƯỚC 11: STUDENT MODEL ENGINE (/api/v1/ai/profile)")
print("=========================================================")
profile_res = get('http://localhost:8080/api/v1/ai/profile', token)['data']
print("--> Student Profile Output:")
print(f"   Student: {profile_res['studentName']} ({profile_res['studentEmail']})")
print(f"   Grammar Score: {profile_res['grammarScore']}/10")
print(f"   Vocabulary Score: {profile_res['vocabularyScore']}/10")
print(f"   Listening Score: {profile_res['listeningScore']}/10")
print(f"   Reading Score: {profile_res['readingScore']}/10")
print(f"   Writing Score: {profile_res['writingScore']}/10")
print(f"   Speaking Score: {profile_res['speakingScore']}/10")
print(f"   Learning Speed: {profile_res['learningSpeed']}")
print(f"   Weak Topics: {profile_res['weakTopics']}")
print(f"   Strong Topics: {profile_res['strongTopics']}")
print(f"   Recent Mistakes: {profile_res['recentMistakes']}")
print(f"   Target Goal: {profile_res['currentGoal']}")
assert profile_res['studentEmail'] == email, "Student Profile email must match logged in student!"

print("\n=========================================================")
print("  TEST BƯỚC 12: LEARNING ANALYTICS ENGINE (/api/v1/ai/report)")
print("=========================================================")
report_res = get('http://localhost:8080/api/v1/ai/report', token)['data']
print("--> Learning Report Output:")
print(f"   Overall Completion Rate: {report_res['overallCompletionRate']}%")
print(f"   Teacher Feedback: {report_res['overallTeacherFeedback']}")
print(f"   Action Items: {report_res['actionItems']}")
assert report_res['studentName'] == 'vutuanan1403', "Report studentName must match logged in student!"

print("\n=========================================================")
print("  BƯỚC 11 & BƯỚC 12 VERIFICATIONS PASSED 100%!")
print("=========================================================")
