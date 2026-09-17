package com.englishlms.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Yêu cầu tạo hoặc cập nhật khóa học")
public class CourseRequest {

    @Schema(description = "Tiêu đề khóa học", example = "English Communication - Giao Tiếp Căn Bản")
    @NotBlank(message = "Tiêu đề khóa học không được để trống")
    private String title;

    @Schema(description = "Mô tả chi tiết khóa học", example = "Khóa học giúp học viên tự tin giao tiếp tiếng Anh.")
    @NotBlank(message = "Mô tả khóa học không được để trống")
    private String description;

    @Schema(description = "Trình độ khóa học (Cơ bản, Trung cấp, Nâng cao)", example = "Cơ bản")
    private String level;

    @Schema(description = "Đường dẫn ảnh minh họa khóa học", example = "https://images.unsplash.com/photo-1546410531-bb4caa6b424d")
    private String imageUrl;
}

