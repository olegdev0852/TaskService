package org.example.taskservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.taskservice.api.dto.TaskRequestDto;
import org.example.taskservice.api.dto.TaskResponseDto;
import org.example.taskservice.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    TaskService taskService;

    @Test
    void getAllTasks_shouldReturnTasks() throws Exception {
        List<TaskResponseDto> mockList = List.of(
                new TaskResponseDto(
                        1L, "Task1", "Desc1", LocalDateTime.now(), false),
                new TaskResponseDto(
                        2L, "Task2", "Desc2", LocalDateTime.now(), false)
        );
        Pageable expectedPageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "timeOfCreation"));
        Page<TaskResponseDto> mockPage = new PageImpl<>(mockList, expectedPageable, mockList.size());

        Mockito.when(taskService.getTasks(Mockito.any(Pageable.class))).thenReturn(mockPage);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Task1"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].name").value("Task2"));
    }

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
        TaskResponseDto resp = new TaskResponseDto(3L, "NewName", "NewDescr", LocalDateTime.now(), false);

        Mockito.when(taskService.updateTask(eq(3L), any()))
                .thenReturn(resp);

        mockMvc.perform(put("/api/tasks/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(resp)));

        Mockito.verify(taskService)
                .updateTask(eq(3L), any());
    }
}
