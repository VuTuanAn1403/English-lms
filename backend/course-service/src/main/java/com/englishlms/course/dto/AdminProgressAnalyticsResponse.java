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
@Schema(description = "Báo cáo phân tích tiến độ học tập toàn hệ thống dành cho Admin")
public class AdminProgressAnalyticsResponse {

    @Schema(description = "Tổng lượt học/bài học đã xem")
    private Long totalStudySessions;

    @Schema(description = "Tổng số khóa học đã hoàn thành")
    private Long completedCoursesCount;

    @Schema(description = "Tổng số khóa học đang học")
    private Long inProgressCoursesCount;

    @Schema(description = "Tổng số khóa học chưa bắt đầu")
    private Long notStartedCoursesCount;

    @Schema(description = "Tỷ lệ hoàn thành khóa học (%)")
    private Double completionRate;

    @Schema(description = "Phân bổ tiến độ học tập theo dải 0-25%, 25-50%, 50-75%, 75-100%")
    private ProgressDistributionDto progressDistribution;

    @Schema(description = "Top 10 học viên chăm chỉ nhất")
    private List<TopStudentDto> topStudents;

    @Schema(description = "Top 10 khóa học hoàn thành nhiều nhất")
    private List<TopCourseDto> topCourses;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProgressDistributionDto {
        private Long range0to25;
        private Long range25to50;
        private Long range50to75;
        private Long range75to100;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TopStudentDto {
        private UUID userId;
        private String studentName;
        private String studentEmail;
        private Long completedLessonsCount;
        private Long enrolledCoursesCount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TopCourseDto {
        private UUID courseId;
        private String courseTitle;
        private String level;
        private Long completedCount;
        private Long totalEnrollments;
    }
}
