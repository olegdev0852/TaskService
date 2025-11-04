package org.example.taskservice.exception.user;

import lombok.Getter;

@Getter
public class UserException extends RuntimeException {

    private final String userMessage;

    private final String errorCode;

    public UserException(String userMessage, String errorCode) {
        super(userMessage);
        this.userMessage = userMessage;
        this.errorCode = errorCode;
    }

}