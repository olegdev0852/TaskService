package org.example.taskservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.taskservice.dto.TaskRequestDto;
import org.example.taskservice.dto.TaskResponseDto;
import org.example.taskservice.dto.mapping.TaskMapping;
import org.example.taskservice.entity.Task;
import org.example.taskservice.service.serviceImpl.TaskServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskServiceImpl taskService;

    private final TaskMapping taskMapping;

    @GetMapping
    public ResponseEntity<?> getAllTasks() {
        List<Task> tasks = taskService.getTasks();
        return ResponseEntity.ok().body(tasks);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<?> getTaskById(@PathVariable Long taskId) {
        taskService.getTaskById(taskId);
        return ResponseEntity.ok().body(taskId);

    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody @Valid TaskRequestDto taskRequestDto) {
        Task task = taskMapping.fromRequestDto(taskRequestDto);
        Task createdTask = taskService.createTask(task);
        TaskResponseDto taskResponse = taskMapping.toResponseDto(createdTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskResponse);

    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTaskById(taskId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(taskId);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<?> updateTask(
           @PathVariable Long taskId,
           @RequestParam(value = "name",required = false) String name,
           @RequestParam(value = "description", required = false) String description
            ){

        taskService.updateTask(taskId,name,description);
        return ResponseEntity.ok("Задача успешно обновлена");
    }



}
