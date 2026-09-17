package com.englishlms.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin trạng thái tiến độ của bài học")
public class LessonProgressDto {

    @Schema(description = "ID bài học")
    private UUID lessonId;

    @Schema(description = "Tiêu đề bài học")
    private String lessonTitle;

    @Schema(description = "Thứ tự bài học trong khóa")
    private Integer lessonOrder;

    @Schema(description = "Đã hoàn thành hay chưa")
    private Boolean completed;

    @Schema(description = "Thời điểm hoàn thành bài học")
    private LocalDateTime completedAt;

    @Schema(description = "Thời điểm truy cập gần nhất")
    private LocalDateTime lastAccessedAt;
}
