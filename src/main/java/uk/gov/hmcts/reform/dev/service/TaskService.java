package uk.gov.hmcts.reform.dev.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import uk.gov.hmcts.reform.dev.entity.Task;
import uk.gov.hmcts.reform.dev.exceptions.BusinessRuleException;
import uk.gov.hmcts.reform.dev.exceptions.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.mapper.TaskMapper;
import uk.gov.hmcts.reform.dev.model.CreatedTaskModel;
import uk.gov.hmcts.reform.dev.model.TaskModel;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;
import uk.gov.hmcts.reform.dev.validators.TaskValidator;

@Slf4j
@Service
public class TaskService {

    TaskMapper taskMapper;
    TaskRepository taskRepository;
    TaskValidator taskValidator;

    public TaskService(TaskMapper taskMapper,
                       TaskRepository taskRepository,
                       TaskValidator taskValidator) {
        this.taskMapper = taskMapper;
        this.taskRepository = taskRepository;
        this.taskValidator = taskValidator;
    }

    public UUID createTask(TaskModel task) {
        log.info("Creating Task {}", task.getTitle());
        Task taskEntity = taskMapper.map(task);

        validateTask(taskEntity);

        taskEntity = taskRepository.save(taskEntity);
        return taskEntity.getSystemId();
    }

    public CreatedTaskModel get(UUID id) {
        log.info("Looking up Task [{}]", id);
        Optional<Task> task =  taskRepository.findBySystemId(id);
        return taskMapper.map(task.orElseThrow(() -> new TaskNotFoundException("missing.task", "[%s] not found".formatted(id))));
    }

    public List<CreatedTaskModel> getAll() {
        log.info("Looking up all Tasks");
        List<Task> tasks = taskRepository.findAll();
        return taskMapper.map(tasks);
    }

    public void delete(UUID id) {
        log.info("Deleting Task [{}]", id);
        Optional<Task> optionalTask = taskRepository.findBySystemId(id);
        Task task = optionalTask.orElseThrow(() -> new TaskNotFoundException("missing.task", "[%s] not found".formatted(id)));
        taskRepository.delete(task);
        log.info("Deleted Task [{}]", id);
    }

    public CreatedTaskModel update(UUID id, TaskModel taskModel) {
        log.info("Updating Task [{}]", id);
        Optional<Task> optionalTask = taskRepository.findBySystemId(id);
        Task task = optionalTask.orElseThrow(() -> new TaskNotFoundException("missing.task", "[%s] not found".formatted(id)));
        log.info("Found task [{}]", task);
        task = taskMapper.map(taskModel, task);
        log.info("Updated task [{}]", task);
        return taskMapper.map(taskRepository.save(task));

    }

    private void validateTask(Task task) {
        Errors errors = new BeanPropertyBindingResult(task, "task");
        taskValidator.validate(task, errors);
        if (errors.hasErrors()) {
            FieldError fieldError = errors.getFieldError();
            if(fieldError != null) {
                throw new BusinessRuleException(
                    fieldError.getCode(),
                    fieldError.getDefaultMessage()
                );
            } else {
                ObjectError objectError = errors.getGlobalError();
                if (objectError!=null) {
                    throw new BusinessRuleException(objectError.getCode(), objectError.getDefaultMessage());
                } else {
                    throw new BusinessRuleException("fatal", "invalid task");
                }
            }
        }
    }
}
