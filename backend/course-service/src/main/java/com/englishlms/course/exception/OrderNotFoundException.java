package com.englishlms.course.exception;

import org.springframework.http.HttpStatus;

public class OrderNotFoundException extends AppException {
    public OrderNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND");
    }
}
