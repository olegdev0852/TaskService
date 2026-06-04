package org.example.taskservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.taskservice.api.state.TaskState;
import org.example.taskservice.exception.user.BadRequestException;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public boolean isAssignmentValid() {
        return (assignedTo != null && deadline != null)
                || (assignedTo == null && deadline == null);
    }

    public void assignWithDeadline(UUID userId, LocalDateTime taskDeadline) {
        System.out.println("Assigning with deadline " + taskDeadline);
        System.out.println("Assigning with user " + userId);
        if ((userId == null) != (taskDeadline == null)) {
            throw new BadRequestException(
                    "Назначение исполнителя и дедлайн обязательны одновременно"
            );
        }
        this.assignedTo = userId;
        this.deadline = taskDeadline;
    }
}