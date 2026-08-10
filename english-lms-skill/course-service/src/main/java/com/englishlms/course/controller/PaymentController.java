package com.englishlms.course.controller;

import com.englishlms.course.dto.ApiResponse;
import com.englishlms.course.dto.CreatePaymentResponse;
import com.englishlms.course.dto.OrderResponse;
import com.englishlms.course.exception.AppException;
import com.englishlms.course.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping({"/api/v1/payments", "/payments"})
@Tag(name = "Payment", description = "Quản lý Cổng thanh toán (VNPay Sandbox & Mock Payment)")
public class PaymentController {

    private final OrderService orderService;

    public PaymentController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{orderCode}/create")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Tạo liên kết thanh toán", description = "Tạo đường dẫn URL chuyển hướng tới cổng thanh toán (VNPay / Mock).")
    public ResponseEntity<ApiResponse<CreatePaymentResponse>> createPaymentUrl(
            @PathVariable String orderCode,
            @RequestParam(required = false) String returnUrl,
            Principal principal
    ) {
        String email = extractEmailOrThrow(principal);
        CreatePaymentResponse response = orderService.createPaymentUrl(orderCode, email, returnUrl);
        return ResponseEntity.ok(ApiResponse.success("Tạo liên kết thanh toán thành công", response));
    }

    @GetMapping("/vnpay/callback")
    @Operation(summary = "VNPay Return Callback (Browser Redirect)", description = "Xử lý kết quả trả về từ VNPay Sandbox trên trình duyệt.")
    public ResponseEntity<ApiResponse<OrderResponse>> vnPayCallback(@RequestParam Map<String, String> queryParams) {
        OrderResponse response = orderService.processPaymentCallback("VNPAY", queryParams);
        return ResponseEntity.ok(ApiResponse.success("Thanh toán đơn hàng thành công", response));
    }

    @GetMapping("/vnpay/ipn")
    @Operation(summary = "VNPay IPN Callback (Server-to-Server)", description = "Xử lý IPN callback từ VNPay Sandbox (Idempotent).")
    public ResponseEntity<Map<String, String>> vnPayIpnGet(@RequestParam Map<String, String> queryParams) {
        try {
            orderService.processPaymentCallback("VNPAY", queryParams);
            return ResponseEntity.ok(Map.of("RspCode", "00", "Message", "Confirm Success"));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("RspCode", "99", "Message", "Uncertain Error: " + e.getMessage()));
        }
    }

    @PostMapping("/vnpay/ipn")
    @Operation(summary = "VNPay IPN POST Callback", description = "Xử lý IPN POST callback từ VNPay Sandbox (Idempotent).")
    public ResponseEntity<Map<String, String>> vnPayIpnPost(@RequestParam Map<String, String> queryParams) {
        return vnPayIpnGet(queryParams);
    }

    @PostMapping("/mock/process")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Mô phỏng thanh toán (Mock Payment cho Demo/Local)", description = "Cho phép học viên tự nhấn nút 'Thanh toán thành công' trên giao diện mô phỏng.")
    public ResponseEntity<ApiResponse<OrderResponse>> processMockPayment(
            @RequestBody Map<String, String> body,
            Principal principal
    ) {
        String email = extractEmailOrThrow(principal);
        String orderCode = body.get("orderCode");
        String status = body.getOrDefault("status", "SUCCESS");

        if (orderCode == null || orderCode.isBlank()) {
            throw new AppException("Mã đơn hàng không được để trống.", HttpStatus.BAD_REQUEST);
        }

        OrderResponse response = orderService.processMockPayment(orderCode, email, status);
        return ResponseEntity.ok(ApiResponse.success("Xử lý thanh toán mô phỏng thành công", response));
    }

    private String extractEmailOrThrow(Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank() || principal.getName().contains("anonymous")) {
            throw new AppException("Bạn chưa đăng nhập hoặc phiên làm việc đã hết hạn.", HttpStatus.UNAUTHORIZED);
        }
        return principal.getName();
    }
}
