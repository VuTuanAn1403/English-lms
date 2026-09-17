package com.englishlms.ai.exception;

import com.englishlms.ai.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidApiKeyException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidApiKeyException(InvalidApiKeyException ex) {
        log.error("Gemini Invalid API Key Error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ex.getMessage(), ex.getErrorCode()));
    }

    @ExceptionHandler(QuotaExceededException.class)
    public ResponseEntity<ApiResponse<Object>> handleQuotaExceededException(QuotaExceededException ex) {
        log.warn("Gemini Quota Exceeded Error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(ApiResponse.error(ex.getMessage(), ex.getErrorCode()));
    }

    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<ApiResponse<Object>> handleRateLimitException(RateLimitException ex) {
        log.warn("Gemini Rate Limit Error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(ApiResponse.error(ex.getMessage(), ex.getErrorCode()));
    }

    @ExceptionHandler(AiTimeoutException.class)
    public ResponseEntity<ApiResponse<Object>> handleAiTimeoutException(AiTimeoutException ex) {
        log.error("Gemini AI Timeout Error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                .body(ApiResponse.error(ex.getMessage(), ex.getErrorCode()));
    }

    @ExceptionHandler(GeminiUnavailableException.class)
    public ResponseEntity<ApiResponse<Object>> handleGeminiUnavailableException(GeminiUnavailableException ex) {
        log.error("Gemini Service Unavailable Error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(ex.getMessage(), ex.getErrorCode()));
    }

    @ExceptionHandler(EmptyAiResponseException.class)
    public ResponseEntity<ApiResponse<Object>> handleEmptyAiResponseException(EmptyAiResponseException ex) {
        log.error("Empty AI Response Error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ApiResponse.error(ex.getMessage(), ex.getErrorCode()));
    }

    @ExceptionHandler(InvalidPromptException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidPromptException(InvalidPromptException ex) {
        log.warn("Invalid Prompt Error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage(), ex.getErrorCode()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), "NOT_FOUND"));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequestException(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage(), "BAD_REQUEST"));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnauthorizedException(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ex.getMessage(), "UNAUTHORIZED"));
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpClientErrorException(HttpClientErrorException ex) {
        log.error("HTTP Client Error from Gemini API: Status [{}] Body [{}]", ex.getStatusCode(), ex.getResponseBodyAsString());
        if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED || ex.getStatusCode() == HttpStatus.FORBIDDEN) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("API Key Google Gemini không hợp lệ hoặc bị từ chối truy cập.", "AI_INVALID_API_KEY"));
        } else if (ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(ApiResponse.error("Đã vượt quá hạn mức truy cập (Quota/Rate Limit) của Google Gemini API.", "AI_QUOTA_EXCEEDED"));
        }
        return ResponseEntity.status(ex.getStatusCode())
                .body(ApiResponse.error("Lỗi yêu cầu tới Gemini AI: " + ex.getMessage(), "AI_CLIENT_ERROR"));
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpServerErrorException(HttpServerErrorException ex) {
        log.error("HTTP Server Error from Gemini API: Status [{}] Body [{}]", ex.getStatusCode(), ex.getResponseBodyAsString());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error("Máy chủ Google Gemini gặp sự cố tạm thời. Vui lòng thử lại sau!", "AI_SERVICE_UNAVAILABLE"));
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceAccessException(ResourceAccessException ex) {
        log.error("Resource Access Error connecting to Gemini API: {}", ex.getMessage());
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("timeout")) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                    .body(ApiResponse.error("Kết nối tới Google Gemini API bị quá thời gian (Timeout).", "AI_TIMEOUT"));
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error("Không thể kết nối tới máy chủ Google Gemini (Network Connection Error).", "AI_SERVICE_UNAVAILABLE"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Dữ liệu đầu vào không hợp lệ", "AI_INVALID_PROMPT", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex) {
        log.error("Unhandled exception in AI Service: ", ex);
        String msg = ex.getMessage() != null ? ex.getMessage() : "Lỗi không xác định";
        if (msg.contains("credentials") || msg.contains("Credentials") || msg.contains("API_KEY") || msg.contains("401") || msg.contains("UNAUTHORIZED")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Google Gemini API Key chưa được xác thực hoặc cần khai báo GCP Credentials.", "AI_INVALID_API_KEY"));
        } else if (msg.contains("RESOURCE_EXHAUSTED") || msg.contains("429") || msg.contains("quota")) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(ApiResponse.error("Đã vượt quá hạn mức sử dụng (Quota/Rate Limit) của Google Gemini API.", "AI_QUOTA_EXCEEDED"));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Đã xảy ra lỗi hệ thống AI Service: " + msg, "AI_INTERNAL_ERROR"));
    }
}
