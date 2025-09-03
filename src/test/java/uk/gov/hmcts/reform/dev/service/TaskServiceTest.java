package uk.gov.hmcts.reform.dev.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.dev.entity.Status;
import uk.gov.hmcts.reform.dev.entity.Task;
import uk.gov.hmcts.reform.dev.exceptions.BusinessRuleException;
import uk.gov.hmcts.reform.dev.exceptions.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.mapper.TaskMapper;
import uk.gov.hmcts.reform.dev.mapper.TaskMapperImpl;
import uk.gov.hmcts.reform.dev.model.CreatedTaskModel;
import uk.gov.hmcts.reform.dev.model.TaskModel;
import uk.gov.hmcts.reform.dev.model.TaskModel.StatusEnum;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;
import uk.gov.hmcts.reform.dev.validators.TaskValidator;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    TaskRepository taskRepository;

    TaskService taskService;
    TaskMapper mapper = new TaskMapperImpl();
    TaskValidator taskValidator = new TaskValidator();

    @BeforeEach
    void setup() {
        taskService = new TaskService(mapper, taskRepository, taskValidator);
    }

    @Test
    void taskService_createTask_createsTask() {
        UUID random = UUID.randomUUID();
        LocalDateTime now =  LocalDateTime.now().plusMinutes(1);
        Task task = new Task(1, random, "New Task", "Do something", Status.OPEN, now);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskModel taskModel = new TaskModel(now, "New Task", StatusEnum.OPEN);

        var result = taskService.createTask(taskModel);

        assertThat(result).isEqualTo(random);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void taskService_createTaskBadDate_throwsException() {
        LocalDateTime now =  LocalDateTime.now().minusDays(1);

        TaskModel taskModel = new TaskModel(now, "New Task", StatusEnum.OPEN);

        assertThatException().isThrownBy(() -> taskService.createTask(taskModel))
            .isInstanceOf(BusinessRuleException.class)
            .withMessage("The due date should be in the future");
    }

    @Test
    void taskService_getTask_retrievesTask() {
        UUID random = UUID.randomUUID();
        LocalDateTime now =  LocalDateTime.now();
        Task task = new Task(1, random, "New Task", "Do something", Status.OPEN, now);
        when(taskRepository.findBySystemId(random)).thenReturn(Optional.of(task));

        var result = taskService.get(random);

        CreatedTaskModel taskModel = new CreatedTaskModel(random, now, "New Task", CreatedTaskModel.StatusEnum.OPEN);
        taskModel.setDescription("Do something");

        assertThat(result).isEqualTo(taskModel);
        verify(taskRepository).findBySystemId(random);
    }

    @Test
    void taskService_getTaskNoTask_throwsException() {
        UUID random = UUID.randomUUID();
        when(taskRepository.findBySystemId(random)).thenReturn(Optional.empty());

        assertThatException().isThrownBy(() -> taskService.get(random))
            .isInstanceOf(TaskNotFoundException.class)
            .withMessage("[%s] not found", random);
    }

    @Test
    void taskService_putTask_updatesTask() {
        UUID random = UUID.randomUUID();
        LocalDateTime now =  LocalDateTime.now();
        Task task = new Task(1, random, "New Task", "Do something", Status.OPEN, now);
        when(taskRepository.findBySystemId(random)).thenReturn(Optional.of(task));

        Task updatedTask = new Task(1, random, "New Task", "Laugh out loud", Status.OPEN, now);
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        TaskModel taskModel = new TaskModel(now, "New Task", TaskModel.StatusEnum.OPEN);
        taskModel.setDescription("Laugh out loud");

        var result = taskService.update(random, taskModel);

        CreatedTaskModel createdTaskModel = new CreatedTaskModel(random, now, "New Task", CreatedTaskModel.StatusEnum.OPEN);
        createdTaskModel.setDescription("Laugh out loud");
        assertThat(result).isEqualTo(createdTaskModel);
        verify(taskRepository).findBySystemId(random);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void taskService_getTasks_retrievesAllTasks() {
        UUID random1 = UUID.randomUUID();
        UUID random2 = UUID.randomUUID();
        LocalDateTime now =  LocalDateTime.now();
        Task task1 = new Task(1, random1, "New Task", "Do something", Status.OPEN, now);
        Task task2 = new Task(2, random2, "Newer Task", "Do something else", Status.OPEN, now);
        when(taskRepository.findAll()).thenReturn(List.of(task1, task2));

        var result = taskService.getAll();

        CreatedTaskModel taskModel1 = new CreatedTaskModel(random1, now, "New Task", CreatedTaskModel.StatusEnum.OPEN);
        taskModel1.setDescription("Do something");

        CreatedTaskModel taskModel2 = new CreatedTaskModel(random2, now, "Newer Task", CreatedTaskModel.StatusEnum.OPEN);
        taskModel2.setDescription("Do something else");
        assertThat(result).contains(taskModel1, taskModel2);
        verify(taskRepository).findAll();
    }

    @Test
    void taskService_deleteTask_deletesTask() {
        UUID random = UUID.randomUUID();
        LocalDateTime now =  LocalDateTime.now();
        Task task = new Task(1, random, "New Task", "Do something", Status.OPEN, now);
        when(taskRepository.findBySystemId(random)).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).delete(task);

        assertThatNoException().isThrownBy(() -> taskService.delete(random));

        verify(taskRepository).delete(task);
        verify(taskRepository).findBySystemId(random);
        verifyNoMoreInteractions(taskRepository);
    }
}
