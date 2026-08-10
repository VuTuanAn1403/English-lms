package com.englishlms.user.dto;

import com.englishlms.user.entity.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Yêu cầu khóa hoặc mở khóa trạng thái tài khoản")
public class UserStatusRequest {

    @NotNull(message = "Trạng thái không được để trống")
    @Schema(description = "Trạng thái tài khoản (ACTIVE, INACTIVE)", example = "INACTIVE")
    private UserStatus status;
}
