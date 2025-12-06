package org.example.taskservice.repository;

import jakarta.validation.constraints.NotNull;
import org.example.taskservice.dto.TaskResponseDto;
import org.example.taskservice.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository
        extends JpaRepository<Task, Long> {

   // Page<Task> findAll(Pageable pageable);

    void deleteByTimeOfCreationBefore(LocalDateTime cutoff);


}
