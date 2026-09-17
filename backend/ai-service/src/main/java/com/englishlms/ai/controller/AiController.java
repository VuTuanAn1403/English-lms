package com.englishlms.ai.controller;

import com.englishlms.ai.dto.ApiResponse;
import com.englishlms.ai.dto.ChatHistoryResponse;
import com.englishlms.ai.dto.ChatRequest;
import com.englishlms.ai.dto.ChatResponse;
import com.englishlms.ai.dto.GrammarCheckRequest;
import com.englishlms.ai.dto.GrammarCheckResponse;
import com.englishlms.ai.dto.PageResponse;
import com.englishlms.ai.dto.QuizQuestionDto;
import com.englishlms.ai.dto.QuizRequest;
import com.englishlms.ai.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping({"/api/v1/ai", "/ai"})
@RequiredArgsConstructor
@Tag(name = "AI Controller", description = "Quản lý các dịch vụ trí tuệ nhân tạo (Chat Assistant, Grammar Checker, Quiz Generator, History)")
@SecurityRequirement(name = "bearerAuth")
public class AiController {

    private final AiService aiService;

    @PostMapping("/chat")
    @Operation(summary = "Trò chuyện với Trợ lý AI", description = "Gửi tin nhắn/câu hỏi cho trợ lý AI và nhận câu trả lời. Gửi kèm sessionId để duy trì ngữ cảnh hội thoại.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Gửi tin nhắn và nhận phản hồi từ AI thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dữ liệu tin nhắn không hợp lệ"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "502", description = "AI trả về dữ liệu không hợp lệ")
    })
    public ResponseEntity<ApiResponse<ChatResponse>> chat(
            @Valid @RequestBody ChatRequest request,
            Principal principal,
            @RequestHeader(value = "X-User-Email", required = false) String headerEmail
    ) {
        String userEmail = resolveUserEmail(principal, headerEmail);
        ChatResponse response = aiService.chat(request, userEmail);
        return ResponseEntity.ok(ApiResponse.success("Phản hồi từ AI Chat thành công", response));
    }

    @PostMapping("/grammar")
    @Operation(summary = "Kiểm tra và sửa lỗi ngữ pháp tiếng Anh", description = "Phân tích đoạn văn, phát hiện lỗi ngữ pháp và đưa ra giải thích, điểm số, quy tắc và ví dụ.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Kiểm tra ngữ pháp thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Văn bản đầu vào không hợp lệ"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "502", description = "AI trả về dữ liệu không đúng định dạng")
    })
    public ResponseEntity<ApiResponse<GrammarCheckResponse>> grammarCheck(
            @Valid @RequestBody GrammarCheckRequest request,
            Principal principal,
            @RequestHeader(value = "X-User-Email", required = false) String headerEmail
    ) {
        String userEmail = resolveUserEmail(principal, headerEmail);
        GrammarCheckResponse response = aiService.grammarCheck(request, userEmail);
        return ResponseEntity.ok(ApiResponse.success("Kiểm tra ngữ pháp thành công", response));
    }

    @PostMapping("/quiz")
    @Operation(summary = "Tự động sinh đề thi trắc nghiệm tiếng Anh", description = "Sinh danh sách câu hỏi trắc nghiệm theo bài học, trình độ CEFR và kỹ năng.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sinh câu hỏi trắc nghiệm thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Thông số yêu cầu sinh đề không hợp lệ"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "502", description = "AI trả về dữ liệu Quiz không đúng định dạng")
    })
    public ResponseEntity<ApiResponse<List<QuizQuestionDto>>> generateQuiz(
            @Valid @RequestBody QuizRequest request,
            Principal principal,
            @RequestHeader(value = "X-User-Email", required = false) String headerEmail
    ) {
        String userEmail = resolveUserEmail(principal, headerEmail);
        List<QuizQuestionDto> response = aiService.generateQuiz(request, userEmail);
        return ResponseEntity.ok(ApiResponse.success("Sinh bài tập trắc nghiệm thành công", response));
    }

    @GetMapping("/history")
    @Operation(summary = "Xem lịch sử tương tác AI", description = "Truy vấn danh sách lịch sử AI của người dùng hiện tại, hỗ trợ lọc theo type và sessionId.")
    public ResponseEntity<ApiResponse<PageResponse<ChatHistoryResponse>>> getHistory(
            @Parameter(description = "Số trang (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Kích thước trang", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Lọc theo loại prompt: CHAT, GRAMMAR, QUIZ", example = "CHAT")
            @RequestParam(required = false) String type,
            @Parameter(description = "Lọc theo session ID", example = "abc-123")
            @RequestParam(required = false) String sessionId,
            Principal principal,
            @RequestHeader(value = "X-User-Email", required = false) String headerEmail
    ) {
        String userEmail = resolveUserEmail(principal, headerEmail);
        PageResponse<ChatHistoryResponse> response = aiService.getHistory(userEmail, type, sessionId, page, size);
        return ResponseEntity.ok(ApiResponse.success("Lấy lịch sử tương tác AI thành công", response));
    }

    @DeleteMapping("/sessions/{sessionId}")
    @Operation(summary = "Xóa phiên trò chuyện", description = "Xóa toàn bộ lịch sử trò chuyện của một session thuộc về người dùng hiện tại.")
    public ResponseEntity<ApiResponse<Void>> deleteSession(
            @PathVariable String sessionId,
            Principal principal,
            @RequestHeader(value = "X-User-Email", required = false) String headerEmail
    ) {
        String userEmail = resolveUserEmail(principal, headerEmail);
        aiService.deleteSession(sessionId, userEmail);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa phiên trò chuyện thành công"));
    }

    @PostMapping("/sessions/{sessionId}/clear")
    @Operation(summary = "Làm mới phiên trò chuyện", description = "Xóa bộ nhớ ngữ cảnh hội thoại của session hiện tại (không xóa lịch sử DB).")
    public ResponseEntity<ApiResponse<Void>> clearSession(
            @PathVariable String sessionId,
            Principal principal,
            @RequestHeader(value = "X-User-Email", required = false) String headerEmail
    ) {
        resolveUserEmail(principal, headerEmail); // Ensure authenticated
        aiService.clearSession(sessionId);
        return ResponseEntity.ok(ApiResponse.success("Đã làm mới phiên trò chuyện"));
    }

    private String resolveUserEmail(Principal principal, String headerEmail) {
        if (principal != null && StringUtils.hasText(principal.getName())) {
            return principal.getName();
        }
        throw new com.englishlms.ai.exception.UnauthorizedException("Bạn chưa đăng nhập hoặc phiên đăng nhập không hợp lệ");
    }
}
