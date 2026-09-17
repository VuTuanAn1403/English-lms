package com.englishlms.user.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends AppException {

    public EmailAlreadyExistsException(String email) {
        super("Email đã tồn tại: " + email, HttpStatus.BAD_REQUEST);
    }
}
