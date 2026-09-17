package com.englishlms.ai.exception;

public class InvalidPromptException extends AppException {
    public InvalidPromptException(String message) {
        super(message, "AI_INVALID_PROMPT");
    }

    public InvalidPromptException(String message, Throwable cause) {
        super(message, "AI_INVALID_PROMPT", cause);
    }
}
