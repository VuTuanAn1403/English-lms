package com.englishlms.ai.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

    private final String errorCode;

    public AppException(String message) {
        super(message);
        this.errorCode = "AI_INTERNAL_ERROR";
    }

    public AppException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "AI_INTERNAL_ERROR";
    }

    public AppException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
