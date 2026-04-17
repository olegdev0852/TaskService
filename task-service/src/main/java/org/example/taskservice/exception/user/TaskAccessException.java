package org.example.taskservice.exception.user;

import org.springframework.http.HttpStatus;

public class TaskAccessException extends UserException {
    public TaskAccessException() {
        super("Доступ к задаче запрещен, вы не являетесь автором", HttpStatus.NOT_FOUND);
    }
}
