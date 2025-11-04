package org.example.taskservice.service.serviceImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.taskservice.entity.Task;
import org.example.taskservice.exception.GlobalExceptionHandler;
import org.example.taskservice.exception.support.SupportException;
import org.example.taskservice.exception.user.TaskNotFoundException;
import org.example.taskservice.repository.TaskRepository;
import org.example.taskservice.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    @Value("${tasks.clean.days}")
    private int daysThreshold;

    private final TaskRepository taskRepository;

    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public List<Task> getTasks() {
        try {
            return taskRepository.findAll();
        } catch (DataAccessException dae) {
            throw new SupportException(
                    "Ошибка при получении списка задач",
                    "Ошибка при вызове taskRepository.findAll()",
                    dae
            );
        }
    }

    @Override
    public Task getTaskById(Long id) {
        try {
            return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        } catch (DataAccessException dae) {
            throw new SupportException(
                    "Ошибка при получении задачи по id",
                    "findById(id=" + id + ") failed",
                    dae
            );
        }
    }

    @Transactional
    @Override
    public Task createTask(Task task) {
        try {
            return taskRepository.save(task);
            //здесь нужно реализовать обработку ошибок

        } catch (DataAccessException dae) {
            throw new SupportException(
                    "Ошибка при создании задачи",
                    "save(task = " + task + ") failed",
                    dae
            );
        }
    }

    @Override
    public void deleteTaskById(Long id) {
        try {
            if (!taskRepository.existsById(id)) {
                throw new TaskNotFoundException(id);
            }
            taskRepository.deleteById(id);
        } catch (DataAccessException dae) {
            throw new SupportException(
                    "Ошибка при удалении задачи",
                    "deleteById(id=" + ") failed",
                    dae
            );

        }
    }

    @Transactional
    @Override
    public void updateTask(Long id, String name, String description) {
        try {
            Task existing = taskRepository.findById(id).
                    orElseThrow(() -> new TaskNotFoundException(id));

            boolean changed = false;

            if (name != null && !name.equals(existing.getName())) {
                existing.setName(name);
                changed = true;
            }
            if (description != null && !description.equals(existing.getDescription())) {
                existing.setDescription(description);
                changed = true;
            }
            if (changed) {
                log.info("Updating task {}: name={}, description={}", id, existing.getName(), existing.getDescription());
                taskRepository.save(existing);
            }
        } catch (DataAccessException dae) {
            throw new SupportException(
                    "Ошибка при обновлении задачи",
                    "update for id=" + id + ", name=" + name + ", description=" + description,
                    dae
            );
        }


    }

    @Transactional
    @Scheduled(cron = "0 0 10 * * ?")//вынести в конст
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

