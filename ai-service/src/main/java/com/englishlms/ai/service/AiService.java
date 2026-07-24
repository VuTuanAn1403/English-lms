package com.englishlms.ai.service;

import com.englishlms.ai.dto.ChatHistoryResponse;
import com.englishlms.ai.dto.ChatRequest;
import com.englishlms.ai.dto.ChatResponse;
import com.englishlms.ai.dto.GrammarCheckRequest;
import com.englishlms.ai.dto.GrammarCheckResponse;
import com.englishlms.ai.dto.QuizQuestionDto;
import com.englishlms.ai.dto.QuizRequest;

import java.util.List;

public interface AiService {

    ChatResponse chat(String email, ChatRequest request);

    GrammarCheckResponse checkGrammar(String email, GrammarCheckRequest request);

    List<QuizQuestionDto> generateQuiz(String email, QuizRequest request);

    List<ChatHistoryResponse> getHistory(String email);
}
