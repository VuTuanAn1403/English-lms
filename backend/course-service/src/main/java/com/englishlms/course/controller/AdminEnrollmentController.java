package com.englishlms.course.controller;

import com.englishlms.course.dto.AdminEnrollmentStatsResponse;
import com.englishlms.course.dto.ApiResponse;
import com.englishlms.course.dto.EnrollmentResponse;
import com.englishlms.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/admin/enrollments", "/admin/enrollments"})
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Enrollment", description = "Quản lý & Thống kê Đăng ký Khóa học dành cho Quản trị viên")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminEnrollmentController {

    private final CourseService courseService;

    public AdminEnrollmentController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    @Operation(summary = "Admin xem toàn bộ danh sách đăng ký", description = "Lấy danh sách tất cả các lượt đăng ký khóa học trên hệ thống kèm tìm kiếm và bộ lọc trạng thái.")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getAllEnrollments(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status
    ) {
        List<EnrollmentResponse> enrollments = courseService.getAllEnrollmentsForAdmin(search, status);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách đăng ký khóa học cho Admin thành công", enrollments));
    }

    @GetMapping("/statistics")
    @Operation(summary = "Admin xem thống kê đăng ký", description = "Thống kê tổng lượt đăng ký, số bài đang học, hoàn thành và tỷ lệ hoàn thành.")
    public ResponseEntity<ApiResponse<AdminEnrollmentStatsResponse>> getEnrollmentStatistics() {
        AdminEnrollmentStatsResponse stats = courseService.getAdminEnrollmentStatistics();
        return ResponseEntity.ok(ApiResponse.success("Lấy thống kê đăng ký khóa học thành công", stats));
    }
}
