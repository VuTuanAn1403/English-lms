package com.englishlms.ai.prompt.strategy;

import com.englishlms.ai.entity.PromptType;
import com.englishlms.ai.prompt.PromptStrategy;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ChatPromptStrategy implements PromptStrategy {

    public static final String VERSION = "1.0.0";

    @Override
    public PromptType getPromptType() {
        return PromptType.CHAT;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public String getSystemInstruction() {
        return """
            You are an expert English language tutor and AI assistant for the English LMS platform.
            Your role is to help students learn English effectively by providing clear explanations, examples, and corrections.
            Always maintain a helpful, encouraging, and professional tone.
            Respond in Vietnamese for explanations, but provide examples and exercises in natural English.
            """;
    }

    @Override
    public Prompt buildPrompt(Map<String, Object> params) {
        String userMessage = (String) params.getOrDefault("message", "");
        return new Prompt(userMessage);
    }
}
