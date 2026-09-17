package com.englishlms.user.dto;

import com.englishlms.user.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Schema(description = "Yêu cầu Admin tạo người dùng mới")
public class AdminCreateUserRequest {

    @NotBlank(message = "Họ và tên không được để trống")
    @Schema(description = "Họ và tên người dùng", example = "Nguyễn Văn B")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Schema(description = "Địa chỉ Email", example = "student2@gmail.com")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải chứa tối thiểu 6 ký tự")
    @Schema(description = "Mật khẩu khởi tạo", example = "123456")
    private String password;

    @NotNull(message = "Vai trò không được để trống")
    @Schema(description = "Vai trò (ADMIN, STUDENT)", example = "STUDENT")
    private Role role;

    @Schema(description = "Ảnh đại diện (tùy chọn)", example = "https://ui-avatars.com/api/?name=User")
    private String avatar;
}
