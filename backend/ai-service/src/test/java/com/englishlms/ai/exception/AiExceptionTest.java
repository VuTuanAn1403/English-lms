package com.englishlms.ai.exception;

import com.englishlms.ai.dto.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AiExceptionTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Kiểm tra xử lý InvalidApiKeyException -> 401 Unauthorized")
    void testInvalidApiKeyException() {
        InvalidApiKeyException ex = new InvalidApiKeyException("API Key không hợp lệ");
        ResponseEntity<ApiResponse<Object>> response = exceptionHandler.handleInvalidApiKeyException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("AI_INVALID_API_KEY", response.getBody().getErrorCode());
    }

    @Test
    @DisplayName("Kiểm tra xử lý QuotaExceededException -> 429 Too Many Requests")
    void testQuotaExceededException() {
        QuotaExceededException ex = new QuotaExceededException("Hết hạn mức Quota");
        ResponseEntity<ApiResponse<Object>> response = exceptionHandler.handleQuotaExceededException(ex);

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertEquals("AI_QUOTA_EXCEEDED", response.getBody().getErrorCode());
    }

    @Test
    @DisplayName("Kiểm tra xử lý RateLimitException -> 429 Too Many Requests")
    void testRateLimitException() {
        RateLimitException ex = new RateLimitException("Vượt quá giới hạn rate limit");
        ResponseEntity<ApiResponse<Object>> response = exceptionHandler.handleRateLimitException(ex);

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertEquals("AI_RATE_LIMIT_EXCEEDED", response.getBody().getErrorCode());
    }

    @Test
    @DisplayName("Kiểm tra xử lý AiTimeoutException -> 504 Gateway Timeout")
    void testAiTimeoutException() {
        AiTimeoutException ex = new AiTimeoutException("Kết nối quá thời gian");
        ResponseEntity<ApiResponse<Object>> response = exceptionHandler.handleAiTimeoutException(ex);

        assertEquals(HttpStatus.GATEWAY_TIMEOUT, response.getStatusCode());
        assertEquals("AI_TIMEOUT", response.getBody().getErrorCode());
    }

    @Test
    @DisplayName("Kiểm tra xử lý GeminiUnavailableException -> 503 Service Unavailable")
    void testGeminiUnavailableException() {
        GeminiUnavailableException ex = new GeminiUnavailableException("Gemini tạm dừng dịch vụ");
        ResponseEntity<ApiResponse<Object>> response = exceptionHandler.handleGeminiUnavailableException(ex);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("AI_SERVICE_UNAVAILABLE", response.getBody().getErrorCode());
    }

    @Test
    @DisplayName("Kiểm tra xử lý EmptyAiResponseException -> 502 Bad Gateway")
    void testEmptyAiResponseException() {
        EmptyAiResponseException ex = new EmptyAiResponseException("Phản hồi rỗng");
        ResponseEntity<ApiResponse<Object>> response = exceptionHandler.handleEmptyAiResponseException(ex);

        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertEquals("AI_EMPTY_RESPONSE", response.getBody().getErrorCode());
    }

    @Test
    @DisplayName("Kiểm tra xử lý InvalidPromptException -> 400 Bad Request")
    void testInvalidPromptException() {
        InvalidPromptException ex = new InvalidPromptException("Prompt không hợp lệ");
        ResponseEntity<ApiResponse<Object>> response = exceptionHandler.handleInvalidPromptException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("AI_INVALID_PROMPT", response.getBody().getErrorCode());
    }
}
