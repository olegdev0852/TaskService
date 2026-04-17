package org.example.taskservice.service;

import org.example.jwtstarter.model.ParsedJwt;
import org.example.taskservice.api.dto.TaskRequestDto;
import org.example.taskservice.api.dto.TaskResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface TaskService {

    Page<TaskResponseDto> getTasks(Pageable pageable, ParsedJwt jwt);

    TaskResponseDto getTaskById(Long id, ParsedJwt jwt);

    TaskResponseDto createTask(TaskRequestDto task, ParsedJwt jwt);

    void deleteTaskById(Long id,ParsedJwt jwt);

    TaskResponseDto updateTask(Long id, TaskRequestDto taskRequestDto, ParsedJwt jwt);
}
