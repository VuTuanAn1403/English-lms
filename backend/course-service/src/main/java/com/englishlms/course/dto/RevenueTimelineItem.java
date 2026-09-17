package com.englishlms.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Doanh thu theo mốc thời gian")
public class RevenueTimelineItem {

    @Schema(description = "Mốc thời gian (YYYY-MM-DD hoặc YYYY-MM)", example = "2026-08-10")
    private String label;

    @Schema(description = "Doanh thu trong mốc thời gian", example = "2990000.00")
    private BigDecimal revenue;

    @Schema(description = "Số đơn hàng thành công trong mốc thời gian", example = "6")
    private long paidOrdersCount;
}
