package com.englishlms.course.controller;

import com.englishlms.course.dto.AdminProgressAnalyticsResponse;
import com.englishlms.course.dto.ApiResponse;
import com.englishlms.course.dto.CourseProgressResponse;
import com.englishlms.course.dto.EnrollmentResponse;
import com.englishlms.course.dto.ProgressRequest;
import com.englishlms.course.exception.AppException;
import com.englishlms.course.service.ProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/api/v1/progress", "/progress"})
@Tag(name = "Learning Progress", description = "Hệ thống theo dõi & quản lý tiến độ học tập chi tiết")
@SecurityRequirement(name = "Bearer Authentication")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @PostMapping("/complete")
    @Operation(summary = "Đánh dấu bài học hoàn thành", description = "Đánh dấu bài học là hoàn thành và tự động tính lại phần trăm tiến độ khóa học.")
    public ResponseEntity<ApiResponse<CourseProgressResponse>> completeLesson(
            Principal principal,
            @Valid @RequestBody ProgressRequest request
    ) {
        String email = extractEmailOrThrow(principal);
        CourseProgressResponse response = progressService.completeLesson(email, request);
        return ResponseEntity.ok(ApiResponse.success("Đã đánh dấu hoàn thành bài học", response));
    }

    @PostMapping("/uncomplete")
    @Operation(summary = "Bỏ đánh dấu hoàn thành bài học", description = "Bỏ đánh dấu hoàn thành bài học và tự động tính lại phần trăm tiến độ.")
    public ResponseEntity<ApiResponse<CourseProgressResponse>> uncompleteLesson(
            Principal principal,
            @Valid @RequestBody ProgressRequest request
    ) {
        String email = extractEmailOrThrow(principal);
        CourseProgressResponse response = progressService.uncompleteLesson(email, request);
        return ResponseEntity.ok(ApiResponse.success("Đã bỏ hoàn thành bài học", response));
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Xem tiến độ chi tiết của khóa học", description = "Lấy % tiến độ, số bài đã học, danh sách checklist bài học và ID bài học kế tiếp.")
    public ResponseEntity<ApiResponse<CourseProgressResponse>> getCourseProgress(
            Principal principal,
            @PathVariable UUID courseId
    ) {
        String email = extractEmailOrThrow(principal);
        CourseProgressResponse response = progressService.getCourseProgress(email, courseId);
        return ResponseEntity.ok(ApiResponse.success("Lấy tiến độ khóa học thành công", response));
    }

    @GetMapping("/me")
    @Operation(summary = "Danh sách toàn bộ tiến độ học tập của tôi", description = "Xem danh sách tiến độ chi tiết tất cả khóa học người dùng đang học.")
    public ResponseEntity<ApiResponse<List<CourseProgressResponse>>> getMyProgressList(Principal principal) {
        String email = extractEmailOrThrow(principal);
        List<CourseProgressResponse> list = progressService.getMyProgressList(email);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách tiến độ cá nhân thành công", list));
    }

    @GetMapping("/admin")
    @Operation(summary = "Admin xem danh sách tiến độ tất cả học viên", description = "Dành cho Quản trị viên theo dõi danh sách tiến độ của toàn bộ học viên.")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getAdminProgressList(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status
    ) {
        List<EnrollmentResponse> list = progressService.getAdminProgressList(search, status);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách tiến độ học viên cho Admin thành công", list));
    }

    @GetMapping("/admin/analytics")
    @Operation(summary = "Admin xem báo cáo phân tích tiến độ & Top 10", description = "Lấy báo cáo phân bổ tiến độ (0-25%, 25-50%, 50-75%, 75-100%), Top 10 học viên chăm chỉ và Top 10 khóa học hoàn thành.")
    public ResponseEntity<ApiResponse<AdminProgressAnalyticsResponse>> getAdminProgressAnalytics() {
        AdminProgressAnalyticsResponse analytics = progressService.getAdminProgressAnalytics();
        return ResponseEntity.ok(ApiResponse.success("Lấy báo cáo phân tích tiến độ cho Admin thành công", analytics));
    }

    private String extractEmailOrThrow(Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank() || principal.getName().contains("anonymous")) {
            throw new AppException("Bạn chưa đăng nhập hoặc phiên làm việc đã hết hạn.", HttpStatus.UNAUTHORIZED);
        }
        return principal.getName();
    }
}
