package org.example.taskservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.jwtstarter.model.ParsedJwt;
import org.example.taskservice.api.controller.TaskServiceApi;
import org.example.taskservice.api.dto.AssignTaskRequest;
import org.example.taskservice.api.dto.PagedResponse;
import org.example.taskservice.api.dto.TaskRequestDto;
import org.example.taskservice.api.dto.TaskResponseDto;
import org.example.taskservice.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TaskController implements TaskServiceApi {

    private final TaskService taskService;

    @Override
    public PagedResponse<TaskResponseDto> getAllTasks(int page, int size, String sort, ParsedJwt jwt) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TaskResponseDto> pageResult = taskService.getTasks(pageable, jwt);

        return new PagedResponse<>(
                pageResult.getContent(),
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }

    @Override
    public TaskResponseDto getTaskById(Long taskId, ParsedJwt jwt) {
        return taskService.getTaskById(taskId, jwt);
    }

    @Override
    public TaskResponseDto createTask(TaskRequestDto taskRequestDto, ParsedJwt jwt) {
        return taskService.createTask(taskRequestDto, jwt);
    }

    @Override
    public TaskResponseDto assignTask(Long taskId, AssignTaskRequest request) {
        return taskService.assignTask(taskId, request);
    }

    @Override
    public TaskResponseDto approveTask(Long taskId) {
        return taskService.approveTask(taskId);
    }

    @Override
    public TaskResponseDto updateTask(Long taskId, TaskRequestDto taskRequestDto, ParsedJwt jwt) {
        return taskService.updateTask(taskId, taskRequestDto,jwt);
    }

    @Override
    public void deleteTask(Long taskId, ParsedJwt jwt) {
        taskService.deleteTaskById(taskId,jwt);
    }

}

