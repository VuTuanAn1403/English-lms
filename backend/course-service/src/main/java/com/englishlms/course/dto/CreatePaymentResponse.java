package com.englishlms.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Phản hồi thông tin liên kết thanh toán")
public class CreatePaymentResponse {

    @Schema(description = "Mã đơn hàng", example = "LMS202608101455001")
    private String orderCode;

    @Schema(description = "Đường dẫn trang thanh toán (VNPay / Mock Checkout)", example = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?...")
    private String paymentUrl;

    @Schema(description = "Nhà cung cấp thanh toán", example = "VNPAY")
    private String provider;

    @Schema(description = "Số tiền thanh toán", example = "299000.00")
    private BigDecimal totalAmount;

    @Schema(description = "Thời gian hết hạn link thanh toán", example = "2026-08-10T15:10:00")
    private LocalDateTime expiresAt;
}
