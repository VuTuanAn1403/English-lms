package com.englishlms.ai.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Kết quả sửa lỗi và giải thích ngữ pháp từ AI")
public class GrammarCheckResponse {

    @Schema(description = "Văn bản gốc của người dùng", example = "He go to school yesterday.")
    @JsonAlias({"originalText", "original_text"})
    private String original;

    @Schema(description = "Văn bản đã được sửa đúng ngữ pháp", example = "He went to school yesterday.")
    @JsonAlias({"correctedText", "corrected_text"})
    private String corrected;

    @Schema(description = "Giải thích chi tiết bằng tiếng Việt")
    private String explanation;

    @Schema(description = "Danh sách các lỗi cụ thể phát hiện được")
    private List<String> errors;

    @Schema(description = "Điểm đánh giá ngữ pháp (0-10)", example = "6")
    private Integer score;

    @Schema(description = "Các quy tắc ngữ pháp áp dụng")
    @JsonAlias({"grammar_rules"})
    private List<String> grammarRules;

    @Schema(description = "Các ví dụ minh họa tương tự")
    private List<String> examples;

    @Schema(description = "Mẹo ghi nhớ ngữ pháp")
    private String tips;
}
