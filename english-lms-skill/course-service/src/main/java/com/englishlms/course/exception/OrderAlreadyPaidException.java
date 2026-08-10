package com.englishlms.course.exception;

import org.springframework.http.HttpStatus;

public class OrderAlreadyPaidException extends AppException {
    public OrderAlreadyPaidException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "ORDER_ALREADY_PAID");
    }
}
