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
@Schema(description = "Yêu cầu kiểm tra ngữ pháp tiếng Anh")
public class GrammarCheckRequest {

    @NotBlank(message = "Đoạn văn bản cần kiểm tra không được để trống")
    @Size(max = 2000, message = "Đoạn văn bản cần kiểm tra tối đa 2000 ký tự")
    @Schema(description = "Đoạn văn bản tiếng Anh cần kiểm tra ngữ pháp", example = "He go to school yesterday.")
    private String text;
}
