package com.englishlms.course.exception;

import org.springframework.http.HttpStatus;

public class OrderExpiredException extends AppException {
    public OrderExpiredException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "ORDER_EXPIRED");
    }
}
