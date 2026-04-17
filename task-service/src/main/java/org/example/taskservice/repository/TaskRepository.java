package org.example.taskservice.repository;

import org.example.taskservice.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByAuthorId(UUID id, Pageable pageable);

    void deleteByCreatedAtBefore(LocalDateTime createdAtBefore);

}
