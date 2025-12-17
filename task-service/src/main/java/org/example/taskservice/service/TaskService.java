package org.example.taskservice.service;

import org.example.taskservice.api.dto.TaskRequestDto;
import org.example.taskservice.api.dto.TaskResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface TaskService {

    Page<TaskResponseDto> getTasks(Pageable pageable);

    TaskResponseDto getTaskById(Long id);

    TaskResponseDto createTask(TaskRequestDto task);

    void deleteTaskById(Long id);

    TaskResponseDto updateTask(Long id, TaskRequestDto taskRequestDto);
}
