package com.englishlms.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Phản hồi kết quả Chat AI")
public class ChatResponse {

    @Schema(description = "Nội dung phản hồi từ AI", example = "Thì Hiện tại hoàn thành (Present Perfect) dùng để mô tả...")
    private String response;

    @Schema(description = "ID của phiên trò chuyện", example = "session-123456")
    private String sessionId;

    @Schema(description = "Thời gian phản hồi")
    private LocalDateTime timestamp;
}
