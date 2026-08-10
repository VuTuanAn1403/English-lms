package com.englishlms.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Yêu cầu tạo đơn hàng mua khóa học")
public class CreateOrderRequest {

    @NotNull(message = "Mã khóa học không được để trống")
    @Schema(description = "Mã UUID khóa học muốn mua", example = "550e8400-e29b-41d4-a716-446655440001")
    private UUID courseId;

    @Schema(description = "Nhà cung cấp thanh toán (VNPAY, MOCK)", example = "VNPAY")
    @Builder.Default
    private String paymentProvider = "VNPAY";
}
