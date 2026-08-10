package com.englishlms.ai.exception;

public class AiNetworkException extends AppException {
    public AiNetworkException(String message) {
        super(message);
    }

    public AiNetworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
