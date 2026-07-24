package com.englishlms.ai;

import com.englishlms.ai.dto.ChatHistoryResponse;
import com.englishlms.ai.dto.ChatRequest;
import com.englishlms.ai.dto.ChatResponse;
import com.englishlms.ai.dto.GrammarCheckRequest;
import com.englishlms.ai.dto.GrammarCheckResponse;
import com.englishlms.ai.dto.QuizQuestionDto;
import com.englishlms.ai.dto.QuizRequest;
import com.englishlms.ai.entity.ChatHistory;
import com.englishlms.ai.mapper.AiMapper;
import com.englishlms.ai.repository.ChatHistoryRepository;
import com.englishlms.ai.service.impl.AiServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.model.ChatModel;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiServiceTest {

    @Mock
    private ChatHistoryRepository chatHistoryRepository;

    @Mock
    private AiMapper aiMapper;

    @Mock
    private ChatModel chatModel;

    private ObjectMapper objectMapper;
    private AiServiceImpl aiService;

    private final String testEmail = "student@gmail.com";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        aiService = new AiServiceImpl(chatHistoryRepository, aiMapper, objectMapper, chatModel);
    }

    @Test
    @DisplayName("Chat AI thành công và lưu lịch sử")
    void chat_Success() {
        ChatRequest request = ChatRequest.builder().message("Giải thích thì hiện tại đơn").build();
        when(chatHistoryRepository.save(any(ChatHistory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ChatResponse response = aiService.chat(testEmail, request);

        assertNotNull(response);
        assertNotNull(response.getResponse());
        verify(chatHistoryRepository, times(1)).save(any(ChatHistory.class));
    }

    @Test
    @DisplayName("Grammar Checker thành công")
    void checkGrammar_Success() {
        GrammarCheckRequest request = GrammarCheckRequest.builder().text("I has a book").build();
        when(chatHistoryRepository.save(any(ChatHistory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GrammarCheckResponse response = aiService.checkGrammar(testEmail, request);

        assertNotNull(response);
        assertEquals("I has a book", response.getOriginal());
        verify(chatHistoryRepository, times(1)).save(any(ChatHistory.class));
    }

    @Test
    @DisplayName("Quiz Generator thành công")
    void generateQuiz_Success() {
        QuizRequest request = QuizRequest.builder().lesson("Present Simple").numberOfQuestions(5).build();
        when(chatHistoryRepository.save(any(ChatHistory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<QuizQuestionDto> questions = aiService.generateQuiz(testEmail, request);

        assertNotNull(questions);
        assertEquals(5, questions.size());
        verify(chatHistoryRepository, times(1)).save(any(ChatHistory.class));
    }

    @Test
    @DisplayName("Lấy lịch sử Chat thành công")
    void getHistory_Success() {
        ChatHistory history = ChatHistory.builder()
                .id(UUID.randomUUID())
                .userId(UUID.nameUUIDFromBytes(testEmail.getBytes()))
                .prompt("Hello")
                .response("Hi there!")
                .type("CHAT")
                .build();

        ChatHistoryResponse historyDto = ChatHistoryResponse.builder()
                .id(history.getId())
                .userId(history.getUserId())
                .prompt(history.getPrompt())
                .response(history.getResponse())
                .type(history.getType())
                .build();

        when(chatHistoryRepository.findByUserIdOrderByCreatedAtDesc(any(UUID.class)))
                .thenReturn(Collections.singletonList(history));
        when(aiMapper.toChatHistoryResponseList(anyList()))
                .thenReturn(Collections.singletonList(historyDto));

        List<ChatHistoryResponse> result = aiService.getHistory(testEmail);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Hello", result.get(0).getPrompt());
    }
}
