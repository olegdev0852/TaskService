package org.example.taskservice.api.dto;

import java.time.LocalDateTime;

public record TaskResponseDto( long id,

                               String name,

                               String description,

                               LocalDateTime timeOfCreation,

                               boolean completed ){}

