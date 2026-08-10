package com.englishlms.course.dto;

import com.englishlms.course.entity.OrderStatus;
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
@Schema(description = "Thông tin chi tiết đơn hàng")
public class OrderResponse {

    @Schema(description = "ID đơn hàng", example = "550e8400-e29b-41d4-a716-446655440001")
    private UUID id;

    @Schema(description = "Mã đơn hàng duy nhất", example = "LMS202608101455001")
    private String orderCode;

    @Schema(description = "ID người mua", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID userId;

    @Schema(description = "Email người mua", example = "student@gmail.com")
    private String userEmailSnapshot;

    @Schema(description = "ID khóa học", example = "550e8400-e29b-41d4-a716-446655440001")
    private UUID courseId;

    @Schema(description = "Tên khóa học tại thời điểm mua", example = "English Grammar 1")
    private String courseTitleSnapshot;

    @Schema(description = "Giá gốc khóa học", example = "499000.00")
    private BigDecimal originalPrice;

    @Schema(description = "Số tiền giảm giá", example = "200000.00")
    private BigDecimal discountAmount;

    @Schema(description = "Tổng tiền thanh toán", example = "299000.00")
    private BigDecimal totalAmount;

    @Schema(description = "Đơn vị tiền tệ", example = "VND")
    private String currency;

    @Schema(description = "Trạng thái đơn hàng (PENDING, PAID, FAILED, CANCELLED, EXPIRED)", example = "PENDING")
    private OrderStatus status;

    @Schema(description = "Cổng thanh toán", example = "VNPAY")
    private String paymentProvider;

    @Schema(description = "Thời gian tạo đơn", example = "2026-08-10T14:55:00")
    private LocalDateTime createdAt;

    @Schema(description = "Thời gian hết hạn đơn hàng", example = "2026-08-10T15:10:00")
    private LocalDateTime expiresAt;

    @Schema(description = "Thời gian thanh toán thành công", example = "2026-08-10T15:02:00")
    private LocalDateTime paidAt;
}
