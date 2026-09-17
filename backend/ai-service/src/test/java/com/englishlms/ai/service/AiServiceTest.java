package com.englishlms.ai.service;

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
import com.englishlms.ai.exception.EmptyAiResponseException;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiServiceTest {

    @Mock private ChatClient chatClient;
    @Mock private ChatClient.ChatClientRequestSpec chatClientRequest;
    @Mock private ChatClient.CallResponseSpec callResponse;
    @Mock private ChatHistoryRepository chatHistoryRepository;
    @Mock private ChatMemory chatMemory;

    private AiServiceImpl aiService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        ChatPromptTemplate chatPromptTemplate = new ChatPromptTemplate();
        GrammarPromptTemplate grammarPromptTemplate = new GrammarPromptTemplate();
        QuizPromptTemplate quizPromptTemplate = new QuizPromptTemplate();

        aiService = new AiServiceImpl(
                chatClient, chatHistoryRepository,
                chatPromptTemplate, grammarPromptTemplate, quizPromptTemplate,
                objectMapper, chatMemory
        );

        // Common ChatClient mock chain
        lenient().when(chatClient.prompt()).thenReturn(chatClientRequest);
        lenient().when(chatClientRequest.system(anyString())).thenReturn(chatClientRequest);
        lenient().when(chatClientRequest.user(anyString())).thenReturn(chatClientRequest);
        lenient().when(chatClientRequest.messages(any(List.class))).thenReturn(chatClientRequest);
        lenient().when(chatClientRequest.call()).thenReturn(callResponse);
    }

    // ==================== CHAT TESTS ====================

    @Test
    @DisplayName("Chat: 2 tin nhắn cùng sessionId sử dụng được context trước đó")
    void chat_SameSession_UsesContextFromPreviousMessages() {
        String sessionId = "session-abc";
        List<Message> previousContext = List.of(
                new org.springframework.ai.chat.messages.UserMessage("Hello"),
                new org.springframework.ai.chat.messages.AssistantMessage("Hi there!")
        );
        when(chatMemory.get(sessionId, 10)).thenReturn(previousContext);
        when(callResponse.content()).thenReturn("Based on our conversation, here is more info.");

        ChatRequest request = ChatRequest.builder()
                .message("Tell me more")
                .sessionId(sessionId)
                .build();

        ChatResponse response = aiService.chat(request, "student@gmail.com");

        assertNotNull(response);
        assertEquals(sessionId, response.getSessionId());
        // Verify ChatClient received the previous context messages
        verify(chatClientRequest).messages(previousContext);
    }

    @Test
    @DisplayName("Chat: 2 user khác nhau không chia sẻ memory")
    void chat_DifferentUsers_DoNotShareMemory() {
        when(chatMemory.get("session-user1", 10)).thenReturn(List.of());
        when(chatMemory.get("session-user2", 10)).thenReturn(List.of());
        when(callResponse.content()).thenReturn("Response");

        ChatRequest req1 = ChatRequest.builder().message("User 1 question").sessionId("session-user1").build();
        ChatRequest req2 = ChatRequest.builder().message("User 2 question").sessionId("session-user2").build();

        aiService.chat(req1, "user1@gmail.com");
        aiService.chat(req2, "user2@gmail.com");

        // Verify each chat uses its own sessionId for memory
        verify(chatMemory).get("session-user1", 10);
        verify(chatMemory).get("session-user2", 10);

        // Verify separate saves with different emails
        ArgumentCaptor<ChatHistory> captor = ArgumentCaptor.forClass(ChatHistory.class);
        verify(chatHistoryRepository, times(2)).save(captor.capture());
        List<ChatHistory> saved = captor.getAllValues();
        assertEquals("user1@gmail.com", saved.get(0).getUserEmail());
        assertEquals("user2@gmail.com", saved.get(1).getUserEmail());
    }

    @Test
    @DisplayName("Chat: Mỗi lượt chat chỉ lưu đúng 1 bản ghi vào DB")
    void chat_SingleTurn_SavesExactlyOneRecord() {
        when(chatMemory.get(anyString(), anyInt())).thenReturn(List.of());
        when(callResponse.content()).thenReturn("AI response");

        ChatRequest request = ChatRequest.builder()
                .message("Hello AI")
                .sessionId("session-001")
                .build();

        aiService.chat(request, "student@gmail.com");

        // Verify exactly 1 save call (not 2 or 3)
        verify(chatHistoryRepository, times(1)).save(any(ChatHistory.class));

        // Verify the saved record has both user prompt and AI response
        ArgumentCaptor<ChatHistory> captor = ArgumentCaptor.forClass(ChatHistory.class);
        verify(chatHistoryRepository).save(captor.capture());
        ChatHistory saved = captor.getValue();
        assertEquals("student@gmail.com", saved.getUserEmail());
        assertEquals("session-001", saved.getSessionId());
        assertEquals(PromptType.CHAT, saved.getPromptType());
        assertNotNull(saved.getUserPrompt());
        assertNotNull(saved.getAiResponse());
    }

    // ==================== GRAMMAR TESTS ====================

    @Test
    @DisplayName("Grammar: Parse thành công JSON đúng schema mới")
    void grammarCheck_ValidJsonResponse_ParsesCorrectly() {
        String validJson = """
            {
              "original": "He go to school",
              "corrected": "He goes to school",
              "explanation": "Sử dụng goes cho chủ ngữ He",
              "errors": ["Thiếu es ở động từ go"],
              "score": 6,
              "grammarRules": ["Subject-verb agreement"],
              "examples": ["She goes to work every day"],
              "tips": "He/She/It + Vs/es"
            }
            """;
        when(callResponse.content()).thenReturn(validJson);

        GrammarCheckRequest request = GrammarCheckRequest.builder().text("He go to school").build();
        GrammarCheckResponse response = aiService.grammarCheck(request, "student@gmail.com");

        assertEquals("He go to school", response.getOriginal());
        assertEquals("He goes to school", response.getCorrected());
        assertEquals(6, response.getScore());
        assertNotNull(response.getGrammarRules());
        assertFalse(response.getGrammarRules().isEmpty());
        assertNotNull(response.getExamples());
        assertNotNull(response.getTips());
    }

    @Test
    @DisplayName("Grammar: JSON sai từ model → ném EmptyAiResponseException (HTTP 502)")
    void grammarCheck_InvalidJson_ThrowsException() {
        when(callResponse.content()).thenReturn("This is not valid JSON at all.");

        GrammarCheckRequest request = GrammarCheckRequest.builder().text("He go to school").build();

        assertThrows(EmptyAiResponseException.class, () -> aiService.grammarCheck(request, "student@gmail.com"));
    }

    @Test
    @DisplayName("Grammar: AI trả về null → ném EmptyAiResponseException")
    void grammarCheck_NullResponse_ThrowsException() {
        when(callResponse.content()).thenReturn(null);

        GrammarCheckRequest request = GrammarCheckRequest.builder().text("He go to school").build();

        assertThrows(EmptyAiResponseException.class, () -> aiService.grammarCheck(request, "student@gmail.com"));
    }

    // ==================== QUIZ TESTS ====================

    @Test
    @DisplayName("Quiz: Parse thành công JSON với correctAnswerIndex số nguyên")
    void generateQuiz_ValidJson_ParsesWithCorrectAnswerIndex() {
        String validJson = """
            [
              {
                "id": "1",
                "question": "She ___ to school every day.",
                "options": ["go", "goes", "went", "going"],
                "correctAnswerIndex": 1,
                "explanation": "Chủ ngữ She + goes"
              }
            ]
            """;
        when(callResponse.content()).thenReturn(validJson);

        QuizRequest request = QuizRequest.builder().lesson("Present Simple").numberOfQuestions(5).build();
        List<QuizQuestionDto> result = aiService.generateQuiz(request, "student@gmail.com");

        assertFalse(result.isEmpty());
        assertEquals(1, result.get(0).getCorrectAnswerIndex());
        assertEquals(4, result.get(0).getOptions().size());
    }

    @Test
    @DisplayName("Quiz: JSON sai từ model → ném EmptyAiResponseException (HTTP 502)")
    void generateQuiz_InvalidJson_ThrowsException() {
        when(callResponse.content()).thenReturn("Some random non-JSON text");

        QuizRequest request = QuizRequest.builder().lesson("Present Simple").numberOfQuestions(5).build();

        assertThrows(EmptyAiResponseException.class, () -> aiService.generateQuiz(request, "student@gmail.com"));
    }

    @Test
    @DisplayName("Quiz: Câu hỏi thiếu options → ném EmptyAiResponseException")
    void generateQuiz_MissingOptions_ThrowsException() {
        String invalidOptionsJson = """
            [
              {
                "id": "1",
                "question": "Test?",
                "options": ["A", "B"],
                "correctAnswerIndex": 0,
                "explanation": "Test"
              }
            ]
            """;
        when(callResponse.content()).thenReturn(invalidOptionsJson);

        QuizRequest request = QuizRequest.builder().lesson("Test").numberOfQuestions(5).build();

        assertThrows(EmptyAiResponseException.class, () -> aiService.generateQuiz(request, "student@gmail.com"));
    }

    @Test
    @DisplayName("Quiz: correctAnswerIndex ngoài phạm vi 0-3 → ném EmptyAiResponseException")
    void generateQuiz_InvalidAnswerIndex_ThrowsException() {
        String invalidIndexJson = """
            [
              {
                "id": "1",
                "question": "Test?",
                "options": ["A", "B", "C", "D"],
                "correctAnswerIndex": 5,
                "explanation": "Test"
              }
            ]
            """;
        when(callResponse.content()).thenReturn(invalidIndexJson);

        QuizRequest request = QuizRequest.builder().lesson("Test").numberOfQuestions(5).build();

        assertThrows(EmptyAiResponseException.class, () -> aiService.generateQuiz(request, "student@gmail.com"));
    }

    // ==================== HISTORY TESTS ====================

    @Test
    @DisplayName("History: Student chỉ xem được lịch sử của chính mình (không rò dữ liệu)")
    void getHistory_ReturnsOnlyUserOwnData() {
        ChatHistory ownHistory = ChatHistory.builder()
                .id(UUID.randomUUID())
                .userEmail("student@gmail.com")
                .sessionId("s1")
                .promptType(PromptType.CHAT)
                .userPrompt("Hello")
                .aiResponse("Hi")
                .createdAt(LocalDateTime.now())
                .build();

        Page<ChatHistory> page = new PageImpl<>(List.of(ownHistory));
        when(chatHistoryRepository.findByUserEmail(eq("student@gmail.com"), any(Pageable.class))).thenReturn(page);

        PageResponse<ChatHistoryResponse> result = aiService.getHistory("student@gmail.com", null, null, 0, 10);

        assertEquals(1, result.getItems().size());
        assertEquals("student@gmail.com", result.getItems().get(0).getUserEmail());
        // Verify it queried only for this specific user
        verify(chatHistoryRepository).findByUserEmail(eq("student@gmail.com"), any(Pageable.class));
    }

    @Test
    @DisplayName("History: Lọc theo promptType = GRAMMAR")
    void getHistory_FilterByType_ReturnsFiltered() {
        Page<ChatHistory> page = new PageImpl<>(Collections.emptyList());
        when(chatHistoryRepository.findByUserEmailAndPromptType(eq("s@g.com"), eq(PromptType.GRAMMAR), any(Pageable.class))).thenReturn(page);

        aiService.getHistory("s@g.com", "GRAMMAR", null, 0, 10);

        verify(chatHistoryRepository).findByUserEmailAndPromptType(eq("s@g.com"), eq(PromptType.GRAMMAR), any(Pageable.class));
    }

    @Test
    @DisplayName("History: Lọc theo sessionId")
    void getHistory_FilterBySessionId_ReturnsFiltered() {
        Page<ChatHistory> page = new PageImpl<>(Collections.emptyList());
        when(chatHistoryRepository.findByUserEmailAndSessionId(eq("s@g.com"), eq("sess-1"), any(Pageable.class))).thenReturn(page);

        aiService.getHistory("s@g.com", null, "sess-1", 0, 10);

        verify(chatHistoryRepository).findByUserEmailAndSessionId(eq("s@g.com"), eq("sess-1"), any(Pageable.class));
    }
}
