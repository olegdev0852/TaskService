package org.example.taskservice.mapping;

import org.example.taskservice.api.dto.TaskRequestDto;
import org.example.taskservice.api.dto.TaskResponseDto;
import org.example.taskservice.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapping {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    Task fromRequestDto(TaskRequestDto requestDto);

    TaskResponseDto toResponseDto(Task task);

    List<TaskResponseDto> toResponseDto(Page<Task> tasks);
}