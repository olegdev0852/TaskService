package org.example.taskservice.service.serviceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jwtstarter.model.ParsedJwt;
import org.example.taskservice.api.dto.AssignTaskRequest;
import org.example.taskservice.api.dto.TaskRequestDto;
import org.example.taskservice.api.dto.TaskResponseDto;
import org.example.taskservice.api.state.TaskState;
import org.example.taskservice.entity.Task;
import org.example.taskservice.event.TaskApprovedEvent;
import org.example.taskservice.event.TaskAssignedEvent;
import org.example.taskservice.event.TaskCreatedEvent;
import org.example.taskservice.exception.support.SupportException;
import org.example.taskservice.exception.user.BadRequestException;
import org.example.taskservice.exception.user.TaskAccessException;
import org.example.taskservice.exception.user.TaskNotFoundException;
import org.example.taskservice.mapping.TaskMapping;
import org.example.taskservice.repository.TaskRepository;
import org.example.taskservice.service.TaskService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

   private final ApplicationEventPublisher eventPublisher;
    @Value("${tasks.scheduled.cleanup-tasks-days-threshold}")
    private int daysThreshold;

    private final TaskRepository taskRepository;

    private final TaskMapping mapper;

    @Transactional(readOnly = true)
    @Override
    public Page<TaskResponseDto> getTasks(Pageable pageable, ParsedJwt jwt) {
        try {
            Page<Task> tasks = taskRepository.findByAuthorId(jwt.getUserId(), pageable);
            return tasks.map(mapper::toResponseDto);
        } catch (DataAccessException dae) {
            throw new SupportException(
                    "Ошибка при получении списка задач",
                    "Ошибка при вызове taskRepository.findAll()",
                    dae
            );
        }
    }

    @Override
    public TaskResponseDto getTaskById(Long id, ParsedJwt jwt) {
        try {
            Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
            if (!Objects.equals(task.getAuthorId(), jwt.getUserId())) {
                throw new TaskAccessException();
            }
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
    public TaskResponseDto createTask(@Valid TaskRequestDto taskReq, ParsedJwt jwt) {
        try {
            Task task = mapper.fromRequestDto(taskReq);

            boolean hasAssigned = task.getAssignedTo() != null;
            boolean hasDeadline = task.getDeadline() != null;
            if (hasAssigned != hasDeadline) {
                throw new BadRequestException("Исполнитель и дедлайн назначаются одновременно");
            }

            task.setState(TaskState.CREATED);
            task.setAuthorId(jwt.getUserId());
            Task savedTask = taskRepository.save(task);

            eventPublisher.publishEvent(new TaskCreatedEvent(task));
            return mapper.toResponseDto(savedTask);
        } catch (DataAccessException dae) {
            throw new SupportException(
                    "Ошибка при создании задачи",
                    "save(task = %s) failed".formatted(taskReq),
                    dae
            );
        }
    }

    @Transactional
    @Override
    public TaskResponseDto assignTask(Long taskId, AssignTaskRequest request) {
        try {
            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new TaskNotFoundException(taskId));

            if ((request.assignedTo() == null) != (request.deadline() == null)) {
                throw new BadRequestException(
                        "Назначение исполнителя и дедлайн обязательны одновременно"
                );
            }

            task.setAssignedTo(request.assignedTo());
            task.setDeadline(request.deadline());
            Task saved = taskRepository.save(task);

            eventPublisher.publishEvent(new TaskAssignedEvent(task));
            return mapper.toResponseDto(saved);
        } catch (DataAccessException dae) {
            throw new SupportException(
                    "Ошибка при назначении задачи",
                    "save(task = %s) failed".formatted(taskId),
                    dae
            );
        }
    }

    @Transactional
    @Override
    public TaskResponseDto approveTask(Long taskId) {
        try {
            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new TaskNotFoundException(taskId));

            eventPublisher.publishEvent(new TaskApprovedEvent(task));
            return mapper.toResponseDto(task);
        }catch (DataAccessException dae) {
            throw new SupportException(
                    "Ошибка при апруве задачи",
                    "save(task = %s) failed".formatted(taskId),
                    dae
            );
        }
    }

    @Override
    @Transactional
    public void deleteTaskById(Long id, ParsedJwt jwt) {
        try {
            Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
            if (!Objects.equals(task.getAuthorId(), jwt.getUserId())) {
                throw new TaskAccessException();
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
    public TaskResponseDto updateTask(Long id, TaskRequestDto request, ParsedJwt jwt) {

        Task existing = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        if (!Objects.equals(existing.getAuthorId(), jwt.getUserId())) {
            throw new TaskAccessException();
        }

        boolean changed = false;

        if (request.name() != null && !request.name().equals(existing.getName())) {
            existing.setName(request.name());
            changed = true;
        }
        if (request.description() != null && !request.description().equals(existing.getDescription())) {
            existing.setDescription(request.description());
            changed = true;
        }
        if (changed) {
            log.info("Updating task {}: name={}, description={}", id, existing.getName(), existing.getDescription());
            try {
                taskRepository.save(existing);
            } catch (DataAccessException dae) {
                throw new SupportException(
                        "Ошибка при обновлении задачи",
                        "update for id= %s, name= %s, description= %s".formatted(id, request.name(), request.description()),
                        dae
                );
            }
        }

        return mapper.toResponseDto(existing);
    }

    @Transactional
    @Scheduled(cron = "${tasks.scheduled.cleanup-tasks-cron}")
    public void cleanupOldTasks() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysThreshold);
        taskRepository.deleteByCreatedAtBefore(cutoffDate);
    }

}

