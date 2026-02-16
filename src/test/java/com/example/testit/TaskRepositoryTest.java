package com.example.testit;

import com.example.testit.model.Status;
import com.example.testit.model.Task;
import com.example.testit.model.User;
import com.example.testit.repository.TaskRepository;
import com.example.testit.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(new User("user1", "mdp1"));
        user2 = userRepository.save(new User("user2", "mdp2"));
    }

    @Test
    void save_shouldPersistTask() {
        Task task = new Task("Test Task", "Description", user1);
        Task saved = taskRepository.save(task);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Test Task");
        assertThat(saved.getAssignedUser().getUsername()).isEqualTo("user1");
        assertThat(saved.getStatus()).isEqualTo(Status.OUVERT);
    }

    @Test
    void findById_shouldReturnTask_whenExists() {
        Task saved = taskRepository.save(new Task("Find Me", "Desc", user1));

        Optional<Task> found = taskRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Find Me");
    }

    @Test
    void findAll_shouldReturnAllTasks() {
        taskRepository.save(new Task("Task1", "Desc1", user1));
        taskRepository.save(new Task("Task2", "Desc2", user2));

        List<Task> tasks = taskRepository.findAll();

        assertThat(tasks).hasSize(2);
    }

    @Test
    void findByAssignedUserId_shouldReturnUserTasks() {
        taskRepository.save(new Task("Task1", "Desc1", user1));
        taskRepository.save(new Task("Task2", "Desc2", user1));
        taskRepository.save(new Task("Task3", "Desc3", user2));

        List<Task> user1Tasks = taskRepository.findByAssignedUserId(user1.getId());
        List<Task> user2Tasks = taskRepository.findByAssignedUserId(user2.getId());

        assertThat(user1Tasks).hasSize(2);
        assertThat(user2Tasks).hasSize(1);
    }

    @Test
    void findByUserAndStatus_shouldReturnMatchingTasks() {
        Task task1 = taskRepository.save(new Task("Task1", "Desc1", user1));
        task1.setStatus(Status.EN_COURS);
        taskRepository.save(task1);
        taskRepository.save(new Task("Task2", "Desc2", user1)); // OUVERT
        taskRepository.save(new Task("Task3", "Desc3", user2)); // OUVERT

        List<Task> ongoingTasks = taskRepository.findByUserAndStatus(user1.getId(), Status.EN_COURS);

        assertThat(ongoingTasks).hasSize(1);
        assertThat(ongoingTasks.get(0).getTitle()).isEqualTo("Task1");
    }

    @Test
    void findByUserAndStatus_shouldReturnEmpty_whenNoMatch() {
        taskRepository.save(new Task("Task1", "Desc1", user1));

        List<Task> ongoingTasks = taskRepository.findByUserAndStatus(user1.getId(), Status.FINI);

        assertThat(ongoingTasks).isEmpty();
    }
}
