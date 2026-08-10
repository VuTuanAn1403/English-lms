package com.englishlms.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Yêu cầu gửi tin nhắn Chat AI")
public class ChatRequest {

    @NotBlank(message = "Nội dung tin nhắn không được để trống")
    @Size(max = 2000, message = "Nội dung tin nhắn tối đa 2000 ký tự")
    @Schema(description = "Câu hỏi hoặc tin nhắn của người dùng gửi cho AI", example = "Hãy giải thích thì Hiện tại hoàn thành")
    private String message;

    @Schema(description = "ID của phiên trò chuyện (nếu có)", example = "session-123456")
    private String sessionId;
}
