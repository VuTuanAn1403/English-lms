package com.englishlms.ai.prompt;

import com.englishlms.ai.entity.PromptType;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PromptEngine {

    private final Map<PromptType, PromptStrategy> strategyMap = new ConcurrentHashMap<>();

    public PromptEngine(List<PromptStrategy> strategies) {
        if (strategies != null) {
            for (PromptStrategy strategy : strategies) {
                strategyMap.put(strategy.getPromptType(), strategy);
            }
        }
    }

    public PromptStrategy getStrategy(PromptType type) {
        PromptStrategy strategy = strategyMap.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("Không tìm thấy PromptStrategy cho PromptType: " + type);
        }
        return strategy;
    }

    public String getVersion(PromptType type) {
        return getStrategy(type).getVersion();
    }

    public String getSystemInstruction(PromptType type) {
        return getStrategy(type).getSystemInstruction();
    }

    public Prompt createPrompt(PromptType type, Map<String, Object> params) {
        return getStrategy(type).buildPrompt(params);
    }
}
