package org.example.taskservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @SequenceGenerator(
            name = "sequence_task",
            sequenceName = "sequence_task",
            allocationSize = 1
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

    @NotBlank
    private String description;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime timeOfCreation;

    private boolean completed;


    public Task() {
        this.timeOfCreation = LocalDateTime.now();
        this.completed = false;
    }

    public Task(String name, String description, LocalDateTime timeOfCreation) {

        this.name = name;
        this.description = description;
        this.timeOfCreation = timeOfCreation;

    }

}
