package com.englishlms.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
@Schema(description = "Yêu cầu tạo hoặc cập nhật bài học")
public class LessonRequest {

    @NotNull(message = "Mã khóa học không được để trống")
    @Schema(description = "Mã UUID khóa học", example = "550e8400-e29b-41d4-a716-446655440001")
    private UUID courseId;

    @NotBlank(message = "Tiêu đề bài học không được để trống")
    @Schema(description = "Tiêu đề bài học", example = "Lesson 1: Greetings & Introductions")
    private String title;

    @Schema(description = "Mô tả ngắn gọn bài học", example = "Học cách chào hỏi và giới thiệu bản thân bằng tiếng Anh.")
    private String description;

    @Schema(description = "Nội dung chi tiết bài học", example = "Nội dung bài học chi tiết dạng văn bản...")
    private String content;

    @Schema(description = "Đường dẫn video bài giảng (URL)", example = "https://www.youtube.com/watch?v=dQw4w9WgXcQ")
    private String videoUrl;

    @Schema(description = "Đường dẫn tài liệu học tập (URL)", example = "https://example.com/document.pdf")
    private String documentUrl;

    @Schema(description = "Đường dẫn tài liệu PDF (alias)", example = "https://example.com/document.pdf")
    private String pdfUrl;

    @Min(value = 0, message = "Thời lượng không được nhỏ hơn 0")
    @Schema(description = "Thời lượng bài học (phút)", example = "20")
    private Integer duration;

    @Min(value = 1, message = "Thứ tự bài học phải từ 1 trở lên")
    @Schema(description = "Thứ tự sắp xếp bài học (Order Index)", example = "1")
    private Integer orderIndex;

    @Schema(description = "Alias cho thứ tự bài học (Lesson Order)", example = "1")
    private Integer lessonOrder;

    @Schema(description = "Trạng thái xuất bản (true: Xuất bản, false: Nháp)", example = "true")
    private Boolean isPublished;
}
