package org.example.taskservice.exception.user;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserException extends RuntimeException {

    private final String userMessage;

    private final HttpStatus httpStatus; // HTTP

    public UserException(String userMessage, HttpStatus httpStatus) {
        super(userMessage);
        this.userMessage = userMessage;
        this.httpStatus = httpStatus;
    }

}