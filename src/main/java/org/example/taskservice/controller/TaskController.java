package org.example.taskservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.taskservice.dto.TaskRequestDto;
import org.example.taskservice.service.TaskService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<?> getAllTasks(
            @PageableDefault(sort = "timeOfCreation", direction = Sort.Direction.DESC)
            Pageable pageable) {
        var response = taskService.getTasks(pageable);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<?> getTaskById(@PathVariable Long taskId) {
        var response = taskService.getTaskById(taskId);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody @Valid TaskRequestDto taskRequestDto) {
        var response = taskService.createTask(taskRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTaskById(taskId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<?> updateTask(
            @PathVariable Long taskId,
            @RequestBody TaskRequestDto taskRequest
    ) {
        taskService.updateTask(taskId, taskRequest.name(), taskRequest.description());
        return ResponseEntity.ok("Задача успешно обновлена");
    }
}
