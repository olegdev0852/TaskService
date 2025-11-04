package org.example.taskservice.exception.user;

public class TaskNotFoundException extends UserException {

    public TaskNotFoundException(Long id) {
        super("Задача с "+ id + " id Не найдена", "TASK_NOT_FOUND");
    }

}
