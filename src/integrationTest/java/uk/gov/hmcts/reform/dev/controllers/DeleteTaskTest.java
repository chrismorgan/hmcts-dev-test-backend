package uk.gov.hmcts.reform.dev.controllers;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.dev.service.TaskService;

@WebMvcTest(controllers = TaskController.class)
class DeleteTaskTest {

    @MockitoBean
    TaskService taskService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private transient MockMvc mockMvc;

    @DisplayName("Should delete a Task by Id")
    @Test
    void deleteTaskEndpoint() throws Exception {

        UUID random = UUID.randomUUID();

        doNothing().when(taskService).delete(random);

        mockMvc.perform(delete("/tasks/{id}", random))
            .andExpect(status().isNoContent())
            .andReturn();

    }


}
