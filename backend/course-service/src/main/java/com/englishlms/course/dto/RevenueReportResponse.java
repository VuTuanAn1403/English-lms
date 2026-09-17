package com.englishlms.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Báo cáo thống kê doanh thu dành cho ADMIN")
public class RevenueReportResponse {

    @Schema(description = "Tổng doanh thu thực tế (đã xác minh thanh toán thành công)", example = "15980000.00")
    private BigDecimal totalRevenue;

    @Schema(description = "Số đơn hàng đã thanh toán thành công", example = "32")
    private long totalPaidOrders;

    @Schema(description = "Số học viên đã mua khóa học", example = "28")
    private long totalStudentsCount;

    @Schema(description = "Giá trị đơn hàng trung bình (AOV)", example = "499375.00")
    private BigDecimal averageOrderValue;

    @Schema(description = "Số đơn hàng đang chờ thanh toán (PENDING)", example = "5")
    private long pendingOrdersCount;

    @Schema(description = "Số đơn hàng thất bại hoặc hủy (FAILED / CANCELLED / EXPIRED)", example = "2")
    private long failedOrdersCount;

    @Schema(description = "Dữ liệu doanh thu theo mốc thời gian (ngày/tháng)")
    private List<RevenueTimelineItem> revenueTimeline;

    @Schema(description = "Top các khóa học có doanh thu cao nhất")
    private List<TopCourseRevenueItem> topCourses;
}
