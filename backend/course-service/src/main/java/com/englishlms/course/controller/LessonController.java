package com.englishlms.course.controller;

import com.englishlms.course.dto.ApiResponse;
import com.englishlms.course.dto.LessonReorderRequest;
import com.englishlms.course.dto.LessonRequest;
import com.englishlms.course.dto.LessonResponse;
import com.englishlms.course.dto.PageResponse;
import com.englishlms.course.service.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/api/v1/lessons", "/lessons"})
@Tag(name = "Lessons", description = "Quản lý Bài học tiếng Anh")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách bài học phân trang", description = "Hỗ trợ phân trang, tìm kiếm từ khóa theo tiêu đề/nội dung, lọc theo khóa học và trạng thái xuất bản.")
    public ResponseEntity<ApiResponse<PageResponse<LessonResponse>>> getLessons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UUID courseId,
            @RequestParam(required = false) Boolean isPublished,
            @RequestParam(defaultValue = "lessonOrder,asc") String sort
    ) {
        PageResponse<LessonResponse> response = lessonService.getLessons(page, size, keyword, courseId, isPublished, sort);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách bài học thành công", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết bài học theo ID", description = "Xem thông tin chi tiết, nội dung bài giảng, video URL và tài liệu PDF (Thực thi giới hạn 5 bài học thử đối với học viên chưa mua).")
    public ResponseEntity<ApiResponse<LessonResponse>> getLessonById(
            @Parameter(description = "Mã UUID bài học", required = true) @PathVariable UUID id,
            Principal principal,
            Authentication authentication
    ) {
        String email = principal != null ? principal.getName() : null;
        String role = null;
        if (authentication != null && authentication.getAuthorities() != null) {
            role = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));
        }
        LessonResponse response = lessonService.getLessonByIdWithAuthorization(id, email, role);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin bài học thành công", response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Thêm mới bài học (Dành cho ADMIN)", description = "Tạo một bài học mới thuộc về một khóa học xác định.")
    public ResponseEntity<ApiResponse<LessonResponse>> createLesson(
            @Valid @RequestBody LessonRequest request
    ) {
        LessonResponse response = lessonService.createLesson(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Tạo bài học thành công", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Cập nhật bài học (Dành cho ADMIN)", description = "Chỉnh sửa thông tin tiêu đề, mô tả, nội dung, video, tài liệu, thời lượng, thứ tự và trạng thái xuất bản.")
    public ResponseEntity<ApiResponse<LessonResponse>> updateLesson(
            @PathVariable UUID id,
            @Valid @RequestBody LessonRequest request
    ) {
        LessonResponse response = lessonService.updateLesson(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật bài học thành công", response));
    }

    @PatchMapping("/{id}/reorder")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Thay đổi thứ tự bài học (Dành cho ADMIN)", description = "Cập nhật lại vị trí thứ tự (Order Index) của bài học.")
    public ResponseEntity<ApiResponse<LessonResponse>> reorderLesson(
            @PathVariable UUID id,
            @Valid @RequestBody LessonReorderRequest request
    ) {
        LessonResponse response = lessonService.reorderLesson(id, request.getNewOrderIndex());
        return ResponseEntity.ok(ApiResponse.success("Thay đổi thứ tự bài học thành công", response));
    }

    @PatchMapping("/{id}/move-up")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Đẩy bài học lên trước một vị trí (Dành cho ADMIN)", description = "Giảm thứ tự bài học đi 1 đơn vị.")
    public ResponseEntity<ApiResponse<LessonResponse>> moveUp(@PathVariable UUID id) {
        LessonResponse response = lessonService.moveUp(id);
        return ResponseEntity.ok(ApiResponse.success("Đẩy bài học lên trước thành công", response));
    }

    @PatchMapping("/{id}/move-down")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Đẩy bài học xuống sau một vị trí (Dành cho ADMIN)", description = "Tăng thứ tự bài học lên 1 đơn vị.")
    public ResponseEntity<ApiResponse<LessonResponse>> moveDown(@PathVariable UUID id) {
        LessonResponse response = lessonService.moveDown(id);
        return ResponseEntity.ok(ApiResponse.success("Đẩy bài học xuống sau thành công", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Xóa bài học (Dành cho ADMIN)", description = "Xóa bài học khỏi cơ sở dữ liệu. Tự động xóa dữ liệu học tập liên quan để đảm bảo không bị lỗi Foreign Key.")
    public ResponseEntity<ApiResponse<Void>> deleteLesson(@PathVariable UUID id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa bài học thành công", null));
    }
}
