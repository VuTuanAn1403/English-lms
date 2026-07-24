package com.englishlms.course.controller;

import com.englishlms.course.dto.ApiResponse;
import com.englishlms.course.dto.EnrollmentRequest;
import com.englishlms.course.dto.EnrollmentResponse;
import com.englishlms.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping({"/api/v1/enrollments", "/enrollments"})
@Tag(name = "Enrollment Management", description = "API Đăng ký & Theo dõi tiến độ học tập")
@SecurityRequirement(name = "bearerAuth")
public class EnrollmentController {

    private final CourseService courseService;

    public EnrollmentController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    @Operation(summary = "Đăng ký khóa học mới")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enroll(
            Principal principal,
            @Valid @RequestBody EnrollmentRequest request
    ) {
        String email = principal != null ? principal.getName() : "student@gmail.com";
        EnrollmentResponse response = courseService.enroll(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Đăng ký khóa học thành công", response));
    }

    @GetMapping("/my-courses")
    @Operation(summary = "Danh sách khóa học người dùng đã đăng ký")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getMyEnrollments(Principal principal) {
        String email = principal != null ? principal.getName() : "student@gmail.com";
        List<EnrollmentResponse> enrollments = courseService.getMyEnrollments(email);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách khóa học thành công", enrollments));
    }
}
