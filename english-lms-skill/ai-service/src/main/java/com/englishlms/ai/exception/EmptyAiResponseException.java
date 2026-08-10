package com.englishlms.ai.exception;

public class EmptyAiResponseException extends AppException {
    public EmptyAiResponseException(String message) {
        super(message, "AI_EMPTY_RESPONSE");
    }

    public EmptyAiResponseException(String message, Throwable cause) {
        super(message, "AI_EMPTY_RESPONSE", cause);
    }
}
