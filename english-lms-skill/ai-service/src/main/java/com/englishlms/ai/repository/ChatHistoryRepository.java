package com.englishlms.ai.repository;

import com.englishlms.ai.entity.ChatHistory;
import com.englishlms.ai.entity.PromptType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, UUID>, JpaSpecificationExecutor<ChatHistory> {

    List<ChatHistory> findByUserEmailOrderByCreatedAtDesc(String userEmail);

    List<ChatHistory> findBySessionIdOrderByCreatedAtAsc(String sessionId);

    Page<ChatHistory> findByUserEmailAndPromptType(String userEmail, PromptType promptType, Pageable pageable);

    Page<ChatHistory> findByUserEmail(String userEmail, Pageable pageable);

    Page<ChatHistory> findByUserEmailAndSessionId(String userEmail, String sessionId, Pageable pageable);

    Page<ChatHistory> findByUserEmailAndPromptTypeAndSessionId(String userEmail, PromptType promptType, String sessionId, Pageable pageable);

    void deleteBySessionId(String sessionId);

    void deleteBySessionIdAndUserEmail(String sessionId, String userEmail);

    long countBySessionIdAndUserEmail(String sessionId, String userEmail);
}
