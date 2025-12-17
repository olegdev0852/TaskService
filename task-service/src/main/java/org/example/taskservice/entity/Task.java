package org.example.taskservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @SequenceGenerator(
            name = "sequence_task",
            sequenceName = "sequence_task",
            allocationSize = 50
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "sequence_task"
    )
    private Long id;

    @NotBlank
    @Size(max = 70)
    @Column(nullable = false, length = 70)
    private String name;

    private String description;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime timeOfCreation;

    private boolean completed = false;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.completed = false;
    }
}
