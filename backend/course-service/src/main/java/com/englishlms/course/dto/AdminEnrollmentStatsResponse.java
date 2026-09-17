package com.englishlms.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Báo cáo thống kê lượt đăng ký khóa học cho Admin")
public class AdminEnrollmentStatsResponse {

    @Schema(description = "Tổng số lượt đăng ký", example = "58")
    private Long totalEnrollments;

    @Schema(description = "Số lượng đang học", example = "35")
    private Long inProgressCount;

    @Schema(description = "Số lượng đã hoàn thành", example = "15")
    private Long completedCount;

    @Schema(description = "Số lượng chưa bắt đầu", example = "8")
    private Long notStartedCount;

    @Schema(description = "Tỷ lệ hoàn thành (%)", example = "25.86")
    private Double completionRate;

    @Schema(description = "Tổng số khóa học trong hệ thống", example = "6")
    private Long totalCourses;

    @Schema(description = "Tổng số bài học trong hệ thống", example = "50")
    private Long totalLessons;
}
