package com.englishlms.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Yêu cầu sinh bài tập trắc nghiệm (Quiz) bằng AI")
public class QuizRequest {

    @NotBlank(message = "Chủ đề bài học không được để trống")
    @Schema(description = "Chủ đề hoặc bài học cần sinh câu hỏi", example = "Present Simple Tense")
    private String lesson;

    @Schema(description = "Trình độ khung CEFR (A1, A2, B1, B2, C1, C2)", example = "B1")
    private String cefrLevel;

    @Schema(description = "Kỹ năng mục tiêu (Grammar, Vocabulary, Reading)", example = "Grammar")
    private String skillTarget;

    @Schema(description = "Độ khó (Beginner, Intermediate, Advanced)", example = "Intermediate")
    private String difficulty;

    @Schema(description = "Số lượng câu hỏi cần sinh", example = "5")
    @Min(value = 5, message = "Số lượng câu hỏi tối thiểu là 5 câu")
    @Max(value = 10, message = "Số lượng câu hỏi tối đa là 10 câu")
    @Builder.Default
    private int numberOfQuestions = 5;
}
