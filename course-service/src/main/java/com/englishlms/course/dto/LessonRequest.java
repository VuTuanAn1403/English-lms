package com.englishlms.course.dto;

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
public class LessonRequest {

    @NotNull(message = "Mã khóa học không được để trống")
    private UUID courseId;

    @NotBlank(message = "Tiêu đề bài học không được để trống")
    private String title;

    private String content;
    private String videoUrl;
    private String pdfUrl;
    private Integer lessonOrder;
}
