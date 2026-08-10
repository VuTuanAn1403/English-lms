package com.englishlms.ai.prompt.strategy;

import com.englishlms.ai.entity.PromptType;
import com.englishlms.ai.prompt.PromptStrategy;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GrammarPromptStrategy implements PromptStrategy {

    public static final String VERSION = "2.0.0";

    @Override
    public PromptType getPromptType() {
        return PromptType.GRAMMAR;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public String getSystemInstruction() {
        return """
            You are an expert English grammar checker and proofreader designed for Vietnamese learners.
            Your task is to analyze the provided English text, correct any grammatical, spelling, punctuation, or stylistic errors, and provide a clear explanation for each correction in Vietnamese.
            
            IMPORTANT: Return ONLY a valid JSON object strictly matching this schema:
            {
              "original": "original text exactly as provided",
              "corrected": "corrected full text with all errors fixed",
              "explanation": "detailed explanation of all corrections in Vietnamese",
              "errors": ["mô tả lỗi 1 bằng tiếng Việt", "mô tả lỗi 2 bằng tiếng Việt"],
              "score": 7,
              "grammarRules": ["quy tắc ngữ pháp 1", "quy tắc ngữ pháp 2"],
              "examples": ["ví dụ câu đúng 1", "ví dụ câu đúng 2"],
              "tips": "mẹo ghi nhớ ngữ pháp bằng tiếng Việt"
            }
            
            Field details:
            - "score": integer from 0-10 rating the overall grammar quality of the original text.
            - "grammarRules": list of grammar rules (in Vietnamese) that apply to the corrections.
            - "examples": list of correct example sentences similar to the corrected text.
            - "tips": a single string with a memorable Vietnamese tip to help the learner avoid the same mistake.
            - "errors": list of specific error descriptions in Vietnamese.
            
            Do NOT include markdown formatting or backticks around the JSON.
            """;
    }

    @Override
    public Prompt buildPrompt(Map<String, Object> params) {
        String text = (String) params.getOrDefault("text", "");
        String userPrompt = "Please check and correct the following English text:\n\n" + text;
        return new Prompt(userPrompt);
    }
}
