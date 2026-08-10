# AI SERVICE - TRỢ LÝ AI ASSISTANT

## 1. Mô tả
Tích hợp Google Gemini Provider hỗ trợ Chat AI Assistant theo session, Sửa lỗi Ngữ pháp (Grammar Check), Sinh bài tập trắc nghiệm tự động (Quiz Generator) và Lịch sử hội thoại.

## 2. Thông tin Kỹ thuật
- **Port**: 8083
- **Database**: `ai_db` (PostgreSQL - Port 5434)
- **Swagger UI**: `http://localhost:8083/swagger-ui.html`

## 3. Danh sách Endpoints chính
| Method | Path | Quyền Truy Cập | Mô tả |
| :--- | :--- | :--- | :--- |
| POST | `/api/v1/ai/chat` | Authenticated | Chat AI Assistant theo `sessionId` |
| POST | `/api/v1/ai/grammar-check` | Authenticated | Sửa lỗi ngữ pháp (JSON 8 trường) |
| POST | `/api/v1/ai/generate-quiz` | Authenticated | Sinh bài tập trắc nghiệm 4 lựa chọn |
| GET | `/api/v1/ai/history` | Authenticated | Lịch sử chat AI cá nhân |
| DELETE | `/api/v1/ai/sessions/{sessionId}` | Authenticated | Xóa lịch sử phiên hội thoại |
| GET | `/api/v1/ai/admin/history` | Admin (`ROLE_ADMIN`) | Quản lý toàn bộ lịch sử AI hệ thống |

> Tài liệu API đầy đủ xem tại [docs/04-api.md](../../docs/04-api.md).
