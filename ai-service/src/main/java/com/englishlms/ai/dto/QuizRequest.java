package com.englishlms.ai.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class QuizRequest {

    @NotBlank(message = "Chủ đề / Bài học không được để trống")
    private String lesson;

    @Min(value = 1, message = "Số lượng câu hỏi tối thiểu là 1")
    @Max(value = 20, message = "Số lượng câu hỏi tối đa là 20")
    @Builder.Default
    private int numberOfQuestions = 5;
}
