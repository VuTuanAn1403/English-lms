package com.englishlms.course.dto;

import com.englishlms.course.entity.EnrollmentStatus;
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
@Schema(description = "Thông tin đăng ký và tiến độ khóa học chi tiết")
public class EnrollmentResponse {

    @Schema(description = "ID bản ghi đăng ký", example = "550e8400-e29b-41d4-a716-446655440201")
    private UUID id;

    @Schema(description = "ID người dùng", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID userId;

    @Schema(description = "Tên học viên", example = "Nguyễn Văn A")
    private String studentName;

    @Schema(description = "Email học viên", example = "student@gmail.com")
    private String studentEmail;

    @Schema(description = "ID khóa học", example = "550e8400-e29b-41d4-a716-446655440001")
    private UUID courseId;

    @Schema(description = "Tên khóa học", example = "English Grammar 1")
    private String courseName;

    @Schema(description = "Trình độ khóa học", example = "BEGINNER")
    private String level;

    @Schema(description = "Ảnh thumbnail khóa học", example = "https://images.unsplash.com/...")
    private String thumbnail;

    @Schema(description = "Thông tin chi tiết khóa học")
    private CourseResponse course;

    @Schema(description = "Phần trăm tiến độ học tập (0-100)", example = "45")
    private Integer progress;

    @Schema(description = "Số bài học đã hoàn thành", example = "4")
    private Integer completedLessons;

    @Schema(description = "Tổng số bài học trong khóa", example = "10")
    private Integer totalLessons;

    @Schema(description = "Trạng thái hoàn thành khóa học", example = "false")
    private Boolean completed;

    @Schema(description = "Trạng thái học (NOT_STARTED, IN_PROGRESS, COMPLETED)", example = "IN_PROGRESS")
    private EnrollmentStatus status;

    @Schema(description = "Thời điểm đăng ký", example = "2026-07-24T10:00:00")
    private LocalDateTime enrolledAt;

    @Schema(description = "Thời điểm cập nhật gần nhất", example = "2026-07-25T11:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Thời điểm hoàn thành khóa học", example = "2026-07-25T12:00:00")
    private LocalDateTime completionDate;
}
