package com.englishlms.ai.prompt;

import com.englishlms.ai.prompt.strategy.QuizPromptStrategy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class QuizPromptTemplate {

    private final QuizPromptStrategy quizPromptStrategy;

    public QuizPromptTemplate(QuizPromptStrategy quizPromptStrategy) {
        this.quizPromptStrategy = quizPromptStrategy != null ? quizPromptStrategy : new QuizPromptStrategy();
    }

    public QuizPromptTemplate() {
        this.quizPromptStrategy = new QuizPromptStrategy();
    }

    public String getVersion() {
        return quizPromptStrategy.getVersion();
    }

    public String getSystemInstruction() {
        return quizPromptStrategy.getSystemInstruction();
    }

    public String buildUserPrompt(String lesson, String cefrLevel, String skillTarget, String difficulty, int numberOfQuestions) {
        Map<String, Object> params = new HashMap<>();
        if (lesson != null) params.put("lesson", lesson);
        if (cefrLevel != null) params.put("cefrLevel", cefrLevel);
        if (skillTarget != null) params.put("skillTarget", skillTarget);
        if (difficulty != null) params.put("difficulty", difficulty);
        params.put("numberOfQuestions", numberOfQuestions);

        return (String) quizPromptStrategy.buildPrompt(params).getContents();
    }

    public String createQuizUserPrompt(String lesson, String cefrLevel, String skillTarget, String difficulty, int numberOfQuestions) {
        return buildUserPrompt(lesson, cefrLevel, skillTarget, difficulty, numberOfQuestions);
    }
}
