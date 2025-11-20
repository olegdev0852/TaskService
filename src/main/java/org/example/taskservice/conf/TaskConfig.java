package org.example.taskservice.conf;

import org.example.taskservice.entity.Task;
import org.example.taskservice.repository.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class TaskConfig {

    @Bean
    CommandLineRunner commandLineRunner(TaskRepository taskRepository) {
        return args -> {
            var tasks = List.of(
                    new Task(
                            "NameFirst",
                            "Test1"
                    ),
                    new Task(
                            "NameSecond",
                            "Test2"
                    )
            );
            taskRepository.saveAll(tasks);
        };
    }
}
