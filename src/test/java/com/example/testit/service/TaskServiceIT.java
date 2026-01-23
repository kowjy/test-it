package com.example.testit.service;

import com.example.testit.model.Task;
import com.example.testit.repository.TaskRepository;
import com.example.testit.repository.UserRepository;
import com.example.testit.service.TaskService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TaskServiceIT {

    @Autowired
    TaskService taskService;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    public void test() {
        var task = new Task();
        Task saved = taskRepository.save(task);

        Assertions.assertThat(taskRepository.findAll()).isEqualTo(saved);
    }

}
