package com.englishlms.ai.memory;

import com.englishlms.ai.entity.ChatHistory;
import com.englishlms.ai.entity.PromptType;
import com.englishlms.ai.repository.ChatHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatMemoryTest {

    @Mock
    private ChatHistoryRepository chatHistoryRepository;

    private DbChatMemory dbChatMemory;
    private String sessionId;

    @BeforeEach
    void setUp() {
        dbChatMemory = new DbChatMemory(chatHistoryRepository);
        sessionId = "test-session-123";
    }

    @Test
    @DisplayName("DbChatMemory.add() là no-op để tránh ghi trùng bản ghi (AiServiceImpl chịu trách nhiệm lưu)")
    void testAddMessage_IsNoOpToPreventDuplicateWrites() {
        Message userMessage = new UserMessage("Xin chào AI");
        Message assistantMessage = new AssistantMessage("Chào bạn! Tôi có thể giúp gì?");

        dbChatMemory.add(sessionId, List.of(userMessage, assistantMessage));

        // DbChatMemory.add() is intentionally no-op; AiServiceImpl handles persistence
        verify(chatHistoryRepository, never()).save(any(ChatHistory.class));
    }

    @Test
    @DisplayName("Kiểm tra nạp lịch sử tin nhắn hội thoại per session")
    void testGetMessages() {
        ChatHistory h1 = ChatHistory.builder()
                .id(UUID.randomUUID())
                .sessionId(sessionId)
                .promptType(PromptType.CHAT)
                .userPrompt("Hôm nay thời tiết thế nào?")
                .createdAt(LocalDateTime.now())
                .build();

        ChatHistory h2 = ChatHistory.builder()
                .id(UUID.randomUUID())
                .sessionId(sessionId)
                .promptType(PromptType.CHAT)
                .aiResponse("Tôi là trợ lý học tiếng Anh, không cung cấp thời tiết.")
                .createdAt(LocalDateTime.now().plusSeconds(1))
                .build();

        when(chatHistoryRepository.findBySessionIdOrderByCreatedAtAsc(sessionId))
                .thenReturn(List.of(h1, h2));

        List<Message> messages = dbChatMemory.get(sessionId, 10);

        assertNotNull(messages);
        assertEquals(2, messages.size());
        assertEquals("Hôm nay thời tiết thế nào?", messages.get(0).getText());
        assertEquals("Tôi là trợ lý học tiếng Anh, không cung cấp thời tiết.", messages.get(1).getText());
    }

    @Test
    @DisplayName("DbChatMemory.clear() thực hiện xóa bản ghi DB theo sessionId")
    void testClearSession() {
        dbChatMemory.clear(sessionId);
        verify(chatHistoryRepository).deleteBySessionId(sessionId);
    }
}
