package uk.gov.hmcts.reform.dev.controllers;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.dev.model.CreatedTaskModel;
import uk.gov.hmcts.reform.dev.model.TaskModel;
import uk.gov.hmcts.reform.dev.service.TaskService;

@Slf4j
@RestController
public class TaskController implements TaskApi {

    TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public ResponseEntity<Void> createTask(TaskModel taskModel) {
        UUID systemId = taskService.createTask(taskModel);
        return ResponseEntity.created(URI.create("/tasks/%s".formatted(systemId))).build();
    }

    @Override
    public ResponseEntity<Void> deleteTask(UUID id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<CreatedTaskModel> getTask(UUID id) {
        return ResponseEntity.ok(taskService.get(id));
    }

    @Override
    public ResponseEntity<List<CreatedTaskModel>> listTasks() {
        return ResponseEntity.ok(taskService.getAll());
    }

    @Override
    public ResponseEntity<CreatedTaskModel> updateTask(UUID id, TaskModel taskModel) {
        return ResponseEntity.ok(taskService.update(id, taskModel));
    }
}
