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
print("  STEP 1: LOGIN & AUTHENTICATION")
print("=========================================================")
email = "vutuanan1403@gmail.com"
password = "123456"

try:
    post('http://localhost:8081/api/v1/auth/register', {'fullName': 'Vũ Tuấn An', 'email': email, 'password': password})
except Exception:
    pass

token = post('http://localhost:8081/api/v1/auth/login', {'email': email, 'password': password})['data']['token']
print(f"Logged in successfully as {email}!")

print("\n=========================================================")
print("  STEP 2: SPRING AI COACH ADAPTIVE CHAT ('Tôi nên học gì tiếp?')")
print("=========================================================")
chat_res = post('http://localhost:8080/api/v1/ai/chat', {'message': 'Tôi nên học gì tiếp?'}, token)['data']['response']
print("--> AI Coach Response:\n" + chat_res)
assert "English Communication" in chat_res or "Bài" in chat_res or "tiến độ" in chat_res, "AI Coach Chat response must contain personalized advice!"

print("\n=========================================================")
print("  STEP 3: RECOMMENDATION ENGINE (/api/v1/ai/recommendation)")
print("=========================================================")
rec_res = post('http://localhost:8080/api/v1/ai/recommendation', {}, token)['data']
print("--> AI Recommendation Response:")
print(f"   Course: {rec_res['recommendedCourseTitle']}")
print(f"   Lesson: {rec_res['recommendedLessonTitle']}")
print(f"   Target Skill: {rec_res['targetSkill']}")
print(f"   Reasoning: {rec_res['reasoning']}")
assert rec_res['recommendedCourseTitle'] is not None, "Recommended Course Title must not be null!"

print("\n=========================================================")
print("  STEP 4: LEARNING ANALYTICS ENGINE (/api/v1/ai/report)")
print("=========================================================")
report_res = get('http://localhost:8080/api/v1/ai/report', token)['data']
print("--> AI Learning Report Response:")
print(f"   Student Name: {report_res['studentName']}")
print(f"   Overall Completion Rate: {report_res['overallCompletionRate']}%")
print(f"   Strong Skills: {report_res['strongSkills']}")
print(f"   Weak Skills: {report_res['weakSkills']}")
print(f"   Teacher Feedback: {report_res['overallTeacherFeedback']}")
assert report_res['studentName'] == 'vutuanan1403', "Report studentName must match logged in student!"

print("\n=========================================================")
print("  STEP 5: GRAMMAR CHECKER (/api/v1/ai/grammar)")
print("=========================================================")
grammar_res = post('http://localhost:8080/api/v1/ai/grammar', {'text': "He don't likes playing football."}, token)['data']
print("--> Grammar Check Response:")
print(f"   Original: {grammar_res['original']}")
print(f"   Corrected: {grammar_res['corrected']}")
print(f"   Explanation: {grammar_res['explanation']}")
assert "doesn't" in grammar_res['corrected'], "Grammar correction must fix 'don't' to 'doesn't'!"

print("\n=========================================================")
print("  STEP 6: QUIZ GENERATOR (/api/v1/ai/quiz)")
print("=========================================================")
quiz_res = post('http://localhost:8080/api/v1/ai/quiz', {'lesson': 'Present Simple', 'numberOfQuestions': 3}, token)['data']
print(f"--> Quiz Generated: {len(quiz_res)} questions")
for idx, q in enumerate(quiz_res, 1):
    print(f"   Q{idx}: {q['question']}")
assert len(quiz_res) == 3, "Quiz question count must match requested count!"

print("\n=========================================================")
print("  STEP 7: VOCABULARY GENERATOR (/api/v1/ai/vocabulary)")
print("=========================================================")
vocab_res = post('http://localhost:8080/api/v1/ai/vocabulary', {'topic': 'Business Communication', 'count': 2}, token)['data']
print(f"--> Vocabulary Topic: {vocab_res['topic']}")
for item in vocab_res['items']:
    print(f"   Word: {item['word']} {item['ipa']} - {item['meaning']}")
assert len(vocab_res['items']) > 0, "Vocabulary items must not be empty!"

print("\n=========================================================")
print("  STEP 8: RAG KNOWLEDGE BASE SEARCH (/api/v1/ai/rag)")
print("=========================================================")
rag_res = post('http://localhost:8080/api/v1/ai/rag', {'question': 'Cách dùng Skimming và Scanning trong IELTS Reading?'}, token)['data']
print("--> RAG Answer:\n" + rag_res['answer'])
print(f"--> RAG Sources: {rag_res['sources']}")
assert len(rag_res['sources']) > 0, "RAG sources must be present!"

print("\n=========================================================")
print("  SPRING AI LEARNING COACH VERIFICATION PASSED 100%!")
print("=========================================================")
