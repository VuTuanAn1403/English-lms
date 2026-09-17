package com.englishlms.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Phản hồi đăng nhập thành công chứa JWT token")
public class LoginResponse {

    @Schema(description = "JWT Access Token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "JWT Refresh Token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String refreshToken;

    @Schema(description = "Loại Token", example = "Bearer")
    private String tokenType;

    @Schema(description = "Thông tin người dùng đăng nhập")
    private UserResponse user;
}

