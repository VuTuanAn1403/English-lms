package com.englishlms.ai.dto;

import jakarta.validation.constraints.NotBlank;
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
public class GrammarCheckRequest {

    @NotBlank(message = "Văn bản tiếng Anh cần kiểm tra không được để trống")
    private String text;
}
