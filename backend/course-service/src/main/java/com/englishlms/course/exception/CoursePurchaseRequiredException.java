package com.englishlms.course.exception;

import org.springframework.http.HttpStatus;

public class CoursePurchaseRequiredException extends AppException {
    public CoursePurchaseRequiredException(String message) {
        super(message, HttpStatus.FORBIDDEN, "COURSE_PURCHASE_REQUIRED");
    }
}
