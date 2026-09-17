package com.englishlms.course.exception;

import org.springframework.http.HttpStatus;

public class CourseAlreadyOwnedException extends AppException {
    public CourseAlreadyOwnedException(String message) {
        super(message, HttpStatus.CONFLICT, "COURSE_ALREADY_OWNED");
    }
}
