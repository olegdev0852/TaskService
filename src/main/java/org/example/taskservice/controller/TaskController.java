package org.example.taskservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.taskservice.dto.TaskRequestDto;
import org.example.taskservice.dto.TaskResponseDto;
import org.example.taskservice.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<?> getAllTasks() {
        List<TaskResponseDto> tasks = taskService.getTasks();
        return ResponseEntity.ok().body(tasks);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<?> getTaskById(@PathVariable Long taskId) {
        taskService.getTaskById(taskId);
        return ResponseEntity.ok().body(taskId);
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody @Valid TaskRequestDto taskRequestDto) {
        taskService.createTask(taskRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskRequestDto);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTaskById(taskId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(taskId);
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
