package org.example.taskservice.exception.user;

public class ValidationException extends UserException {
    public ValidationException() {
        super("Введены некорректные данные","NOT_VALID" );
    }
}
