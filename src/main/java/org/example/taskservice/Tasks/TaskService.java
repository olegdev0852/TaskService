package org.example.taskservice.Tasks;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {
    @Value("${tasks.clean.days}")
    private int daysThreshold;

    private final TaskRepository taskRepository;

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    @Transactional// она вроде нужна, но не понимаю зачем
    public Task createTask(Task task) {
       /* if (task.getId() != null) {
            throw new BadRequestException("id при создании задачи передовать нельзя");
        }*/
        return taskRepository.save(task);
    }


    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    @Transactional
    public void updateTask(Long taskId, String name, String description) {

        var Task = taskRepository.findById(taskId).
                // поменять на BadRequestException??
                        orElseThrow(() -> new IllegalArgumentException("Задача с id=%s не найдена".formatted(taskId)));

        if (name != null && !name.equals(Task.getName())) {
            Task.setName(name);
        }
        if (description != null && !description.equals(Task.getDescription())) {
            Task.setDescription(description);
        }
    }


    @Transactional
    @Scheduled(cron = "0 0 10 * * ?")
    //0    0    10    *    *    ?
    //┃    ┃    ┃     ┃    ┃    ┃
    //┃    ┃    ┃     ┃    ┃    ┗ День недели (any)
    //┃    ┃    ┃     ┃    ┗━━━━━━ Месяц (every month)
    //┃    ┃    ┃     ┗━━━━━━━━━━━ День месяца (every day)
    //┃    ┃    ┗━━━━━━━━━━━━━━━━━ Часы (10)
    //┃    ┗━━━━━━━━━━━━━━━━━━━━━━ Минуты (0)
    //┗━━━━━━━━━━━━━━━━━━━━━━━━━━━ Секунды (0)
    public void cleanupOldTasks() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysThreshold);
        taskRepository.deleteByTimeOfCreationBefore(cutoffDate);
    }

}

