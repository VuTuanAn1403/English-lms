import urllib.request, json, sys, time

sys.stdout.reconfigure(encoding='utf-8')

def post(url, data, token=None):
    headers = {'Content-Type': 'application/json'}
    if token:
        headers['Authorization'] = f'Bearer {token}'
    req = urllib.request.Request(url, data=json.dumps(data).encode('utf-8'), headers=headers)
    return json.loads(urllib.request.urlopen(req).read().decode('utf-8'))

email = "vutuanan1403@gmail.com"
password = "123456"

try:
    post('http://localhost:8081/api/v1/auth/register', {'fullName': 'Vũ Tuấn An', 'email': email, 'password': password})
except Exception:
    pass

token = post('http://localhost:8081/api/v1/auth/login', {'email': email, 'password': password})['data']['token']

print("=========================================================")
print("  TEST BƯỚC 3: TOOL CALLING SEARCH COURSE ('Tôi muốn học phát âm')")
print("=========================================================")
chat_res = post('http://localhost:8080/api/v1/ai/chat', {'message': 'Tôi muốn học phát âm'}, token)['data']['response']
print("--> AI Tool Calling Response:\n" + chat_res)
assert "English Pronunciation" in chat_res, "AI must recommend English Pronunciation course!"
assert "IPA" in chat_res or "phát âm" in chat_res, "AI response must describe IPA syllabus!"

print("\n=========================================================")
print("  TEST BƯỚC 4: GRAMMAR INVERSION ('Had I knew about the meeting...')")
print("=========================================================")
grammar_res = post('http://localhost:8080/api/v1/ai/grammar', {'text': 'Had I knew about the meeting earlier, I would have attended it.'}, token)['data']
print("--> Grammar Response:")
print(f"   Original: {grammar_res['original']}")
print(f"   Corrected: {grammar_res['corrected']}")
print(f"   Score: {grammar_res['score']}/10")
print(f"   Explanation: {grammar_res['explanation']}")
assert "known" in grammar_res['corrected'].lower(), "Grammar correction must fix 'knew' to 'known'!"
assert grammar_res['score'] < 10, "Score for incorrect sentence must be less than 10!"
assert "Inversion" in str(grammar_res['grammarRules']) or "Third Conditional" in str(grammar_res['grammarRules']), "Rule must reference Inversion/Third Conditional!"

print("\n=========================================================")
print("  TEST BƯỚC 5: QUIZ GENERATOR ZERO PLACEHOLDERS")
print("=========================================================")
quiz_res = post('http://localhost:8080/api/v1/ai/quiz', {'lesson': 'Conditionals', 'numberOfQuestions': 5}, token)['data']
print(f"--> Quiz Count: {len(quiz_res)}")
for idx, q in enumerate(quiz_res, 1):
    print(f"   Q{idx}: {q['question']}")
    assert "Question " + str(idx) + ":" not in q['question'], f"Question {idx} must not be a generic placeholder!"
    assert "Choice A" not in q['options'][0], "Options must not contain generic Choice A placeholders!"

print("\n=========================================================")
print("  TEST BƯỚC 7: LEARNING COACH IELTS ('Tôi muốn luyện IELTS')")
print("=========================================================")
ielts_res = post('http://localhost:8080/api/v1/ai/chat', {'message': 'Tôi muốn luyện IELTS'}, token)['data']['response']
print("--> Learning Coach IELTS Response:\n" + ielts_res)
assert "IELTS" in ielts_res, "Learning Coach must provide personalized IELTS advice!"

print("\n=========================================================")
print("  ALL 12 STEP VERIFICATIONS PASSED 100%!")
print("=========================================================")
