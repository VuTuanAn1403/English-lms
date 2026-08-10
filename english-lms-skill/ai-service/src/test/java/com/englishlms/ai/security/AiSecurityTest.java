package com.englishlms.ai.security;

import com.englishlms.ai.controller.AiAdminController;
import com.englishlms.ai.controller.AiController;
import com.englishlms.ai.dto.ChatHistoryResponse;
import com.englishlms.ai.dto.ChatRequest;
import com.englishlms.ai.dto.PageResponse;
import com.englishlms.ai.dto.QuizRequest;
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

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AiSecurityTest {

    private MockMvc mockMvcUser;
    private MockMvc mockMvcAdmin;

    @Mock
    private AiService aiService;

    @InjectMocks
    private AiController aiController;

    @InjectMocks
    private AiAdminController aiAdminController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvcUser = MockMvcBuilders.standaloneSetup(aiController).build();
        mockMvcAdmin = MockMvcBuilders.standaloneSetup(aiAdminController).build();
    }

    @Test
    @DisplayName("GET /api/v1/ai/history - Lịch sử AI chỉ trả về của chính user đăng nhập")
    void getHistory_ReturnsOnlyLoggedInUserHistory() throws Exception {
        PageResponse<ChatHistoryResponse> mockResponse = PageResponse.<ChatHistoryResponse>builder()
                .items(Collections.emptyList())
                .pageNumber(0)
                .pageSize(10)
                .totalElements(0)
                .totalPages(0)
                .isLast(true)
                .build();

        when(aiService.getHistory(eq("student@gmail.com"), isNull(), isNull(), anyInt(), anyInt())).thenReturn(mockResponse);

        mockMvcUser.perform(get("/api/v1/ai/history")
                        .principal(() -> "student@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/ai/admin/history - Admin lấy được toàn bộ lịch sử hệ thống")
    void getAdminHistory_ReturnsAllHistoryForAdmin() throws Exception {
        PageResponse<ChatHistoryResponse> mockResponse = PageResponse.<ChatHistoryResponse>builder()
                .items(Collections.emptyList())
                .pageNumber(0)
                .pageSize(10)
                .totalElements(0)
                .totalPages(0)
                .isLast(true)
                .build();

        when(aiService.getAllHistoryForAdmin(isNull(), isNull(), anyInt(), anyInt())).thenReturn(mockResponse);

        mockMvcAdmin.perform(get("/api/v1/ai/admin/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/ai/chat - Prompt vượt quá 2000 ký tự - Validation 400 Bad Request")
    void chat_PromptTooLong_ReturnsBadRequest() throws Exception {
        String longPrompt = "a".repeat(2001);

        ChatRequest request = ChatRequest.builder()
                .message(longPrompt)
                .build();

        mockMvcUser.perform(post("/api/v1/ai/chat")
                        .principal(() -> "student@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/ai/quiz - Số lượng câu hỏi < 5 - Validation 400 Bad Request")
    void quiz_InvalidQuestionCount_ReturnsBadRequest() throws Exception {
        QuizRequest request = QuizRequest.builder()
                .lesson("Present Simple")
                .numberOfQuestions(2)
                .build();

        mockMvcUser.perform(post("/api/v1/ai/quiz")
                        .principal(() -> "student@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
