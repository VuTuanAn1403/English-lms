package com.englishlms.ai.prompt;

import com.englishlms.ai.entity.PromptType;
import com.englishlms.ai.prompt.strategy.ChatPromptStrategy;
import com.englishlms.ai.prompt.strategy.GrammarPromptStrategy;
import com.englishlms.ai.prompt.strategy.QuizPromptStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PromptEngineTest {

    private PromptEngine promptEngine;

    @BeforeEach
    void setUp() {
        ChatPromptStrategy chatStrategy = new ChatPromptStrategy();
        GrammarPromptStrategy grammarStrategy = new GrammarPromptStrategy();
        QuizPromptStrategy quizStrategy = new QuizPromptStrategy();

        promptEngine = new PromptEngine(List.of(chatStrategy, grammarStrategy, quizStrategy));
    }

    @Test
    @DisplayName("Kiểm tra đăng ký và lấy Strategy theo PromptType")
    void testGetStrategy() {
        PromptStrategy chatStrategy = promptEngine.getStrategy(PromptType.CHAT);
        assertNotNull(chatStrategy);
        assertEquals(PromptType.CHAT, chatStrategy.getPromptType());
        assertEquals("1.0.0", promptEngine.getVersion(PromptType.CHAT));

        PromptStrategy grammarStrategy = promptEngine.getStrategy(PromptType.GRAMMAR);
        assertNotNull(grammarStrategy);
        assertEquals(PromptType.GRAMMAR, grammarStrategy.getPromptType());
        assertEquals("2.0.0", promptEngine.getVersion(PromptType.GRAMMAR));

        PromptStrategy quizStrategy = promptEngine.getStrategy(PromptType.QUIZ);
        assertNotNull(quizStrategy);
        assertEquals(PromptType.QUIZ, quizStrategy.getPromptType());
        assertEquals("2.0.0", promptEngine.getVersion(PromptType.QUIZ));
    }

    @Test
    @DisplayName("Kiểm tra sinh Prompt qua PromptEngine với tham số động")
    void testCreatePrompt() {
        Prompt chatPrompt = promptEngine.createPrompt(PromptType.CHAT, Map.of("message", "Test Chat"));
        assertNotNull(chatPrompt);
        assertEquals("Test Chat", chatPrompt.getContents());

        Prompt grammarPrompt = promptEngine.createPrompt(PromptType.GRAMMAR, Map.of("text", "He go home"));
        assertNotNull(grammarPrompt);
        assertTrue(grammarPrompt.getContents().contains("He go home"));

        Prompt quizPrompt = promptEngine.createPrompt(PromptType.QUIZ, Map.of(
                "lesson", "Past Simple",
                "cefrLevel", "A2",
                "numberOfQuestions", 3
        ));
        assertNotNull(quizPrompt);
        assertTrue(quizPrompt.getContents().contains("Past Simple"));
    }
}
