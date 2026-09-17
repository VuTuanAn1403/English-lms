package com.englishlms.ai.service.impl;

import com.englishlms.ai.dto.ChatHistoryResponse;
import com.englishlms.ai.dto.ChatRequest;
import com.englishlms.ai.dto.ChatResponse;
import com.englishlms.ai.dto.GrammarCheckRequest;
import com.englishlms.ai.dto.GrammarCheckResponse;
import com.englishlms.ai.dto.PageResponse;
import com.englishlms.ai.dto.QuizQuestionDto;
import com.englishlms.ai.dto.QuizRequest;
import com.englishlms.ai.entity.ChatHistory;
import com.englishlms.ai.entity.PromptType;
import com.englishlms.ai.exception.BadRequestException;
import com.englishlms.ai.exception.EmptyAiResponseException;
import com.englishlms.ai.prompt.ChatPromptTemplate;
import com.englishlms.ai.prompt.GrammarPromptTemplate;
import com.englishlms.ai.prompt.QuizPromptTemplate;
import com.englishlms.ai.repository.ChatHistoryRepository;
import com.englishlms.ai.service.AiService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private static final Logger log = LoggerFactory.getLogger(AiServiceImpl.class);

    private final ChatClient chatClient;
    private final ChatHistoryRepository chatHistoryRepository;
    private final ChatPromptTemplate chatPromptTemplate;
    private final GrammarPromptTemplate grammarPromptTemplate;
    private final QuizPromptTemplate quizPromptTemplate;
    private final ObjectMapper objectMapper;
    private final org.springframework.ai.chat.memory.ChatMemory chatMemory;

    // ======================== CHAT ========================

    @Override
    public ChatResponse chat(ChatRequest request, String userEmail) {
        String userPrompt = request.getMessage();
        if (userPrompt == null || userPrompt.isBlank()) {
            throw new com.englishlms.ai.exception.InvalidPromptException("Nội dung tin nhắn gửi tới AI không được để trống.");
        }

        String sessionId = (request.getSessionId() != null && !request.getSessionId().isBlank())
                ? request.getSessionId()
                : UUID.randomUUID().toString();

        String systemInstruction = chatPromptTemplate.getSystemInstruction();
        String userPromptText = chatPromptTemplate.createChatUserPrompt(userPrompt);

        log.info("Sending Chat AI prompt for user [{}] with sessionId [{}]", userEmail, sessionId);

        // Load conversation context from DB via ChatMemory (read-only)
        List<org.springframework.ai.chat.messages.Message> historyMessages = chatMemory.get(sessionId, 10);

        String aiResponseContent = chatClient.prompt()
                .system(systemInstruction)
                .messages(historyMessages)
                .user(userPromptText)
                .call()
                .content();

        if (aiResponseContent == null || aiResponseContent.isBlank()) {
            aiResponseContent = "Rất tiếc, AI chưa thể phản hồi lúc này. Vui lòng thử lại sau!";
        }

        // Single canonical save — DbChatMemory.add() is no-op so no duplicate writes
        saveChatHistory(userEmail, sessionId, PromptType.CHAT, userPrompt, aiResponseContent);

        return ChatResponse.builder()
                .response(aiResponseContent)
                .sessionId(sessionId)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // ======================== GRAMMAR CHECK ========================

    @Override
    public GrammarCheckResponse grammarCheck(GrammarCheckRequest request, String userEmail) {
        String originalText = request.getText();
        String systemInstruction = grammarPromptTemplate.getSystemInstruction();
        String userPromptText = grammarPromptTemplate.createGrammarCheckPrompt(originalText);

        log.info("Sending Grammar Check prompt for user [{}]", userEmail);

        String rawContent = chatClient.prompt()
                .system(systemInstruction)
                .user(userPromptText)
                .call()
                .content();

        if (rawContent == null || rawContent.isBlank()) {
            throw new EmptyAiResponseException("AI không trả về kết quả kiểm tra ngữ pháp. Vui lòng thử lại.");
        }

        String cleanedJson = cleanJsonOutput(rawContent);
        GrammarCheckResponse response;

        try {
            response = objectMapper.readValue(cleanedJson, GrammarCheckResponse.class);
            if (response.getOriginal() == null || response.getOriginal().isBlank()) {
                response.setOriginal(originalText);
            }
        } catch (Exception e) {
            log.warn("Failed to parse Grammar AI response to JSON: {}", e.getMessage());
            throw new EmptyAiResponseException("AI trả về dữ liệu không đúng định dạng. Vui lòng thử lại.");
        }

        saveChatHistory(userEmail, null, PromptType.GRAMMAR, originalText, rawContent);

        return response;
    }

    // ======================== QUIZ ========================

    @Override
    public List<QuizQuestionDto> generateQuiz(QuizRequest request, String userEmail) {
        String systemInstruction = quizPromptTemplate.getSystemInstruction();
        String userPromptText = quizPromptTemplate.buildUserPrompt(
                request.getLesson(),
                request.getCefrLevel(),
                request.getSkillTarget(),
                request.getDifficulty(),
                request.getNumberOfQuestions()
        );

        log.info("Sending Quiz Generation prompt for user [{}] - Topic: [{}]", userEmail, request.getLesson());

        String rawContent = chatClient.prompt()
                .system(systemInstruction)
                .user(userPromptText)
                .call()
                .content();

        if (rawContent == null || rawContent.isBlank()) {
            throw new EmptyAiResponseException("AI không trả về câu hỏi trắc nghiệm. Vui lòng thử lại.");
        }

        String cleanedJson = cleanJsonOutput(rawContent);
        List<QuizQuestionDto> quizList;

        try {
            quizList = objectMapper.readValue(cleanedJson, new TypeReference<List<QuizQuestionDto>>() {});
        } catch (Exception e) {
            log.warn("Failed to parse Quiz AI response to JSON: {}", e.getMessage());
            throw new EmptyAiResponseException("AI trả về dữ liệu Quiz không đúng định dạng JSON. Vui lòng thử lại.");
        }

        if (quizList.isEmpty()) {
            throw new EmptyAiResponseException("AI trả về danh sách câu hỏi rỗng. Vui lòng thử lại.");
        }

        // Validate each quiz question
        for (int i = 0; i < quizList.size(); i++) {
            QuizQuestionDto q = quizList.get(i);
            if (q.getOptions() == null || q.getOptions().size() != 4) {
                throw new EmptyAiResponseException(
                        String.format("Câu hỏi %d không có đủ 4 lựa chọn. AI trả về dữ liệu không hợp lệ.", i + 1));
            }
            if (q.getCorrectAnswerIndex() < 0 || q.getCorrectAnswerIndex() > 3) {
                throw new EmptyAiResponseException(
                        String.format("Câu hỏi %d có chỉ số đáp án đúng không hợp lệ (%d). Phải từ 0 đến 3.", i + 1, q.getCorrectAnswerIndex()));
            }
        }

        saveChatHistory(userEmail, null, PromptType.QUIZ, userPromptText, rawContent);

        return quizList;
    }

    // ======================== HISTORY ========================

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public PageResponse<ChatHistoryResponse> getHistory(String userEmail, String promptTypeStr, String sessionId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        PromptType promptType = null;
        if (promptTypeStr != null && !promptTypeStr.isBlank()) {
            try {
                promptType = PromptType.valueOf(promptTypeStr.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Loại prompt không hợp lệ: " + promptTypeStr + ". Chấp nhận: CHAT, GRAMMAR, QUIZ.");
            }
        }

        Page<ChatHistory> historyPage;

        if (promptType != null && sessionId != null && !sessionId.isBlank()) {
            historyPage = chatHistoryRepository.findByUserEmailAndPromptTypeAndSessionId(userEmail, promptType, sessionId, pageable);
        } else if (promptType != null) {
            historyPage = chatHistoryRepository.findByUserEmailAndPromptType(userEmail, promptType, pageable);
        } else if (sessionId != null && !sessionId.isBlank()) {
            historyPage = chatHistoryRepository.findByUserEmailAndSessionId(userEmail, sessionId, pageable);
        } else {
            historyPage = chatHistoryRepository.findByUserEmail(userEmail, pageable);
        }

        List<ChatHistoryResponse> items = historyPage.getContent().stream()
                .map(this::mapToHistoryResponse)
                .collect(Collectors.toList());

        return PageResponse.<ChatHistoryResponse>builder()
                .items(items)
                .pageNumber(historyPage.getNumber())
                .pageSize(historyPage.getSize())
                .totalElements(historyPage.getTotalElements())
                .totalPages(historyPage.getTotalPages())
                .isLast(historyPage.isLast())
                .build();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public PageResponse<ChatHistoryResponse> getAllHistoryForAdmin(String search, String promptTypeStr, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        org.springframework.data.jpa.domain.Specification<ChatHistory> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("userEmail")), pattern),
                        cb.like(cb.lower(root.get("userPrompt")), pattern)
                ));
            }
            if (promptTypeStr != null && !promptTypeStr.isBlank()) {
                try {
                    PromptType pt = PromptType.valueOf(promptTypeStr.trim().toUpperCase());
                    predicates.add(cb.equal(root.get("promptType"), pt));
                } catch (IllegalArgumentException ignored) {}
            }
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        Page<ChatHistory> historyPage = chatHistoryRepository.findAll(spec, pageable);

        List<ChatHistoryResponse> items = historyPage.getContent().stream()
                .map(this::mapToHistoryResponse)
                .collect(Collectors.toList());

        return PageResponse.<ChatHistoryResponse>builder()
                .items(items)
                .pageNumber(historyPage.getNumber())
                .pageSize(historyPage.getSize())
                .totalElements(historyPage.getTotalElements())
                .totalPages(historyPage.getTotalPages())
                .isLast(historyPage.isLast())
                .build();
    }

    // ======================== SESSION MANAGEMENT ========================

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void deleteSession(String sessionId, String userEmail) {
        long count = chatHistoryRepository.countBySessionIdAndUserEmail(sessionId, userEmail);
        if (count == 0) {
            throw new com.englishlms.ai.exception.ResourceNotFoundException(
                    "Không tìm thấy session [" + sessionId + "] thuộc về bạn.");
        }
        chatHistoryRepository.deleteBySessionIdAndUserEmail(sessionId, userEmail);
        chatMemory.clear(sessionId);
        log.info("Deleted session [{}] for user [{}] ({} records)", sessionId, userEmail, count);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void clearSession(String sessionId) {
        chatMemory.clear(sessionId);
        log.info("Cleared memory for session [{}]", sessionId);
    }

    // ======================== PRIVATE HELPERS ========================

    private void saveChatHistory(String userEmail, String sessionId, PromptType promptType, String userPrompt, String aiResponse) {
        try {
            ChatHistory history = ChatHistory.builder()
                    .userEmail(userEmail)
                    .sessionId(sessionId)
                    .promptType(promptType)
                    .userPrompt(userPrompt)
                    .aiResponse(aiResponse)
                    .build();
            chatHistoryRepository.save(history);
        } catch (Exception e) {
            log.error("Failed to save ChatHistory for user [{}]: {}", userEmail, e.getMessage());
        }
    }

    private String cleanJsonOutput(String rawContent) {
        if (rawContent == null) return "";
        String content = rawContent.trim();
        if (content.startsWith("```json")) {
            content = content.substring(7);
        } else if (content.startsWith("```")) {
            content = content.substring(3);
        }
        if (content.endsWith("```")) {
            content = content.substring(0, content.length() - 3);
        }
        return content.trim();
    }

    private ChatHistoryResponse mapToHistoryResponse(ChatHistory entity) {
        return ChatHistoryResponse.builder()
                .id(entity.getId())
                .userEmail(entity.getUserEmail())
                .sessionId(entity.getSessionId())
                .promptType(entity.getPromptType())
                .userPrompt(entity.getUserPrompt())
                .aiResponse(entity.getAiResponse())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
