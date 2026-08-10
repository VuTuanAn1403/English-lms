package com.englishlms.ai.exception;

public class GeminiUnavailableException extends AppException {
    public GeminiUnavailableException(String message) {
        super(message, "AI_SERVICE_UNAVAILABLE");
    }

    public GeminiUnavailableException(String message, Throwable cause) {
        super(message, "AI_SERVICE_UNAVAILABLE", cause);
    }
}
