package com.englishlms.ai.prompt;

import com.englishlms.ai.entity.PromptType;
import com.englishlms.ai.prompt.strategy.ChatPromptStrategy;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ChatPromptTemplate {

    private final ChatPromptStrategy chatPromptStrategy;

    public ChatPromptTemplate(ChatPromptStrategy chatPromptStrategy) {
        this.chatPromptStrategy = chatPromptStrategy != null ? chatPromptStrategy : new ChatPromptStrategy();
    }

    public ChatPromptTemplate() {
        this.chatPromptStrategy = new ChatPromptStrategy();
    }

    public String getVersion() {
        return chatPromptStrategy.getVersion();
    }

    public String getSystemInstruction() {
        return chatPromptStrategy.getSystemInstruction();
    }

    public String createChatUserPrompt(String userMessage) {
        return (String) chatPromptStrategy.buildPrompt(Map.of("message", userMessage)).getContents();
    }
}
