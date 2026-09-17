package com.englishlms.ai.controller;

import com.englishlms.ai.dto.ChatHistoryResponse;
import com.englishlms.ai.dto.ChatRequest;
import com.englishlms.ai.dto.ChatResponse;
import com.englishlms.ai.dto.GrammarCheckRequest;
import com.englishlms.ai.dto.GrammarCheckResponse;
import com.englishlms.ai.dto.PageResponse;
import com.englishlms.ai.dto.QuizQuestionDto;
import com.englishlms.ai.dto.QuizRequest;
import com.englishlms.ai.entity.PromptType;
import com.englishlms.ai.service.AiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AiService aiService;

    @InjectMocks
    private AiController aiController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(aiController).build();
    }

    @Test
    @DisplayName("POST /api/v1/ai/chat - Gọi API Chat AI thành công")
    void chat_ApiSuccess() throws Exception {
        ChatRequest request = ChatRequest.builder()
                .message("Hello AI")
                .sessionId("session-123")
                .build();

        ChatResponse response = ChatResponse.builder()
                .response("Hello Student")
                .sessionId("session-123")
                .timestamp(LocalDateTime.now())
                .build();

        when(aiService.chat(any(ChatRequest.class), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/ai/chat")
                        .principal(() -> "student@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.response").value("Hello Student"));
    }

    @Test
    @DisplayName("POST /api/v1/ai/grammar - Gọi API Grammar Check thành công")
    void grammar_ApiSuccess() throws Exception {
        GrammarCheckRequest request = GrammarCheckRequest.builder()
                .text("He go to school")
                .build();

        GrammarCheckResponse response = GrammarCheckResponse.builder()
                .original("He go to school")
                .corrected("He goes to school")
                .explanation("Thêm es cho chủ ngữ He")
                .errors(List.of("Thiếu es"))
                .score(6)
                .grammarRules(List.of("Subject-verb agreement"))
                .examples(List.of("She goes to work"))
                .tips("He/She/It + Vs/es")
                .build();

        when(aiService.grammarCheck(any(GrammarCheckRequest.class), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/ai/grammar")
                        .principal(() -> "student@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.corrected").value("He goes to school"))
                .andExpect(jsonPath("$.data.score").value(6))
                .andExpect(jsonPath("$.data.grammarRules[0]").value("Subject-verb agreement"));
    }

    @Test
    @DisplayName("POST /api/v1/ai/quiz - Gọi API Quiz Generator thành công với correctAnswerIndex")
    void quiz_ApiSuccess() throws Exception {
        QuizRequest request = QuizRequest.builder()
                .lesson("Present Simple")
                .numberOfQuestions(5)
                .build();

        QuizQuestionDto dto = QuizQuestionDto.builder()
                .id("1")
                .question("She ___ to school")
                .options(List.of("go", "goes", "went", "going"))
                .correctAnswerIndex(1)
                .explanation("She + goes")
                .build();

        when(aiService.generateQuiz(any(QuizRequest.class), any())).thenReturn(List.of(dto));

        mockMvc.perform(post("/api/v1/ai/quiz")
                        .principal(() -> "student@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].correctAnswerIndex").value(1))
                .andExpect(jsonPath("$.data[0].options.length()").value(4));
    }

    @Test
    @DisplayName("GET /api/v1/ai/history - Gọi API History thành công")
    void history_ApiSuccess() throws Exception {
        ChatHistoryResponse item = ChatHistoryResponse.builder()
                .id(UUID.randomUUID())
                .userEmail("student@gmail.com")
                .promptType(PromptType.CHAT)
                .userPrompt("Hi")
                .aiResponse("Hello")
                .build();

        PageResponse<ChatHistoryResponse> pageResponse = PageResponse.<ChatHistoryResponse>builder()
                .items(List.of(item))
                .pageNumber(0)
                .pageSize(10)
                .totalElements(1)
                .totalPages(1)
                .isLast(true)
                .build();

        when(aiService.getHistory(any(), isNull(), isNull(), anyInt(), anyInt())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/ai/history")
                        .principal(() -> "student@gmail.com")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items[0].userPrompt").value("Hi"));
    }
}
