# AI Service

## Mô tả
AI Microservice - Chat AI, Grammar Checker, Quiz Generator sử dụng Spring AI và Google Gemini.

## Port
- 8083

## Database
- ai_db (PostgreSQL)

## API
| Method | Path | Mô tả |
|--------|------|-------|
| POST | /api/v1/ai/chat | Chat AI |
| POST | /api/v1/ai/grammar | Kiểm tra ngữ pháp |
| POST | /api/v1/ai/quiz | Sinh câu hỏi trắc nghiệm |
| GET | /api/v1/ai/history | Lịch sử chat |

## Chạy
```bash
mvn spring-boot:run
```

## Swagger
- http://localhost:8083/swagger-ui.html

## Docker
```bash
docker build -t english-lms/ai-service .
docker run -p 8083:8083 english-lms/ai-service
```
