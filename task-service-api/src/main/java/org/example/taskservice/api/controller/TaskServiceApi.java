package org.example.taskservice.api.controller;

import jakarta.validation.Valid;
import org.example.jwtstarter.model.ParsedJwt;
import org.example.taskservice.api.dto.PagedResponse;
import org.example.taskservice.api.dto.TaskRequestDto;
import org.example.taskservice.api.dto.TaskResponseDto;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/tasks")
public interface TaskServiceApi {

    @GetMapping
    PagedResponse<TaskResponseDto> getAllTasks(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sort", required = false) String sort,
            ParsedJwt jwt
    );

    @GetMapping("/task/{id}")
    TaskResponseDto getTaskById(@PathVariable("id") Long taskId, ParsedJwt jwt);

    @PostMapping
    @ResponseStatus(code = org.springframework.http.HttpStatus.CREATED)
    TaskResponseDto createTask(@Valid @RequestBody TaskRequestDto taskRequestDto, ParsedJwt jwt);

    @PutMapping("/{id}")
    TaskResponseDto updateTask(@PathVariable("id") Long taskId,
                               @Valid @RequestBody TaskRequestDto taskRequestDto, ParsedJwt jwt);

    @DeleteMapping("/{taskId}")
    @ResponseStatus(code = org.springframework.http.HttpStatus.NO_CONTENT)
    void deleteTask(@PathVariable("taskId") Long taskId, ParsedJwt jwt);
}
