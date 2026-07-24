package com.englishlms.course.dto;

import jakarta.validation.constraints.NotBlank;
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
public class CourseRequest {

    @NotBlank(message = "Tiêu đề khóa học không được để trống")
    private String title;

    @NotBlank(message = "Mô tả khóa học không được để trống")
    private String description;

    private String level;
    private String imageUrl;
}
