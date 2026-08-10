package com.englishlms.ai.service;

import com.englishlms.ai.dto.ChatHistoryResponse;
import com.englishlms.ai.dto.ChatRequest;
import com.englishlms.ai.dto.ChatResponse;
import com.englishlms.ai.dto.GrammarCheckRequest;
import com.englishlms.ai.dto.GrammarCheckResponse;
import com.englishlms.ai.dto.PageResponse;
import com.englishlms.ai.dto.QuizQuestionDto;
import com.englishlms.ai.dto.QuizRequest;

import java.util.List;

public interface AiService {

    ChatResponse chat(ChatRequest request, String userEmail);

    GrammarCheckResponse grammarCheck(GrammarCheckRequest request, String userEmail);

    List<QuizQuestionDto> generateQuiz(QuizRequest request, String userEmail);

    PageResponse<ChatHistoryResponse> getHistory(String userEmail, String promptType, String sessionId, int page, int size);

    PageResponse<ChatHistoryResponse> getAllHistoryForAdmin(String search, String promptTypeStr, int page, int size);

    void deleteSession(String sessionId, String userEmail);

    void clearSession(String sessionId);
}
