package org.example.taskservice.Tasks;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class TaskConfig {

    @Bean
    CommandLineRunner commandLineRunner(TaskRepository taskRepository) {
        return args -> {
            var tasks = List.of(
                    new Task(
                            "NameFirst",
                            "Test1",
                            LocalDateTime.of(2023, 10,23,12,5)
                    ),
                    new Task(
                            "NameSecond",
                            "Test2",
                            LocalDateTime.of(2025, 10,23,12,5)

                    )
            );
            taskRepository.saveAll(tasks);
        };
    }
}
