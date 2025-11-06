package org.example.taskservice.exception.user;

import org.springframework.http.HttpStatus;

public class BadRequestException extends UserException {
    public BadRequestException() {
        super("Введены некорректные данные", HttpStatus.BAD_REQUEST );
    }
}
