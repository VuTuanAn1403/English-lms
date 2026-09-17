package com.englishlms.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
@Schema(description = "Yêu cầu cập nhật thông tin cá nhân")
public class UpdateProfileRequest {

    @Schema(description = "Họ và tên mới", example = "Nguyễn Văn A")
    @NotBlank(message = "Họ và tên không được để trống")
    @Size(max = 100, message = "Họ và tên tối đa 100 ký tự")
    private String fullName;

    @Schema(description = "URL ảnh đại diện mới", example = "https://example.com/avatar.jpg")
    @Size(max = 255, message = "Đường dẫn ảnh đại diện tối đa 255 ký tự")
    private String avatar;
}

