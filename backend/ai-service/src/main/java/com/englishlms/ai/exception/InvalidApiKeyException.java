package com.englishlms.ai.exception;

public class InvalidApiKeyException extends AppException {
    public InvalidApiKeyException(String message) {
        super(message, "AI_INVALID_API_KEY");
    }

    public InvalidApiKeyException(String message, Throwable cause) {
        super(message, "AI_INVALID_API_KEY", cause);
    }
}
