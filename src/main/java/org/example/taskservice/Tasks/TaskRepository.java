package org.example.taskservice.Tasks;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface TaskRepository
        extends JpaRepository<Task, Long> {

    void deleteByTimeOfCreationBefore(LocalDateTime cutoff);


}
