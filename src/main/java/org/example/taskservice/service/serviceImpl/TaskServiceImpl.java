package org.example.taskservice.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskservice.dto.TaskRequestDto;
import org.example.taskservice.dto.TaskResponseDto;
import org.example.taskservice.dto.mapping.TaskMapping;
import org.example.taskservice.entity.Task;
import org.example.taskservice.exception.support.SupportException;
import org.example.taskservice.exception.user.TaskNotFoundException;
import org.example.taskservice.repository.TaskRepository;
import org.example.taskservice.service.TaskService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    @Value("${tasks.scheduled.cleanup-tasks-days-threshold}")
    private int daysThreshold;

    private final TaskRepository taskRepository;

    private final TaskMapping mapper;

    @Transactional(readOnly = true)
    @Override
    public Page<TaskResponseDto> getTasks(Pageable pageable) {
        try {

            Page<Task> tasks = taskRepository.findAll(pageable);

            return tasks.map(mapper::toResponseDto);

        } catch (DataAccessException dae) {
            throw new SupportException(
                    "Ошибка при получении списка задач",
                    "Ошибка при вызове taskRepository.findAll()",
                    dae
            );
        }
    }


    /*@Transactional(readOnly = true)
    @Override
    public List<TaskResponseDto> getTasks() {
        try {
            List<Task> tasks = taskRepository.findAll();
            return mapper.toResponseDto(tasks);
        } catch (DataAccessException dae) {
            throw new SupportException(
                    "Ошибка при получении списка задач",
                    "Ошибка при вызове taskRepository.findAll()",
                    dae
            );
        }
    }*/


    @Override
    public TaskResponseDto getTaskById(Long id) {
        try {
            Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
            return mapper.toResponseDto(task);
        } catch (DataAccessException dae) {
            throw new SupportException(
                    "Ошибка при получении задачи по id",
                    "findById(id= %s) failed".formatted(id),
                    dae
            );
        }
    }

    @Transactional
    @Override
    public TaskResponseDto createTask(TaskRequestDto taskReq) {
        try {

            Task task = mapper.fromRequestDto(taskReq);
            Task savedTask = taskRepository.save(task);
            return mapper.toResponseDto(savedTask);
        } catch (DataAccessException dae) {
            throw new SupportException(
                    "Ошибка при создании задачи",
                    "save(task = %s) failed".formatted(taskReq),
                    dae
            );
        }
    }

    @Override
    @Transactional
    public void deleteTaskById(Long id) {
        try {
            if (!taskRepository.existsById(id)) {
                throw new TaskNotFoundException(id);
            }
            taskRepository.deleteById(id);
        } catch (DataAccessException dae) {
            throw new SupportException(
                    "Ошибка при удалении задачи",
                    "deleteById(id= %s ) failed".formatted(id),
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
                    "update for id= %s, name= %s, description= %s".formatted(id, name, description),
                    dae
            );
        }
    }

    @Transactional
    @Scheduled(cron = "${tasks.scheduled.cleanup-tasks-cron}")
    public void cleanupOldTasks() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysThreshold);
        taskRepository.deleteByTimeOfCreationBefore(cutoffDate);
    }

}

