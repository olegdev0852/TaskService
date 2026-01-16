package org.example.taskservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.taskservice.api.controller.TaskServiceApi;
import org.example.taskservice.api.dto.PagedResponse;
import org.example.taskservice.api.dto.TaskRequestDto;
import org.example.taskservice.api.dto.TaskResponseDto;
import org.example.taskservice.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TaskController implements TaskServiceApi {

    private final TaskService taskService; // твоя бизнес-логика возвращает Page<TaskResponseDto> или Page<TaskEntity>

    @Override
    public PagedResponse<TaskResponseDto> getAllTasks(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TaskResponseDto> pageResult = taskService.getTasks(pageable);

        return new PagedResponse<>(
                pageResult.getContent(),
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }

    @Override
    public TaskResponseDto getTaskById(Long taskId) {
        return taskService.getTaskById(taskId);
    }

    @Override
    public TaskResponseDto createTask(TaskRequestDto taskRequestDto) {
        return taskService.createTask(taskRequestDto);
    }

    @Override
    public TaskResponseDto updateTask(Long taskId, TaskRequestDto taskRequestDto) {
        return taskService.updateTask(taskId, taskRequestDto);
    }

    @Override
    public void deleteTask(Long taskId) {
        taskService.deleteTaskById(taskId);
    }

}

