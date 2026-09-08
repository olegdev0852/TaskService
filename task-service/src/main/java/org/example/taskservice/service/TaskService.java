package org.example.taskservice.service;

import org.example.jwtstarter.model.ParsedJwt;
import org.example.taskservice.api.dto.AssignTaskRequest;
import org.example.taskservice.api.dto.TaskRequestDto;
import org.example.taskservice.api.dto.TaskResponseDto;
import org.example.taskservice.api.state.TaskState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface TaskService {

    Page<TaskResponseDto> getTasks(Pageable pageable, ParsedJwt jwt);

    TaskResponseDto getTaskById(Long id, ParsedJwt jwt);

    TaskResponseDto createTask(TaskRequestDto task, ParsedJwt jwt);

    TaskResponseDto assignTask(Long id, AssignTaskRequest assignTask);

    TaskResponseDto approveTask(Long id);

    void deleteTaskById(Long id,ParsedJwt jwt);

    TaskResponseDto updateTask(Long id, TaskRequestDto taskRequestDto, ParsedJwt jwt);

    void updateTaskState(Long id, TaskState newState);
}
