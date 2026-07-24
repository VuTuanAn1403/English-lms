package com.englishlms.ai.controller;

import com.englishlms.ai.dto.ApiResponse;
import com.englishlms.ai.dto.ChatHistoryResponse;
import com.englishlms.ai.dto.ChatRequest;
import com.englishlms.ai.dto.ChatResponse;
import com.englishlms.ai.dto.GrammarCheckRequest;
import com.englishlms.ai.dto.GrammarCheckResponse;
import com.englishlms.ai.dto.QuizQuestionDto;
import com.englishlms.ai.dto.QuizRequest;
import com.englishlms.ai.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping({"/api/v1/ai", "/ai"})
@Tag(name = "AI Service", description = "Trợ lý AI - Chat, Grammar Checker & Quiz Generator")
@SecurityRequirement(name = "bearerAuth")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/chat")
    @Operation(summary = "Hỏi đáp Trợ lý AI (Trả lời bằng tiếng Việt)")
    public ResponseEntity<ApiResponse<ChatResponse>> chat(
            Principal principal,
            @Valid @RequestBody ChatRequest request
    ) {
        String email = getEmail(principal);
        ChatResponse response = aiService.chat(email, request);
        return ResponseEntity.ok(ApiResponse.success("Phản hồi từ Trợ lý AI", response));
    }

    @PostMapping("/grammar")
    @Operation(summary = "Kiểm tra & sửa lỗi ngữ pháp tiếng Anh")
    public ResponseEntity<ApiResponse<GrammarCheckResponse>> checkGrammar(
            Principal principal,
            @Valid @RequestBody GrammarCheckRequest request
    ) {
        String email = getEmail(principal);
        GrammarCheckResponse response = aiService.checkGrammar(email, request);
        return ResponseEntity.ok(ApiResponse.success("Kiểm tra ngữ pháp hoàn tất", response));
    }

    @PostMapping("/quiz")
    @Operation(summary = "Sinh câu hỏi trắc nghiệm tự động theo bài học/chủ đề")
    public ResponseEntity<ApiResponse<List<QuizQuestionDto>>> generateQuiz(
            Principal principal,
            @Valid @RequestBody QuizRequest request
    ) {
        String email = getEmail(principal);
        List<QuizQuestionDto> questions = aiService.generateQuiz(email, request);
        return ResponseEntity.ok(ApiResponse.success("Sinh câu hỏi trắc nghiệm thành công", questions));
    }

    @GetMapping("/history")
    @Operation(summary = "Xem lịch sử trò chuyện AI của người dùng")
    public ResponseEntity<ApiResponse<List<ChatHistoryResponse>>> getHistory(Principal principal) {
        String email = getEmail(principal);
        List<ChatHistoryResponse> history = aiService.getHistory(email);
        return ResponseEntity.ok(ApiResponse.success("Lấy lịch sử trò chuyện AI thành công", history));
    }

    private String getEmail(Principal principal) {
        return principal != null ? principal.getName() : "student@gmail.com";
    }
}
