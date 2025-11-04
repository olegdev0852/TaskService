package org.example.taskservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequestDto {
    @NotBlank
    @Size(max = 70)
    String name;
    @NotBlank
    String description;
}
