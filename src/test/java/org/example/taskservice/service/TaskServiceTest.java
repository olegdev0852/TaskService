package org.example.taskservice.service;

import org.example.taskservice.dto.TaskRequestDto;
import org.example.taskservice.dto.TaskResponseDto;
import org.example.taskservice.entity.Task;
import org.example.taskservice.exception.user.TaskNotFoundException;
import org.example.taskservice.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
class TaskServiceTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:13-alpine"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.show-sql", () -> "true");
    }

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    void getTasks_WhenNoTasks_ReturnsEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<TaskResponseDto> result = taskService.getTasks(pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getTasks_WhenTasksExist_ReturnsAllTasks() {
        Pageable pageable = PageRequest.of(0, 10);

        TaskRequestDto task1 = new TaskRequestDto("Task 1", "Description 1");
        TaskRequestDto task2 = new TaskRequestDto("Task 2", "Description 2");

        taskService.createTask(task1);
        taskService.createTask(task2);

        Page<TaskResponseDto> result = taskService.getTasks(pageable);

        assertNotNull(result);
        assertEquals(result.getTotalElements(), 2);

        List<String> createdTasksNames = result.getContent().stream()
                .map(TaskResponseDto::name)
                .toList();

        assertTrue(createdTasksNames.contains("Task 1"));
        assertTrue(createdTasksNames.contains("Task 2"));
    }

    @Test
    void getTaskById_WhenTaskExists_ReturnsTask() {
        TaskRequestDto taskRequest = new TaskRequestDto("Test Task", "Test Description");
        TaskResponseDto createdTask = taskService.createTask(taskRequest);

        TaskResponseDto result = taskService.getTaskById(createdTask.id());

        assertNotNull(result);
        assertEquals(createdTask.id(), result.id());
        assertEquals("Test Task", result.name());
        assertEquals("Test Description", result.description());
    }

    @Test
    void getTaskById_WhenTaskNotExists_ThrowsTaskNotFoundException() {
        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(999L));
    }

    @Test
    void createTask_WithValidData_CreatesTaskSuccessfully() {
        TaskRequestDto taskRequest = new TaskRequestDto("New Task", "New Description");

        TaskResponseDto result = taskService.createTask(taskRequest);

        assertNotNull(result);
        assertEquals("New Task", result.name());
        assertEquals("New Description", result.description());

        Task savedTask = taskRepository.findById(result.id()).orElseThrow();
        assertEquals("New Task", savedTask.getName());
        assertEquals("New Description", savedTask.getDescription());
    }

    @Test
    void deleteTaskById_WhenTaskExists_DeletesTask() {
        TaskRequestDto taskRequest = new TaskRequestDto("Task to delete", "Description");
        TaskResponseDto createdTask = taskService.createTask(taskRequest);

        taskService.deleteTaskById(createdTask.id());

        assertFalse(taskRepository.existsById(createdTask.id()));
    }

    @Test
    void deleteTaskById_WhenTaskNotExists_ThrowsTaskNotFoundException() {
        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTaskById(999L));
    }

    @Test
    void updateTask_WithNameAndDescription_UpdatesTask() {
        TaskRequestDto taskRequest = new TaskRequestDto("Original Name", "Original Description");
        TaskResponseDto createdTask = taskService.createTask(taskRequest);

        taskService.updateTask(createdTask.id(), "Updated Name", "Updated Description");

        TaskResponseDto updatedTask = taskService.getTaskById(createdTask.id());
        assertEquals("Updated Name", updatedTask.name());
        assertEquals("Updated Description", updatedTask.description());
    }

    @Test
    void updateTask_WithOnlyName_UpdatesOnlyName() {
        TaskRequestDto taskRequest = new TaskRequestDto("Original Name", "Original Description");
        TaskResponseDto createdTask = taskService.createTask(taskRequest);

        taskService.updateTask(createdTask.id(), "Updated Name", null);

        TaskResponseDto updatedTask = taskService.getTaskById(createdTask.id());
        assertEquals("Updated Name", updatedTask.name());
        assertEquals("Original Description", updatedTask.description());
    }

    @Test
    void updateTask_WithOnlyDescription_UpdatesOnlyDescription() {
        TaskRequestDto taskRequest = new TaskRequestDto("Original Name", "Original Description");
        TaskResponseDto createdTask = taskService.createTask(taskRequest);

        taskService.updateTask(createdTask.id(), null, "Updated Description");

        TaskResponseDto updatedTask = taskService.getTaskById(createdTask.id());
        assertEquals("Original Name", updatedTask.name());
        assertEquals("Updated Description", updatedTask.description());
    }

    @Test
    void updateTask_WithSameData_DoesNotUpdate() {
        TaskRequestDto taskRequest = new TaskRequestDto("Original Name", "Original Description");
        TaskResponseDto createdTask = taskService.createTask(taskRequest);

        taskService.updateTask(createdTask.id(), "Original Name", "Original Description");

        TaskResponseDto task = taskService.getTaskById(createdTask.id());
        assertEquals("Original Name", task.name());
        assertEquals("Original Description", task.description());
    }

    @Test
    void updateTask_WhenTaskNotExists_ThrowsTaskNotFoundException() {
        assertThrows(TaskNotFoundException.class,
                () -> taskService.updateTask(999L, "New Name", "New Description"));
    }

}