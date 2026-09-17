package com.englishlms.course.exception;

import org.springframework.http.HttpStatus;

public class PaymentAmountMismatchException extends AppException {
    public PaymentAmountMismatchException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "PAYMENT_AMOUNT_MISMATCH");
    }
}
