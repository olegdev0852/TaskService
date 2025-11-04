package org.example.taskservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponseDto {
    Long id;

    String name;

    String description;

    LocalDateTime timeOfCreation;
    boolean completed;

}
