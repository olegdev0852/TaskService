package org.example.taskservice.service;

import org.example.taskservice.entity.Task;

import java.util.List;


public interface TaskService {

    List<Task> getTasks();

    Task getTaskById(Long id);

    Task createTask(Task task);

    void deleteTaskById(Long id);

    void updateTask(Long id, String name, String description);
}
