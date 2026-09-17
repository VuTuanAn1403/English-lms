package com.englishlms.course.controller;

import com.englishlms.course.dto.ApiResponse;
import com.englishlms.course.dto.CreateOrderRequest;
import com.englishlms.course.dto.OrderResponse;
import com.englishlms.course.dto.PageResponse;
import com.englishlms.course.exception.AppException;
import com.englishlms.course.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/api/v1/orders", "/orders"})
@Tag(name = "Order", description = "Quản lý Đơn hàng mua Khóa học")
@SecurityRequirement(name = "Bearer Authentication")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Tạo đơn hàng mua khóa học", description = "Tạo đơn hàng mới ở trạng thái PENDING. Giá được đọc trực tiếp từ database.")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            Principal principal,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        String email = extractEmailOrThrow(principal);
        OrderResponse response = orderService.createOrder(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Tạo đơn hàng thành công", response));
    }

    @GetMapping({"/my", "/my-orders"})
    @Operation(summary = "Danh sách đơn hàng của tôi", description = "Truy vấn danh sách đơn hàng đã mua của học viên hiện tại có phân trang.")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal
    ) {
        String email = extractEmailOrThrow(principal);
        PageResponse<OrderResponse> response = orderService.getMyOrders(email, page, size);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách đơn hàng thành công", response));
    }

    @GetMapping("/{orderCode}")
    @Operation(summary = "Lấy chi tiết đơn hàng theo mã đơn", description = "Tra cứu thông tin chi tiết đơn hàng của người mua hoặc Admin.")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByCode(
            @PathVariable String orderCode,
            Principal principal,
            Authentication authentication
    ) {
        String email = extractEmailOrThrow(principal);
        String role = authentication != null && authentication.getAuthorities() != null
                ? authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","))
                : null;
        OrderResponse response = orderService.getOrderByCode(orderCode, email, role);
        return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết đơn hàng thành công", response));
    }

    @PostMapping("/{orderCode}/cancel")
    @Operation(summary = "Hủy đơn hàng đang chờ thanh toán", description = "Hủy đơn hàng ở trạng thái PENDING.")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable String orderCode,
            Principal principal
    ) {
        String email = extractEmailOrThrow(principal);
        OrderResponse response = orderService.cancelOrder(orderCode, email);
        return ResponseEntity.ok(ApiResponse.success("Hủy đơn hàng thành công", response));
    }

    private String extractEmailOrThrow(Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank() || principal.getName().contains("anonymous")) {
            throw new AppException("Bạn chưa đăng nhập hoặc phiên làm việc đã hết hạn.", HttpStatus.UNAUTHORIZED);
        }
        return principal.getName();
    }
}
