package com.englishlms.course.controller;

import com.englishlms.course.dto.AdminEnrollmentStatsResponse;
import com.englishlms.course.dto.ApiResponse;
import com.englishlms.course.dto.EnrollmentRequest;
import com.englishlms.course.dto.EnrollmentResponse;
import com.englishlms.course.dto.UpdateProgressRequest;
import com.englishlms.course.exception.AppException;
import com.englishlms.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/api/v1/enrollments", "/enrollments"})
@Tag(name = "Enrollment", description = "Đăng ký Khóa học, Hủy đăng ký & Theo dõi Tiến độ")
@SecurityRequirement(name = "Bearer Authentication")
public class EnrollmentController {

    private final CourseService courseService;

    public EnrollmentController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    @Operation(summary = "Đăng ký khóa học mới", description = "Đăng ký khóa học cho người dùng hiện tại dựa trên ID khóa học.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Đăng ký khóa học thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Học viên đã đăng ký khóa học này trước đó",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa xác thực JWT Token",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy khóa học",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enroll(
            Principal principal,
            @Valid @RequestBody EnrollmentRequest request
    ) {
        String email = extractEmailOrThrow(principal);
        EnrollmentResponse response = courseService.enroll(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Đăng ký khóa học thành công", response));
    }

    @DeleteMapping("/{courseId}")
    @Operation(summary = "Hủy đăng ký khóa học", description = "Hủy đăng ký khóa học cho học viên hiện tại.")
    public ResponseEntity<ApiResponse<Void>> unenroll(
            Principal principal,
            @PathVariable UUID courseId
    ) {
        String email = extractEmailOrThrow(principal);
        courseService.unenroll(email, courseId);
        return ResponseEntity.ok(ApiResponse.success("Hủy đăng ký khóa học thành công", null));
    }

    @GetMapping({"/me", "/my-courses"})
    @Operation(summary = "Danh sách khóa học tôi đã đăng ký", description = "Xem danh sách tất cả các khóa học người dùng hiện tại đã đăng ký.")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getMyEnrollments(Principal principal) {
        String email = extractEmailOrThrow(principal);
        List<EnrollmentResponse> enrollments = courseService.getMyEnrollments(email);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách khóa học đã đăng ký thành công", enrollments));
    }

    @GetMapping("/{courseId}")
    @Operation(summary = "Lấy thông tin đăng ký theo khóa học", description = "Kiểm tra xem người dùng đã đăng ký khóa học cụ thể này hay chưa.")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> getEnrollmentByCourse(
            Principal principal,
            @PathVariable UUID courseId
    ) {
        String email = extractEmailOrThrow(principal);
        EnrollmentResponse enrollment = courseService.getEnrollmentByCourse(email, courseId);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin đăng ký thành công", enrollment));
    }

    @PatchMapping("/{courseId}/progress")
    @Operation(summary = "Cập nhật tiến độ bài học", description = "Cập nhật số bài học đã hoàn thành và tự động tính lại phần trăm tiến độ.")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> updateProgress(
            Principal principal,
            @PathVariable UUID courseId,
            @RequestBody UpdateProgressRequest request
    ) {
        String email = extractEmailOrThrow(principal);
        EnrollmentResponse updated = courseService.updateProgress(email, courseId, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật tiến độ thành công", updated));
    }

    private String extractEmailOrThrow(Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank() || principal.getName().contains("anonymous")) {
            throw new AppException("Bạn chưa đăng nhập hoặc phiên làm việc đã hết hạn.", HttpStatus.UNAUTHORIZED);
        }
        return principal.getName();
    }
}
