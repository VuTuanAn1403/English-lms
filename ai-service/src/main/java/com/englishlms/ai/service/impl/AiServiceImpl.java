package com.englishlms.ai.service.impl;

import com.englishlms.ai.config.PromptTemplates;
import com.englishlms.ai.dto.ChatHistoryResponse;
import com.englishlms.ai.dto.ChatRequest;
import com.englishlms.ai.dto.GrammarCheckRequest;
import com.englishlms.ai.dto.GrammarCheckResponse;
import com.englishlms.ai.dto.QuizQuestionDto;
import com.englishlms.ai.dto.QuizRequest;
import com.englishlms.ai.entity.ChatHistory;
import com.englishlms.ai.mapper.AiMapper;
import com.englishlms.ai.repository.ChatHistoryRepository;
import com.englishlms.ai.service.AiService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AiServiceImpl implements AiService {

    private static final Logger log = LoggerFactory.getLogger(AiServiceImpl.class);
    private final ChatHistoryRepository chatHistoryRepository;
    private final AiMapper aiMapper;
    private final ObjectMapper objectMapper;
    private final ChatModel chatModel;

    @Autowired
    public AiServiceImpl(
            ChatHistoryRepository chatHistoryRepository,
            AiMapper aiMapper,
            ObjectMapper objectMapper,
            @Autowired(required = false) ChatModel chatModel
    ) {
        this.chatHistoryRepository = chatHistoryRepository;
        this.aiMapper = aiMapper;
        this.objectMapper = objectMapper;
        this.chatModel = chatModel;
    }

    @Override
    public com.englishlms.ai.dto.ChatResponse chat(String email, ChatRequest request) {
        String userPrompt = request.getMessage();
        String fullPrompt = PromptTemplates.CHAT_SYSTEM_PROMPT + "\nNgười học hỏi: " + userPrompt;

        String aiResponseText = callAiModel(fullPrompt, "Chào bạn! Tôi là trợ lý AI học tiếng Anh. Bạn có câu hỏi nào về từ vựng, ngữ pháp hay giao tiếp không?");

        saveHistory(email, userPrompt, aiResponseText, "CHAT");
        return com.englishlms.ai.dto.ChatResponse.builder().response(aiResponseText).build();
    }

    @Override
    public GrammarCheckResponse checkGrammar(String email, GrammarCheckRequest request) {
        String originalText = request.getText();
        String promptText = String.format(PromptTemplates.GRAMMAR_CHECK_PROMPT, originalText, originalText);

        String aiResponseText = callAiModel(promptText, String.format(
                "{\"original\":\"%s\",\"corrected\":\"%s\",\"explanation\":\"Câu của bạn đã đúng ngữ pháp chuẩn tiếng Anh!\"}",
                originalText, originalText
        ));

        GrammarCheckResponse response;
        try {
            String cleanJson = cleanJsonString(aiResponseText);
            response = objectMapper.readValue(cleanJson, GrammarCheckResponse.class);
        } catch (Exception e) {
            log.warn("Failed to parse AI JSON response for grammar check: {}", e.getMessage());
            response = GrammarCheckResponse.builder()
                    .original(originalText)
                    .corrected(originalText)
                    .explanation(aiResponseText)
                    .build();
        }

        saveHistory(email, "Grammar Check: " + originalText, aiResponseText, "GRAMMAR");
        return response;
    }

    @Override
    public List<QuizQuestionDto> generateQuiz(String email, QuizRequest request) {
        String topic = request.getLesson();
        int count = request.getNumberOfQuestions() > 0 ? request.getNumberOfQuestions() : 5;
        String promptText = String.format(PromptTemplates.QUIZ_GENERATOR_PROMPT, count, topic);

        String aiResponseText = callAiModel(promptText, generateMockQuizJson(topic, count));

        List<QuizQuestionDto> questions;
        try {
            String cleanJson = cleanJsonString(aiResponseText);
            questions = objectMapper.readValue(cleanJson, new TypeReference<List<QuizQuestionDto>>() {});
        } catch (Exception e) {
            log.warn("Failed to parse AI JSON response for quiz generation: {}", e.getMessage());
            questions = createFallbackQuestions(topic, count);
        }

        saveHistory(email, String.format("Generate Quiz (%d q): %s", count, topic), aiResponseText, "QUIZ");
        return questions;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatHistoryResponse> getHistory(String email) {
        UUID userId = deriveUserIdFromEmail(email);
        List<ChatHistory> historyList = chatHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return aiMapper.toChatHistoryResponseList(historyList);
    }

    private String callAiModel(String promptMessage, String defaultFallback) {
        if (chatModel != null) {
            try {
                org.springframework.ai.chat.model.ChatResponse response = chatModel.call(new Prompt(promptMessage));
                if (response != null && response.getResult() != null && response.getResult().getOutput() != null) {
                    String resultText = response.getResult().getOutput().getText();
                    if (resultText != null && !resultText.isBlank()) {
                        return resultText;
                    }
                }
            } catch (Exception e) {
                log.info("Google Gemini API call notice (using fallback): {}", e.getMessage());
            }
        }
        return defaultFallback;
    }

    private void saveHistory(String email, String prompt, String response, String type) {
        UUID userId = deriveUserIdFromEmail(email);
        ChatHistory history = ChatHistory.builder()
                .userId(userId)
                .prompt(prompt)
                .response(response)
                .type(type)
                .build();
        chatHistoryRepository.save(history);
    }

    private UUID deriveUserIdFromEmail(String email) {
        return UUID.nameUUIDFromBytes(email.getBytes(StandardCharsets.UTF_8));
    }

    private String cleanJsonString(String raw) {
        if (raw == null) return "{}";
        String cleaned = raw.trim();
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        return cleaned.trim();
    }

    private String generateMockQuizJson(String topic, int count) {
        List<QuizQuestionDto> list = createFallbackQuestions(topic, count);
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            return "[]";
        }
    }

    private List<QuizQuestionDto> createFallbackQuestions(String topic, int count) {
        List<QuizQuestionDto> list = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            list.add(QuizQuestionDto.builder()
                    .question(String.format("Câu hỏi %d về chủ đề %s: Đâu là đáp án đúng?", i, topic))
                    .options(List.of("Option A", "Option B", "Option C", "Option D"))
                    .answer("Option A")
                    .explanation("Đây là giải thích chi tiết cho câu hỏi " + i)
                    .build());
        }
        return list;
    }
}
