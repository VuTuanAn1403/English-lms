package com.englishlms.course.dto;

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
public class LessonResponse {

    private UUID id;
    private UUID courseId;
    private String title;
    private String content;
    private String videoUrl;
    private String pdfUrl;
    private Integer lessonOrder;
    private LocalDateTime createdAt;
}
