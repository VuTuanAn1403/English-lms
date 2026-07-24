package com.englishlms.course.controller;

import com.englishlms.course.dto.ApiResponse;
import com.englishlms.course.dto.LessonRequest;
import com.englishlms.course.dto.LessonResponse;
import com.englishlms.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping({"/api/v1/lessons", "/lessons"})
@Tag(name = "Lesson Management", description = "API Quản lý Bài học tiếng Anh")
public class LessonController {

    private final CourseService courseService;

    public LessonController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết bài học theo ID")
    public ResponseEntity<ApiResponse<LessonResponse>> getLessonById(@PathVariable UUID id) {
        LessonResponse response = courseService.getLessonById(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin bài học thành công", response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Thêm mới bài học (Dành cho ADMIN)")
    public ResponseEntity<ApiResponse<LessonResponse>> createLesson(@Valid @RequestBody LessonRequest request) {
        LessonResponse response = courseService.createLesson(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Tạo bài học thành công", response));
    }
}
