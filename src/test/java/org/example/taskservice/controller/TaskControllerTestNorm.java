package org.example.taskservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.taskservice.dto.TaskRequestDto;
import org.example.taskservice.dto.TaskResponseDto;
import org.example.taskservice.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTestNorm {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    TaskService taskService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        TaskService taskService() {
            return Mockito.mock(TaskService.class);
        }
    }

   /* @Test
    void getAllTasks_shouldReturnTasks() throws Exception {
        List<TaskResponseDto> mockList = List.of(
                new TaskResponseDto(
                        1L, "Task1", "Desc1", LocalDateTime.now(), false),
                new TaskResponseDto(
                        2L, "Task2", "Desc2", LocalDateTime.now(), false)
        );

        Mockito.when(taskService.getTasks()).thenReturn(mockList);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }*/

    @Test
    void getTaskById_shouldReturnTask() throws Exception {
        TaskResponseDto dto = new TaskResponseDto(
                1L, "Task1", "Desc1", LocalDateTime.now(), false);

        Mockito.when(taskService.getTaskById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createTask_shouldReturnCreated() throws Exception {
        TaskRequestDto req = new TaskRequestDto("New Task", "New desc");
        TaskResponseDto res = new TaskResponseDto(
                10L, "New Task", "New desc", LocalDateTime.now(), false);

        Mockito.when(taskService.createTask(any(TaskRequestDto.class))).thenReturn(res);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void deleteTask_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/tasks/5"))
                .andExpect(status().isNoContent());

        Mockito.verify(taskService).deleteTaskById(5L);
    }

    @Test
    void updateTask_shouldReturnOk() throws Exception {
        TaskRequestDto req = new TaskRequestDto("NewName", "NewDescr");

        mockMvc.perform(put("/api/tasks/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string("Задача успешно обновлена"));

        Mockito.verify(taskService)
                .updateTask(eq(3L), eq("NewName"), eq("NewDescr"));
    }
}
