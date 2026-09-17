package com.englishlms.ai.memory;

import com.englishlms.ai.entity.ChatHistory;
import com.englishlms.ai.entity.PromptType;
import com.englishlms.ai.repository.ChatHistoryRepository;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * ChatMemory backed by DB (chat_histories table).
 * READ-ONLY memory: context is reconstructed from saveChatHistory() records.
 * add() is a no-op to prevent duplicate writes (AiServiceImpl handles persistence).
 */
@Component
public class DbChatMemory implements ChatMemory {

    private final ChatHistoryRepository chatHistoryRepository;

    public DbChatMemory(ChatHistoryRepository chatHistoryRepository) {
        this.chatHistoryRepository = chatHistoryRepository;
    }

    /**
     * No-op: persistence is handled by AiServiceImpl.saveChatHistory().
     * This prevents the duplicate-write problem where both ChatMemory.add()
     * and saveChatHistory() would each create separate DB records for the same turn.
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
        // Intentionally empty - AiServiceImpl.saveChatHistory() is the single source of persistence
    }

    /**
     * Reconstruct conversation context from the canonical chat_histories records.
     * Each record contains both userPrompt and aiResponse for one conversation turn.
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<Message> get(String conversationId, int lastN) {
        if (conversationId == null || conversationId.isBlank()) {
            return List.of();
        }
        List<ChatHistory> histories = chatHistoryRepository.findBySessionIdOrderByCreatedAtAsc(conversationId);
        List<Message> messages = new ArrayList<>();
        for (ChatHistory history : histories) {
            if (history.getPromptType() == PromptType.CHAT) {
                if (history.getUserPrompt() != null && !history.getUserPrompt().isBlank()) {
                    messages.add(new UserMessage(history.getUserPrompt()));
                }
                if (history.getAiResponse() != null && !history.getAiResponse().isBlank()) {
                    messages.add(new AssistantMessage(history.getAiResponse()));
                }
            }
        }

        if (lastN > 0 && messages.size() > lastN) {
            return messages.subList(messages.size() - lastN, messages.size());
        }
        return messages;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void clear(String conversationId) {
        if (conversationId != null && !conversationId.isBlank()) {
            chatHistoryRepository.deleteBySessionId(conversationId);
        }
    }
}
