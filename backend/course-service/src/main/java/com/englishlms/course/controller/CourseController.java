package com.englishlms.course.controller;

import com.englishlms.course.dto.CourseAccessResponse;
import com.englishlms.course.dto.CourseRequest;
import com.englishlms.course.dto.CourseResponse;
import com.englishlms.course.dto.EnrollmentResponse;
import com.englishlms.course.dto.LessonResponse;
import com.englishlms.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/api/v1/courses", "/courses"})
@Tag(name = "Courses", description = "Quản lý Khóa học tiếng Anh")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả khóa học (Công khai)", description = "Xem toàn bộ danh sách các khóa học tiếng Anh có sẵn trong hệ thống.")
    public ResponseEntity<com.englishlms.course.dto.ApiResponse<List<CourseResponse>>> getAllCourses() {
        List<CourseResponse> courses = courseService.getAllCourses();
        return ResponseEntity.ok(com.englishlms.course.dto.ApiResponse.success("Lấy danh sách khóa học thành công", courses));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết khóa học theo ID (Công khai)", description = "Tra cứu thông tin chi tiết của một khóa học theo UUID.")
    public ResponseEntity<com.englishlms.course.dto.ApiResponse<CourseResponse>> getCourseById(
            @Parameter(description = "Mã UUID khóa học", required = true) @PathVariable UUID id
    ) {
        CourseResponse course = courseService.getCourseById(id);
        return ResponseEntity.ok(com.englishlms.course.dto.ApiResponse.success("Lấy thông tin khóa học thành công", course));
    }

    @GetMapping("/{courseId}/access")
    @Operation(summary = "Kiểm tra quyền truy cập và học thử khóa học", description = "Kiểm tra xem người dùng hiện tại có quyền học thử 5 bài hay học toàn bộ khóa học hay không.")
    public ResponseEntity<com.englishlms.course.dto.ApiResponse<CourseAccessResponse>> getCourseAccess(
            @PathVariable UUID courseId,
            Principal principal
    ) {
        String email = principal != null ? principal.getName() : null;
        CourseAccessResponse access = courseService.getCourseAccess(courseId, email);
        return ResponseEntity.ok(com.englishlms.course.dto.ApiResponse.success("Kiểm tra quyền truy cập thành công", access));
    }

    @PostMapping("/{courseId}/trial")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Bắt đầu học thử 5 bài miễn phí", description = "Tạo bản ghi đăng ký TRIAL cho phép học viên học thử 5 bài đầu tiên.")
    public ResponseEntity<com.englishlms.course.dto.ApiResponse<EnrollmentResponse>> startTrial(
            @PathVariable UUID courseId,
            Principal principal
    ) {
        if (principal == null || principal.getName() == null) {
            throw new com.englishlms.course.exception.AppException("Bạn chưa đăng nhập. Vui lòng đăng nhập để bắt đầu học thử.", HttpStatus.UNAUTHORIZED);
        }
        String email = principal.getName();
        EnrollmentResponse response = courseService.startTrial(courseId, email, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(com.englishlms.course.dto.ApiResponse.success("Đã bắt đầu đăng ký học thử 5 bài", response));
    }

    @GetMapping("/{id}/enrolled-count")
    @Operation(summary = "Lấy số lượng học viên đã đăng ký khóa học", description = "Thống kê số lượt học viên đã đăng ký khóa học này.")
    public ResponseEntity<com.englishlms.course.dto.ApiResponse<Long>> getEnrolledCount(
            @Parameter(description = "Mã UUID khóa học", required = true) @PathVariable UUID id
    ) {
        long count = courseService.getEnrolledCountByCourse(id);
        return ResponseEntity.ok(com.englishlms.course.dto.ApiResponse.success("Lấy số lượng học viên đăng ký thành công", count));
    }

    @GetMapping("/{courseId}/lessons")
    @Operation(summary = "Lấy danh sách bài học thuộc khóa học", description = "Xem danh sách các bài học thuộc về khóa học tương ứng.")
    public ResponseEntity<com.englishlms.course.dto.ApiResponse<List<LessonResponse>>> getLessonsByCourseId(
            @Parameter(description = "Mã UUID khóa học", required = true) @PathVariable UUID courseId
    ) {
        List<LessonResponse> lessons = courseService.getLessonsByCourseId(courseId);
        return ResponseEntity.ok(com.englishlms.course.dto.ApiResponse.success("Lấy danh sách bài học thành công", lessons));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Thêm mới khóa học (Dành cho ADMIN)", description = "Tạo một khóa học tiếng Anh mới vào hệ thống.")
    public ResponseEntity<com.englishlms.course.dto.ApiResponse<CourseResponse>> createCourse(
            @Valid @RequestBody CourseRequest request
    ) {
        CourseResponse response = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(com.englishlms.course.dto.ApiResponse.success("Tạo khóa học thành công", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Cập nhật khóa học (Dành cho ADMIN)", description = "Cập nhật thông tin tiêu đề, mô tả, trình độ hoặc ảnh của khóa học.")
    public ResponseEntity<com.englishlms.course.dto.ApiResponse<CourseResponse>> updateCourse(
            @Parameter(description = "Mã UUID khóa học", required = true) @PathVariable UUID id,
            @Valid @RequestBody CourseRequest request
    ) {
        CourseResponse response = courseService.updateCourse(id, request);
        return ResponseEntity.ok(com.englishlms.course.dto.ApiResponse.success("Cập nhật khóa học thành công", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Xóa khóa học (Dành cho ADMIN)", description = "Xóa một khóa học và tất cả bài học liên quan khỏi hệ thống.")
    public ResponseEntity<com.englishlms.course.dto.ApiResponse<Void>> deleteCourse(
            @Parameter(description = "Mã UUID khóa học", required = true) @PathVariable UUID id
    ) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok(com.englishlms.course.dto.ApiResponse.success("Xóa khóa học thành công"));
    }
}
