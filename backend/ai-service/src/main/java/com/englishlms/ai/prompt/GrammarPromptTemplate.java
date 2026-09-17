package com.englishlms.ai.prompt;

import com.englishlms.ai.prompt.strategy.GrammarPromptStrategy;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GrammarPromptTemplate {

    private final GrammarPromptStrategy grammarPromptStrategy;

    public GrammarPromptTemplate(GrammarPromptStrategy grammarPromptStrategy) {
        this.grammarPromptStrategy = grammarPromptStrategy != null ? grammarPromptStrategy : new GrammarPromptStrategy();
    }

    public GrammarPromptTemplate() {
        this.grammarPromptStrategy = new GrammarPromptStrategy();
    }

    public String getVersion() {
        return grammarPromptStrategy.getVersion();
    }

    public String getSystemInstruction() {
        return grammarPromptStrategy.getSystemInstruction();
    }

    public String createGrammarCheckPrompt(String text) {
        return (String) grammarPromptStrategy.buildPrompt(Map.of("text", text)).getContents();
    }

    public String createGrammarUserPrompt(String text) {
        return createGrammarCheckPrompt(text);
    }
}
