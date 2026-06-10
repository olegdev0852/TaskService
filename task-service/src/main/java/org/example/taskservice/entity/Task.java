package org.example.taskservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.taskservice.api.state.TaskState;

import java.time.LocalDateTime;
import java.util.UUID;

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
    @Column(name = "name", nullable = false, length = 70)
    private String name;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 50)
    private TaskState state = TaskState.CREATED;

    @Column(name = "no_test", nullable = false)
    private boolean noTest = false;

    @Column(name = "tech_task", nullable = false)
    private boolean techTask = false;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Column(name = "assigned_to")
    private UUID assignedTo;

    @Column(name = "author_id")
    private UUID authorId;


    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @PrePersist
    protected void onCreated(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdated(){
        this.updatedAt = LocalDateTime.now();
    }


}