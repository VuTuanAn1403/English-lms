package com.englishlms.ai.repository;

import com.englishlms.ai.entity.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, UUID> {

    List<ChatHistory> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
