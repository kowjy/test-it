package com.example.testit;

import com.example.testit.adapter.mail.MailService;
import com.example.testit.model.Status;
import com.example.testit.model.Task;
import com.example.testit.model.User;
import com.example.testit.repository.TaskRepository;
import com.example.testit.repository.UserRepository;
import com.example.testit.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MailService mailService;

    @InjectMocks
    private TaskService taskService;

    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
        user = new User("testuser","test");
        user.setId(1L);
        task = new Task("Test Task", "Description", user);
        task.setId(1L);
        task.setStatus(Status.OUVERT);
        task.setRequester(user);
    }

    @Test
    void createTask_shouldCreateTask() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task created = taskService.createTask("Test Task", "Description", 1L, 1L);

        assertThat(created.getTitle()).isEqualTo("Test Task");
        assertThat(created.getAssignedUser().getUsername()).isEqualTo("testuser");
        assertThat(created.getRequester()).isNotNull();
    }

    @Test
    void startTask_shouldStartTask_whenValid() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.findByUserAndStatus(1L, Status.EN_COURS)).thenReturn(List.of());
        when(taskRepository.save(task)).thenReturn(task);

        Task started = taskService.startTask(1L, 1L);

        assertThat(started.getStatus()).isEqualTo(Status.EN_COURS);
    }

    @Test
    void startTask_shouldThrow_whenTaskNotAssigned() {
        task.setAssignedUser(new User("other", "test"));
        task.getAssignedUser().setId(2L);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.startTask(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Task not assigned to this user");
    }

    @Test
    void startTask_shouldThrow_whenTaskNotOuvert() {
        task.setStatus(Status.EN_COURS);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.startTask(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Task can only be started if status is OUVERT");
    }

    @Test
    void startTask_shouldThrow_whenUserHasOngoingTask() {
        Task ongoing = new Task("Ongoing", "Desc", user);
        ongoing.setStatus(Status.EN_COURS);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.findByUserAndStatus(1L, Status.EN_COURS)).thenReturn(List.of(ongoing));

        assertThatThrownBy(() -> taskService.startTask(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("User cannot have more than one task EN_COURS");
    }

    @Test
    void finishTask_shouldFinishTask_whenValid() {
        task.setStatus(Status.EN_COURS);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        Task finished = taskService.finishTask(1L, 1L);

        assertThat(finished.getStatus()).isEqualTo(Status.FINI);
    }

    @Test
    void finishTask_shouldThrow_whenTaskNotEnCours() {
        task.setStatus(Status.FINI);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.finishTask(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Task can only be finished if status is EN_COURS");
    }
}
