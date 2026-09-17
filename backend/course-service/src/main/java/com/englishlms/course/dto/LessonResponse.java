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
@Schema(description = "Thông tin chi tiết bài học")
public class LessonResponse {

    @Schema(description = "Mã UUID bài học", example = "660e8400-e29b-41d4-a716-446655440001")
    private UUID id;

    @Schema(description = "Mã UUID khóa học", example = "550e8400-e29b-41d4-a716-446655440001")
    private UUID courseId;

    @Schema(description = "Tên khóa học", example = "English Communication - Giao Tiếp Căn Bản")
    private String courseTitle;

    @Schema(description = "Tiêu đề bài học", example = "Lesson 1: Greetings & Introductions")
    private String title;

    @Schema(description = "Mô tả bài học", example = "Học cách chào hỏi và giới thiệu bản thân...")
    private String description;

    @Schema(description = "Nội dung bài học", example = "Nội dung bài học chi tiết...")
    private String content;

    @Schema(description = "Đường dẫn Video", example = "https://www.youtube.com/watch?v=...")
    private String videoUrl;

    @Schema(description = "Đường dẫn Tài liệu PDF", example = "https://example.com/document.pdf")
    private String documentUrl;

    @Schema(description = "Alias đường dẫn PDF", example = "https://example.com/document.pdf")
    private String pdfUrl;

    @Schema(description = "Thời lượng (phút)", example = "20")
    private Integer duration;

    @Schema(description = "Thứ tự sắp xếp (Order Index)", example = "1")
    private Integer orderIndex;

    @Schema(description = "Alias thứ tự bài học (Lesson Order)", example = "1")
    private Integer lessonOrder;

    @Schema(description = "Trạng thái xuất bản", example = "true")
    private Boolean isPublished;

    @Schema(description = "Bài học thuộc 5 bài học thử miễn phí hay không", example = "true")
    private Boolean isTrial;

    @Schema(description = "Thời gian tạo", example = "2026-07-25T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Thời gian cập nhật gần nhất", example = "2026-07-25T10:00:00")
    private LocalDateTime updatedAt;
}
