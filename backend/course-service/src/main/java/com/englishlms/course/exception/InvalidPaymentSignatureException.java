package com.englishlms.course.exception;

import org.springframework.http.HttpStatus;

public class InvalidPaymentSignatureException extends AppException {
    public InvalidPaymentSignatureException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_PAYMENT_SIGNATURE");
    }
}
