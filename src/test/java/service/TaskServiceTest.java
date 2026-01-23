import com.example.testit.adapter.mail.MailService;
import com.example.testit.model.Task;
import com.example.testit.repository.TaskRepository;
import com.example.testit.repository.UserRepository;
import com.example.testit.service.TaskService;

import org.assertj.core.api.Assertions;
import org.hibernate.annotations.TimeZoneStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ovh.ruokki.domain.Compte;

class TaskServiceTest {

    TaskService taskService;

    TaskRepository taskRepository;
    UserRepository userRepository;
    MailService mailService;


    @BeforeEach
    void setUp() {
        taskRepository = Mockito.mock(TaskRepository.class);
        userRepository = Mockito.mock(User.class);
        mailService = Mockito.mock(Mail.class);
        taskService = new TaskService(taskService,  userRepository, mailService);
    }

    @Test
    public void testcreattask(){

        var userRequest = new User();
        var userAssigned = new User();
        var newTask = Task();
        
        
    }


    
}
