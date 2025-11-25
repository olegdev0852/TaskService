package org.example.taskservice.dto.mapping;

import org.example.taskservice.dto.TaskRequestDto;
import org.example.taskservice.dto.TaskResponseDto;
import org.example.taskservice.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapping {


    Task fromRequestDto(TaskRequestDto requestDto);

    TaskResponseDto toResponseDto(Task task);

     List<TaskResponseDto> toResponseDto( Page<Task> tasks);
}