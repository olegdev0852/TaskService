package org.example.taskservice.exception.user;

import org.springframework.http.HttpStatus;

public class TaskNotFoundException extends UserException {

    public TaskNotFoundException(Long id) {
        super("Задача с "+ id + " id Не найдена", HttpStatus.NOT_FOUND);
    }

}
