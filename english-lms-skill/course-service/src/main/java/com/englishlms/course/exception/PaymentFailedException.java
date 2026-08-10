package com.englishlms.course.exception;

import org.springframework.http.HttpStatus;

public class PaymentFailedException extends AppException {
    public PaymentFailedException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "PAYMENT_FAILED");
    }
}
