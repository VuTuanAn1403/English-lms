package com.englishlms.ai.exception;

public class AiTimeoutException extends AppException {
    public AiTimeoutException(String message) {
        super(message, "AI_TIMEOUT");
    }

    public AiTimeoutException(String message, Throwable cause) {
        super(message, "AI_TIMEOUT", cause);
    }
}
