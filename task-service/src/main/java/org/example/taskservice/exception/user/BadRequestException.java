package org.example.taskservice.exception.user;

import org.springframework.http.HttpStatus;

public class BadRequestException extends UserException {
    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST );
    }
}
