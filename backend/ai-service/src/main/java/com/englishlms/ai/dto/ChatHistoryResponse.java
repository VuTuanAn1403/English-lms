package com.englishlms.ai.dto;

import com.englishlms.ai.entity.PromptType;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Thông tin phản hồi lịch sử tương tác AI")
public class ChatHistoryResponse {

    @Schema(description = "ID bản ghi lịch sử")
    private UUID id;

    @Schema(description = "Email người dùng", example = "student@gmail.com")
    private String userEmail;

    @Schema(description = "Mã phiên làm việc (Session ID)", example = "session-123456")
    private String sessionId;

    @Schema(description = "Loại yêu cầu tương tác AI", example = "CHAT")
    private PromptType promptType;

    @Schema(description = "Câu hỏi / Prompt của người dùng")
    private String userPrompt;

    @Schema(description = "Câu trả lời của AI")
    private String aiResponse;

    @Schema(description = "Thời gian tạo bản ghi")
    private LocalDateTime createdAt;
}
