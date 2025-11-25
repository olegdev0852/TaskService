package org.example.taskservice.service;

import org.example.taskservice.dto.TaskRequestDto;
import org.example.taskservice.dto.TaskResponseDto;
import org.example.taskservice.dto.mapping.TaskMapping;
import org.example.taskservice.entity.Task;
import org.example.taskservice.exception.user.TaskNotFoundException;
import org.example.taskservice.repository.TaskRepository;
import org.example.taskservice.service.serviceImpl.TaskServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapping mapper;

    @InjectMocks
    private TaskServiceImpl taskService;

   /* @Test
    void getTasks_ShouldReturnListOfTasksDto_WhenTaskExist() {
        Task task1 = new Task("name1", "des1");
        Task task2 = new Task("name2", "des2");
        List<Task> tasks = List.of(task1, task2);
        TaskResponseDto taskResponseDto1 = new TaskResponseDto(1, "name1", "des1", LocalDateTime.now(), false);
        TaskResponseDto taskResponseDto2 = new TaskResponseDto(2, "name2", "des2", LocalDateTime.now(), false);
        List<TaskResponseDto> expected = List.of(taskResponseDto1, taskResponseDto2);

        Mockito.when(taskRepository.findAll()).thenReturn(tasks);
        Mockito.when(mapper.toResponseDto(tasks)).thenReturn(expected);

        List<TaskResponseDto> actual = taskService.getTasks();

        Assertions.assertEquals(expected, actual);
        verify(taskRepository, times(1)).findAll();
        verify(mapper, times(1)).toResponseDto(tasks);
    }
*/
    @Test
    void getTaskById_ShouldReturnTask_WhenTaskExist() {
        Long taskId = 1L;
        Task task1 = new Task(taskId, "name1", "des1", LocalDateTime.now(), false);
        TaskResponseDto expected = new TaskResponseDto(1, "name1", "des1", LocalDateTime.now(), false);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task1));
        when(mapper.toResponseDto(task1)).thenReturn(expected);

        TaskResponseDto actual = taskService.getTaskById(taskId);

        Assertions.assertEquals(expected, actual);
        verify(taskRepository, times(1)).findById(taskId);
        verify(mapper, times(1)).toResponseDto(task1);
    }

    @Test
    void getTaskById_ShouldThrowEx_WhenTaskNotFound() {
        Long taskId = 99L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        Assertions.assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(taskId));

        verify(taskRepository, times(1)).findById(taskId);
        verify(mapper, never()).toResponseDto(any(Task.class));
    }

    @Test
    void createTask_ShouldReturnTaskDto_WhenValidRequest() {
        TaskRequestDto taskReq = new TaskRequestDto("name1", "des1");
        Task task1 = new Task(1L, "name1", "des1", LocalDateTime.now(), false);
        TaskResponseDto expected = new TaskResponseDto(1, "name1", "des1", LocalDateTime.now(), false);
        when(mapper.fromRequestDto(taskReq)).thenReturn(task1);
        when(taskRepository.save(task1)).thenReturn(task1);
        when(mapper.toResponseDto(task1)).thenReturn(expected);

        TaskResponseDto actual = taskService.createTask(taskReq);

        Assertions.assertEquals(expected, actual);
        verify(mapper, times(1)).fromRequestDto(taskReq);
        verify(taskRepository, times(1)).save(task1);
        verify(mapper, times(1)).toResponseDto(task1);
    }

    @Test
    void createTask_ShouldThrowEx_WhenNotValidReq() {
        TaskRequestDto taskReq = new TaskRequestDto("", "des1");
        when(mapper.fromRequestDto(taskReq)).thenThrow(RuntimeException.class);

        Assertions.assertThrows(RuntimeException.class, () -> taskService.createTask(taskReq));
    }

    @Test
    void updateTask_ShouldUpdateTask_WhenValidReq() {
        Long taskId = 1L;
        Task existing = new Task(taskId, "nameOld", "desOld", LocalDateTime.now(), false);
        String upName = "nameUp";
        String upDes = "desUp";
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existing));
        when(taskRepository.save(existing)).thenReturn(existing);

        taskService.updateTask(taskId, upName, upDes);

        Assertions.assertEquals(upName, existing.getName());
        Assertions.assertEquals(upDes, existing.getDescription());
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(existing);
    }

    @Test
    void updateTask_ShouldThrowEx_WhenTaskNotFound() {
        Long taskId = 99L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        Assertions.assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(taskId, "newName", "newDes"));
    }

    @Test
    void deleteTask_ShouldDeleteTask_WhenTaskExist() {
        Long taskId = 1L;
        when(taskRepository.existsById(taskId)).thenReturn(true);

        taskService.deleteTaskById(taskId);

        verify(taskRepository, times(1)).existsById(taskId);
        verify(taskRepository, times(1)).deleteById(taskId);
    }

    @Test
    void deleteTask_ShouldThrowEx_WhenTaskNotExist() {
        Long taskId = 99L;
        when(taskRepository.existsById(taskId)).thenThrow(TaskNotFoundException.class);

        Assertions.assertThrows(TaskNotFoundException.class, () -> taskService.deleteTaskById(taskId));
        verify(taskRepository, times(1)).existsById(taskId);
        verify(taskRepository, never()).deleteById(any());
    }
}
