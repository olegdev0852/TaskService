package org.example.taskservice.dto.mapping;

import org.example.taskservice.api.dto.TaskRequestDto;
import org.example.taskservice.api.dto.TaskResponseDto;
import org.example.taskservice.entity.Task;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapping {


    Task fromRequestDto(TaskRequestDto requestDto);

    TaskResponseDto toResponseDto(Task task);

     List<TaskResponseDto> toResponseDto( Page<Task> tasks);
}