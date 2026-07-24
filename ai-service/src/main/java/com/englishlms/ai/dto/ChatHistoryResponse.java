package com.englishlms.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatHistoryResponse {

    private UUID id;
    private UUID userId;
    private String prompt;
    private String response;
    private String type;
    private LocalDateTime createdAt;
}
