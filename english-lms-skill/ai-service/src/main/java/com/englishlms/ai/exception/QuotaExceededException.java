package com.englishlms.ai.exception;

public class QuotaExceededException extends AppException {
    public QuotaExceededException(String message) {
        super(message, "AI_QUOTA_EXCEEDED");
    }

    public QuotaExceededException(String message, Throwable cause) {
        super(message, "AI_QUOTA_EXCEEDED", cause);
    }
}
