package org.example.taskservice.repository;

import org.example.taskservice.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;

public interface TaskRepository extends JpaRepository<Task, Long> {

    void deleteByCreatedAtBefore(LocalDateTime createdAtBefore);

}
