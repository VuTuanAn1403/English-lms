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
@Schema(description = "Yêu cầu đăng ký khóa học")
public class EnrollmentRequest {

    @Schema(description = "ID khóa học muốn đăng ký", example = "550e8400-e29b-41d4-a716-446655440001")
    @NotNull(message = "Mã khóa học không được để trống")
    private UUID courseId;
}

