package com.englishlms.course.controller;

import com.englishlms.course.dto.ApiResponse;
import com.englishlms.course.dto.CourseRequest;
import com.englishlms.course.dto.CourseResponse;
import com.englishlms.course.dto.LessonResponse;
import com.englishlms.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/api/v1/courses", "/courses"})
@Tag(name = "Course Management", description = "API Quản lý Khóa học tiếng Anh")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả khóa học (Công khai)")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getAllCourses() {
        List<CourseResponse> courses = courseService.getAllCourses();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách khóa học thành công", courses));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết khóa học theo ID (Công khai)")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourseById(@PathVariable UUID id) {
        CourseResponse course = courseService.getCourseById(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin khóa học thành công", course));
    }

    @GetMapping("/{courseId}/lessons")
    @Operation(summary = "Lấy danh sách bài học thuộc khóa học")
    public ResponseEntity<ApiResponse<List<LessonResponse>>> getLessonsByCourseId(@PathVariable UUID courseId) {
        List<LessonResponse> lessons = courseService.getLessonsByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách bài học thành công", lessons));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Thêm mới khóa học (Dành cho ADMIN)")
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(@Valid @RequestBody CourseRequest request) {
        CourseResponse response = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Tạo khóa học thành công", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cập nhật khóa học (Dành cho ADMIN)")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(
            @PathVariable UUID id,
            @Valid @RequestBody CourseRequest request
    ) {
        CourseResponse response = courseService.updateCourse(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật khóa học thành công", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Xóa khóa học (Dành cho ADMIN)")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable UUID id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa khóa học thành công"));
    }
}
