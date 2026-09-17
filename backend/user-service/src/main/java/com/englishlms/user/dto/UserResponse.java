package com.englishlms.user.dto;

import com.englishlms.user.entity.Role;
import com.englishlms.user.entity.UserStatus;
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
@Schema(description = "Thông tin chi tiết người dùng")
public class UserResponse {

    @Schema(description = "ID người dùng", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Họ và tên", example = "Nguyễn Văn A")
    private String fullName;

    @Schema(description = "Email", example = "student@gmail.com")
    private String email;

    @Schema(description = "Vai trò (STUDENT, ADMIN)", example = "STUDENT")
    private Role role;

    @Schema(description = "Trạng thái (ACTIVE, INACTIVE)", example = "ACTIVE")
    private UserStatus status;

    @Schema(description = "Đường dẫn ảnh đại diện", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "Thời gian tạo tài khoản", example = "2026-07-24T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Thời gian cập nhật gần nhất", example = "2026-07-24T10:00:00")
    private LocalDateTime updatedAt;
}
