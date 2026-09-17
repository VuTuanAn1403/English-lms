package com.englishlms.course.controller;

import com.englishlms.course.dto.ApiResponse;
import com.englishlms.course.dto.OrderResponse;
import com.englishlms.course.dto.PageResponse;
import com.englishlms.course.dto.RevenueReportResponse;
import com.englishlms.course.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping({"/api/v1/admin", "/admin"})
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Revenue & Orders", description = "Thống kê Doanh thu và Quản lý Đơn hàng (Dành cho ADMIN)")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminRevenueController {

    private final OrderService orderService;

    public AdminRevenueController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/revenue")
    @Operation(summary = "Thống kê báo cáo doanh thu hệ thống (Dành cho ADMIN)", description = "Tổng hợp tổng doanh thu, số đơn thanh toán thành công, AOV, top khóa học và biểu đồ theo ngày/tháng.")
    public ResponseEntity<ApiResponse<RevenueReportResponse>> getRevenueReport(
            @Parameter(description = "Từ ngày (YYYY-MM-DD)", example = "2026-07-01")
            @RequestParam(required = false) String from,
            @Parameter(description = "Đến ngày (YYYY-MM-DD)", example = "2026-08-10")
            @RequestParam(required = false) String to,
            @Parameter(description = "Lọc theo ID khóa học")
            @RequestParam(required = false) UUID courseId,
            @Parameter(description = "Gom nhóm biểu đồ: DAY hoặc MONTH", example = "DAY")
            @RequestParam(defaultValue = "DAY") String groupBy
    ) {
        RevenueReportResponse report = orderService.getRevenueReport(from, to, courseId, groupBy);
        return ResponseEntity.ok(ApiResponse.success("Lấy báo cáo doanh thu thành công", report));
    }

    @GetMapping("/orders")
    @Operation(summary = "Quản lý danh sách tất cả đơn hàng (Dành cho ADMIN)", description = "Lấy toàn bộ đơn hàng trong hệ thống có hỗ trợ phân trang, tìm kiếm và bộ lọc.")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String courseId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to
    ) {
        PageResponse<OrderResponse> response = orderService.getAllOrdersForAdmin(page, size, search, status, courseId, from, to);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách đơn hàng cho Admin thành công", response));
    }

    @GetMapping("/orders/{orderCode}")
    @Operation(summary = "Xem chi tiết đơn hàng (Dành cho ADMIN)", description = "Tra cứu thông tin chi tiết một đơn hàng theo mã đơn.")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByCode(@PathVariable String orderCode) {
        OrderResponse response = orderService.getOrderByCode(orderCode, null, "ROLE_ADMIN");
        return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết đơn hàng cho Admin thành công", response));
    }
}
