package com.englishlms.ai.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Chi tiết một câu hỏi trắc nghiệm do AI tạo")
public class QuizQuestionDto {

    @Schema(description = "ID hoặc số thứ tự câu hỏi", example = "1")
    private String id;

    @Schema(description = "Nội dung câu hỏi", example = "She ___ to school every day.")
    private String question;

    @Schema(description = "Danh sách 4 lựa chọn đáp án", example = "[\"go\", \"goes\", \"went\", \"going\"]")
    private List<String> options;

    @Schema(description = "Chỉ số đáp án đúng (0-indexed, 0-3)", example = "1")
    @JsonAlias({"correctAnswer", "correct_answer", "correct_answer_index"})
    private int correctAnswerIndex;

    @Schema(description = "Giải thích đáp án đúng", example = "Chủ ngữ 'She' đi với động từ thêm 'es' ở thì Hiện tại đơn.")
    private String explanation;
}
