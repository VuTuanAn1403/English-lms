package com.englishlms.ai.config;

/**
 * Quản lý các Prompt Template riêng biệt cho AI Service.
 * Không hard-code prompt trong Controller hoặc Service.
 */
public class PromptTemplates {

    public static final String CHAT_SYSTEM_PROMPT = """
            Bạn là trợ lý học tiếng Anh thông minh và thân thiện trong hệ thống English LMS.
            Hãy trả lời người học bằng tiếng Việt một cách dễ hiểu, chính xác, có tính khuyến khích học tập.
            Nếu người dùng hỏi về tiếng Anh (từ vựng, ngữ pháp, phát âm, giao tiếp), hãy đưa ra ví dụ minh họa kèm nghĩa tiếng Việt.
            """;

    public static final String GRAMMAR_CHECK_PROMPT = """
            Hãy kiểm tra ngữ pháp cho văn bản tiếng Anh sau đây:
            "%s"
            
            Trả về duy nhất một chuỗi JSON hợp lệ (không kèm theo bất kỳ văn bản Markdown hay trích dẫn nào khác) theo định dạng:
            {
              "original": "%s",
              "corrected": "<câu đã sửa lỗi ngữ pháp>",
              "explanation": "<giải thích chi tiết các lỗi bằng tiếng Việt>"
            }
            """;

    public static final String QUIZ_GENERATOR_PROMPT = """
            Hãy tạo một bài trắc nghiệm gồm %d câu hỏi tiếng Anh về chủ đề/bài học: "%s".
            
            Trả về duy nhất một mảng JSON hợp lệ (không kèm theo bất kỳ văn bản Markdown hay trích dẫn nào khác) theo định dạng mảng:
            [
              {
                "question": "<Nội dung câu hỏi tiếng Anh>",
                "options": ["Option A", "Option B", "Option C", "Option D"],
                "answer": "<Đáp án đúng, ví dụ: Option A>",
                "explanation": "<Giải thích đáp án bằng tiếng Việt>"
              }
            ]
            """;

    private PromptTemplates() {
    }
}
