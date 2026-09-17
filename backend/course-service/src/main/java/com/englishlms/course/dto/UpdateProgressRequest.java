package com.englishlms.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Yêu cầu cập nhật tiến độ bài học")
public class UpdateProgressRequest {

    @Schema(description = "ID bài học vừa hoàn thành (tùy chọn)", example = "550e8400-e29b-41d4-a716-446655440005")
    private UUID lessonId;

    @Min(0)
    @Schema(description = "Số bài học đã hoàn thành", example = "4")
    private Integer completedLessons;

    @Min(0)
    @Max(100)
    @Schema(description = "Phần trăm tiến độ cụ thể (tùy chọn, 0-100)", example = "40")
    private Integer progress;
}
