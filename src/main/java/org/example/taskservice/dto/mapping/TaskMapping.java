package org.example.taskservice.dto.mapping;

import org.example.taskservice.dto.TaskRequestDto;
import org.example.taskservice.dto.TaskResponseDto;
import org.example.taskservice.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * или мб сделать отдельный маппер для запроса/ответа??
 * посчитал это здесь избыточным, но а в более крупных сервисах как было бы?
 */
@Mapper(componentModel = "spring")
public interface TaskMapping {
    @Mapping(target = "id",ignore = true)
    @Mapping(target = "timeOfCreation", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "completed", constant = "false")
    Task fromRequestDto(TaskRequestDto requestDto);

    TaskResponseDto toResponseDto(Task task);
}