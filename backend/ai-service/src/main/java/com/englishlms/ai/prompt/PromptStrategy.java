package com.englishlms.ai.prompt;

import com.englishlms.ai.entity.PromptType;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.Map;

public interface PromptStrategy {

    PromptType getPromptType();

    String getVersion();

    String getSystemInstruction();

    Prompt buildPrompt(Map<String, Object> params);
}
