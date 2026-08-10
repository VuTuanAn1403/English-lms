package com.englishlms.course.exception;

import org.springframework.http.HttpStatus;

public class CourseNotPublishedException extends AppException {
    public CourseNotPublishedException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "COURSE_NOT_PUBLISHED");
    }
}
