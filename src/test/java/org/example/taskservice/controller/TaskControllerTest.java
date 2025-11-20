package org.example.taskservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.taskservice.dto.TaskRequestDto;
import org.example.taskservice.entity.Task;
import org.example.taskservice.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TaskRepository taskRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:13-alpine");

    @DynamicPropertySource
    static void postgres(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");

    }

    @BeforeEach
    void cleanDb() {
        taskRepository.deleteAll();
    }

    @Test
    void getAllTasks() throws Exception {
        taskRepository.save(new Task("Test1", "Desc1"));
        taskRepository.save(new Task("Test2", "Desc2"));

        mvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Test1"))
                .andExpect(jsonPath("$[1].name").value("Test2"))
                .andExpect(jsonPath("$[0].description").value("Desc1"))
                .andExpect(jsonPath("$[1].description").value("Desc2"));
    }

    @Test
    @SneakyThrows
    void getTaskById() {

        Task task = new Task("Test1", "Desc1");
        Task savedTask = taskRepository.save(task);

        //проблема в том что мой метод getTaskById в контроллере
        // возвращает ID по которому обратились и эти проверки не проходят
        //как я понимаю нужно изменить метод контролера чтоб он возвращал ДТО
        mvc.perform(get("/api/tasks/" + savedTask.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test1"))
                .andExpect(jsonPath("$.description").value("Desc1"));
    }

    @Test
    void createTask_SimpleTest() throws Exception {
        String json = "{\"name\":\"Test\",\"description\":\"Desc\"}";

        mvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void createTask() throws Exception {

        TaskRequestDto requestDto = new TaskRequestDto(
                "Test Task",
                "Task description"

        );

        mvc.perform(
                        post("/api/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(requestDto.name()))
                .andExpect(jsonPath("$.description").value(requestDto.description()));
    }

    @Test
    void deleteTask() throws Exception {
        Task task = new Task("Test1", "Desc1");
        Task savedTask = taskRepository.save(task);

        mvc.perform(delete("/api/tasks/" + savedTask.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateTask() throws Exception {
        Task task = new Task("Test1", "Desc1");
        Task savedTask = taskRepository.save(task);

        TaskRequestDto requestDto = new TaskRequestDto(
                "Test Task",
                "Task description"
        );

        mvc.perform(
                        put("/api/tasks/" + savedTask.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                )
                //проверяю только статус так как контроллер возвращает только его
                //не совсем нравится такое, мне кажется нужно изменить метод в контроллере
                .andExpect(status().isOk());
    }
}