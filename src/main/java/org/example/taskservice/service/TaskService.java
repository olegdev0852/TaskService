package org.example.taskservice.service;

import org.example.taskservice.dto.TaskRequestDto;
import org.example.taskservice.dto.TaskResponseDto;
import org.example.taskservice.entity.Task;

import java.util.List;


public interface TaskService {

    List<TaskResponseDto> getTasks();

    TaskResponseDto getTaskById(Long id);

    TaskResponseDto createTask(TaskRequestDto task);

    void deleteTaskById(Long id);

    void updateTask(Long id, String name, String description);
}
