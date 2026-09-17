package com.englishlms.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "Yêu cầu đánh dấu hoặc bỏ đánh dấu hoàn thành bài học")
public class ProgressRequest {

    @NotNull
    @Schema(description = "ID khóa học", example = "550e8400-e29b-41d4-a716-446655440001")
    private UUID courseId;

    @NotNull
    @Schema(description = "ID bài học", example = "550e8400-e29b-41d4-a716-446655440005")
    private UUID lessonId;
}
