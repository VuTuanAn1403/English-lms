package com.englishlms.ai.exception;

public class RateLimitException extends AppException {
    public RateLimitException(String message) {
        super(message, "AI_RATE_LIMIT_EXCEEDED");
    }

    public RateLimitException(String message, Throwable cause) {
        super(message, "AI_RATE_LIMIT_EXCEEDED", cause);
    }
}
