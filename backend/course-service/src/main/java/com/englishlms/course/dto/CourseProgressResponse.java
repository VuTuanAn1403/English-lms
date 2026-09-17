package com.englishlms.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Báo cáo tiến độ khóa học chi tiết dành cho học viên")
public class CourseProgressResponse {

    @Schema(description = "ID khóa học")
    private UUID courseId;

    @Schema(description = "Tên khóa học")
    private String courseTitle;

    @Schema(description = "Số bài học đã hoàn thành")
    private Integer completedLessons;

    @Schema(description = "Tổng số bài học")
    private Integer totalLessons;

    @Schema(description = "Phần trăm tiến độ hoàn thành (0-100)")
    private Integer progressPercent;

    @Schema(description = "Đã hoàn thành toàn bộ khóa học hay chưa")
    private Boolean completed;

    @Schema(description = "Trạng thái học (NOT_STARTED, IN_PROGRESS, COMPLETED)")
    private String status;

    @Schema(description = "ID bài học kế tiếp cần học")
    private UUID nextLessonId;

    @Schema(description = "Danh sách chi tiết tiến độ các bài học")
    private List<LessonProgressDto> lessons;
}
