package com.englishlms.ai.controller;

import com.englishlms.ai.dto.ApiResponse;
import com.englishlms.ai.dto.ChatHistoryResponse;
import com.englishlms.ai.dto.PageResponse;
import com.englishlms.ai.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/v1/ai/admin", "/api/v1/admin/ai", "/admin/ai"})
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "AI Admin Controller", description = "Quản lý & Thống kê Lịch sử tương tác AI dành cho Quản trị viên")
@SecurityRequirement(name = "bearerAuth")
public class AiAdminController {

    private final AiService aiService;

    @GetMapping("/history")
    @Operation(summary = "Admin xem toàn bộ lịch sử tương tác AI", description = "Lấy danh sách tất cả các lượt tương tác AI trên hệ thống, có phân trang, tìm kiếm từ khóa/email và lọc theo promptType")
    public ResponseEntity<ApiResponse<PageResponse<ChatHistoryResponse>>> getAllHistory(
            @Parameter(description = "Từ khóa tìm kiếm (email, prompt)", example = "student@gmail.com")
            @RequestParam(required = false) String search,
            @Parameter(description = "Loại prompt (CHAT, GRAMMAR, QUIZ)", example = "CHAT")
            @RequestParam(required = false) String promptType,
            @Parameter(description = "Số trang (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Kích thước trang", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<ChatHistoryResponse> response = aiService.getAllHistoryForAdmin(search, promptType, page, size);
        return ResponseEntity.ok(ApiResponse.success("Lấy toàn bộ lịch sử AI cho Admin thành công", response));
    }
}
