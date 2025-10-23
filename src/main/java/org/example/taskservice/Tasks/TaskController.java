package org.example.taskservice.Tasks;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
        List<Task> tasks = taskService.findAll();

        if (tasks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Задачи не найдены,обновите страницу");
        }

        return ResponseEntity.ok().body(tasks);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<?> getTaskById(@PathVariable Long taskId) {
        return taskService.findById(taskId)
                //юзается кастинг
                .map(task -> ResponseEntity.ok().body((Object) task))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Задача не найдена, перезапустите страницу"));
    }

    @PostMapping
    public ResponseEntity<?> createTask(@Valid @RequestBody Task task) {
        Task createdTask = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);

    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        //нужно ли здесь такое body
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
