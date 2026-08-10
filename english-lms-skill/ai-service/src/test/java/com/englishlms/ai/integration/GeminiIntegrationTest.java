package com.englishlms.ai.integration;

import com.englishlms.ai.dto.ChatRequest;
import com.englishlms.ai.dto.ChatResponse;
import com.englishlms.ai.dto.GrammarCheckRequest;
import com.englishlms.ai.dto.GrammarCheckResponse;
import com.englishlms.ai.dto.QuizQuestionDto;
import com.englishlms.ai.dto.QuizRequest;
import com.englishlms.ai.memory.DbChatMemory;
import com.englishlms.ai.prompt.ChatPromptTemplate;
import com.englishlms.ai.prompt.GrammarPromptTemplate;
import com.englishlms.ai.prompt.QuizPromptTemplate;
import com.englishlms.ai.repository.ChatHistoryRepository;
import com.englishlms.ai.service.impl.AiServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeminiIntegrationTest {

    @Mock
    private ChatHistoryRepository chatHistoryRepository;

    private ChatPromptTemplate chatPromptTemplate;
    private GrammarPromptTemplate grammarPromptTemplate;
    private QuizPromptTemplate quizPromptTemplate;
    private ObjectMapper objectMapper;
    private DbChatMemory dbChatMemory;

    @BeforeEach
    void setUp() {
        chatPromptTemplate = new ChatPromptTemplate();
        grammarPromptTemplate = new GrammarPromptTemplate();
        quizPromptTemplate = new QuizPromptTemplate();
        objectMapper = new ObjectMapper();
        dbChatMemory = new DbChatMemory(chatHistoryRepository);
    }

    @Test
    @DisplayName("Xác minh cấu trúc Prompt Template và ChatClient integration")
    void testChatPromptIntegration() {
        ChatClient chatClient = mock(ChatClient.class, org.mockito.Mockito.RETURNS_DEEP_STUBS);
        AiServiceImpl aiService = new AiServiceImpl(
                chatClient, chatHistoryRepository, chatPromptTemplate, grammarPromptTemplate, quizPromptTemplate, objectMapper, dbChatMemory
        );

        when(chatClient.prompt().system(any(String.class)).messages(any(List.class)).user(any(String.class)).call().content())
                .thenReturn("Xin chào! Tôi có thể hỗ trợ bạn học ngữ pháp và từ vựng tiếng Anh.");

        ChatRequest request = ChatRequest.builder()
                .message("Xin chào Gemini AI")
                .build();

        ChatResponse response = aiService.chat(request, "student@gmail.com");
        assertNotNull(response);
        assertNotNull(response.getResponse());
    }

    @Test
    @DisplayName("Xác minh Grammar Check Prompt Integration")
    void testGrammarPromptIntegration() {
        ChatClient chatClient = mock(ChatClient.class, org.mockito.Mockito.RETURNS_DEEP_STUBS);
        AiServiceImpl aiService = new AiServiceImpl(
                chatClient, chatHistoryRepository, chatPromptTemplate, grammarPromptTemplate, quizPromptTemplate, objectMapper, dbChatMemory
        );

        String jsonResponse = """
            {
              "original": "She go school",
              "corrected": "She goes to school",
              "explanation": "Thêm es cho động từ go và giới từ to",
              "errors": ["Thiếu es", "Thiếu giới từ to"]
            }
            """;

        when(chatClient.prompt().system(any(String.class)).user(any(String.class)).call().content())
                .thenReturn(jsonResponse);

        GrammarCheckRequest request = GrammarCheckRequest.builder()
                .text("She go school")
                .build();

        GrammarCheckResponse response = aiService.grammarCheck(request, "student@gmail.com");
        assertNotNull(response);
        assertNotNull(response.getCorrected());
    }

    @Test
    @DisplayName("Xác minh Quiz Generator Prompt Integration")
    void testQuizPromptIntegration() {
        ChatClient chatClient = mock(ChatClient.class, org.mockito.Mockito.RETURNS_DEEP_STUBS);
        AiServiceImpl aiService = new AiServiceImpl(
                chatClient, chatHistoryRepository, chatPromptTemplate, grammarPromptTemplate, quizPromptTemplate, objectMapper, dbChatMemory
        );

        String jsonResponse = """
            [
              {
                "id": "1",
                "question": "Choose the correct verb: He ___ football.",
                "options": ["play", "plays", "playing", "played"],
                "correctAnswer": 1,
                "explanation": "Chủ ngữ He đi với động từ thêm s/es."
              }
            ]
            """;

        when(chatClient.prompt().system(any(String.class)).user(any(String.class)).call().content())
                .thenReturn(jsonResponse);

        QuizRequest request = QuizRequest.builder()
                .lesson("Present Simple")
                .numberOfQuestions(1)
                .build();

        List<QuizQuestionDto> response = aiService.generateQuiz(request, "student@gmail.com");
        assertNotNull(response);
        assertNotNull(response.get(0).getQuestion());
    }
}
