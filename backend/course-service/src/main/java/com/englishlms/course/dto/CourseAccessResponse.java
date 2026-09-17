package com.englishlms.course.dto;

import com.englishlms.course.entity.EnrollmentStatus;
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
@Schema(description = "Thông tin quyền truy cập và học thử của khóa học")
public class CourseAccessResponse {

    @Schema(description = "Người dùng đã đăng nhập hay chưa", example = "true")
    private boolean authenticated;

    @Schema(description = "Trạng thái đăng ký học", example = "TRIAL")
    private EnrollmentStatus enrollmentStatus;

    @Schema(description = "Có thể bắt đầu học thử 5 bài hay không", example = "true")
    private boolean canStartTrial;

    @Schema(description = "Giới hạn số bài học thử miễn phí", example = "5")
    @Builder.Default
    private int trialLessonLimit = 5;

    @Schema(description = "Số bài học thử đã hoàn thành", example = "2")
    private int completedTrialLessons;

    @Schema(description = "Có quyền truy cập toàn bộ bài học hay không", example = "false")
    private boolean hasFullAccess;

    @Schema(description = "Cần mua khóa học để xem tiếp bài học hay không", example = "true")
    private boolean purchaseRequired;

    @Schema(description = "Giá thực tế phải trả", example = "299000.00")
    private BigDecimal effectivePrice;

    @Schema(description = "Đơn vị tiền tệ", example = "VND")
    private String currency;
}
