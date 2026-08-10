package com.englishlms.user.dto;

import com.englishlms.user.entity.Role;
import com.englishlms.user.entity.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
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
@Schema(description = "Yêu cầu Admin cập nhật thông tin người dùng")
public class AdminUpdateUserRequest {

    @Schema(description = "Họ và tên", example = "Nguyễn Văn B")
    private String fullName;

    @Email(message = "Email không đúng định dạng")
    @Schema(description = "Email mới", example = "student2_updated@gmail.com")
    private String email;

    @Schema(description = "Vai trò mới (ADMIN, STUDENT)")
    private Role role;

    @Schema(description = "Trạng thái mới (ACTIVE, INACTIVE)")
    private UserStatus status;

    @Schema(description = "Ảnh đại diện mới")
    private String avatar;
}
