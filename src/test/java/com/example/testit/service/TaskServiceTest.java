package com.example.testit.service;

import com.example.testit.adapter.mail.MailService;
import com.example.testit.model.Task;
import com.example.testit.model.User;
import com.example.testit.repository.TaskRepository;
import com.example.testit.repository.UserRepository;
import com.example.testit.service.TaskService;

import org.assertj.core.api.Assertions;
import org.hibernate.annotations.TimeZoneStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;import static org.mockito.ArgumentMatchers.any;

class TaskServiceTest {

    TaskService taskService;

    TaskRepository taskRepository;
    UserRepository userRepository;
    MailService mailService;


    @BeforeEach
    void setUp() {
        taskRepository = Mockito.mock(TaskRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        mailService = Mockito.mock(MailService.class);
        taskService = new TaskService(taskRepository,  userRepository, mailService);
    }

    @Test
    public void testcreattask() {

        var userRequest = new User();
        var userAssigned = new User();
        long assigned = 1;
        long request = 2;
        userAssigned.setId(assigned);
        userRequest.setId(request);

        Mockito.when(userRepository.findById(assigned)).thenReturn(Optional.of(userAssigned));
        Mockito.when(userRepository.findById(request)).thenReturn(Optional.of(userRequest));

        taskService.createTask("Titre", "Description", assigned, request);

        Assertions.assertThat(userAssigned.getId()).isEqualTo(assigned);
        Assertions.assertThat(userRequest.getId()).isEqualTo(request);

        Mockito.verify(taskRepository).save(any());
    }

    @Test
    public void testupdatetask  () {
        var task = new Task();

        task.setId(1L);
        task.setTitle("Titre");
        task.setDescription("Description");

        Mockito.when(taskRepository.existsById(task.getId())).thenReturn(true);

        taskService.updateTask(task);

        Mockito.verify(taskRepository).save(task);

    }
    
}
