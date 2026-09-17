package com.englishlms.ai.config;

import com.englishlms.ai.memory.DbChatMemory;
import com.englishlms.ai.repository.ChatHistoryRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringAiConfig {

    @Bean
    public ChatMemory chatMemory(ChatHistoryRepository chatHistoryRepository) {
        return new DbChatMemory(chatHistoryRepository);
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }
}
