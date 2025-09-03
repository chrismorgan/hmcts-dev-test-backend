package uk.gov.hmcts.reform.dev.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.reform.dev.model.TaskModel.StatusEnum.OPEN;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.hmcts.reform.dev.model.TaskModel;
import uk.gov.hmcts.reform.dev.service.TaskService;

@WebMvcTest(controllers = TaskController.class)
class CreateTaskTest {

    @MockitoBean
    TaskService taskService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private transient MockMvc mockMvc;

    @DisplayName("Should create a Task with 201 response code and Location header")
    @Test
    void createTaskEndpoint() throws Exception {

        TaskModel model = new TaskModel(
            LocalDateTime.now(),
            "Write Report", OPEN);
        UUID random = UUID.randomUUID();
        when(taskService.createTask(model)).thenReturn(random);

        MvcResult response = mockMvc.perform(post("/tasks")
             .contentType(MediaType.APPLICATION_JSON)
             .content(objectMapper.writeValueAsString(model)))
            .andExpect(status().isCreated()).andReturn();

        var location = response.getResponse().getHeader("Location");
        assertThat(location).startsWith("/tasks/");
    }


}
