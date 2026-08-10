package com.englishlms.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin chi tiết khóa học")
public class CourseResponse {

    @Schema(description = "ID khóa học", example = "550e8400-e29b-41d4-a716-446655440001")
    private UUID id;

    @Schema(description = "Tiêu đề khóa học", example = "English Communication - Giao Tiếp Căn Bản")
    private String title;

    @Schema(description = "Mô tả khóa học", example = "Khóa học giúp học viên tự tin giao tiếp tiếng Anh.")
    private String description;

    @Schema(description = "Trình độ", example = "Cơ bản")
    private String level;

    @Schema(description = "Đường dẫn ảnh minh họa", example = "https://images.unsplash.com/photo-1546410531-bb4caa6b424d")
    private String imageUrl;

    @Schema(description = "Giá niêm yết (VND)", example = "499000.00")
    private BigDecimal price;

    @Schema(description = "Giá khuyến mãi (VND)", example = "299000.00")
    private BigDecimal salePrice;

    @Schema(description = "Giá thực tế áp dụng (VND)", example = "299000.00")
    private BigDecimal effectivePrice;

    @Schema(description = "Đơn vị tiền tệ", example = "VND")
    private String currency;

    @Schema(description = "Khóa học có miễn phí hay không", example = "false")
    private Boolean isFree;

    @Schema(description = "Trạng thái công khai", example = "true")
    private Boolean published;

    @Schema(description = "Tổng số bài học", example = "12")
    private Integer totalLessons;

    @Schema(description = "Thời gian tạo", example = "2026-07-24T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Thời gian cập nhật gần nhất", example = "2026-07-24T10:00:00")
    private LocalDateTime updatedAt;
}
