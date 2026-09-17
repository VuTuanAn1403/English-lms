package com.englishlms.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Top khóa học theo doanh thu")
public class TopCourseRevenueItem {

    @Schema(description = "ID khóa học")
    private UUID courseId;

    @Schema(description = "Tên khóa học", example = "English Grammar 1")
    private String courseTitle;

    @Schema(description = "Doanh thu tích lũy", example = "5980000.00")
    private BigDecimal revenue;

    @Schema(description = "Số lượt bán thành công", example = "12")
    private long purchaseCount;
}
