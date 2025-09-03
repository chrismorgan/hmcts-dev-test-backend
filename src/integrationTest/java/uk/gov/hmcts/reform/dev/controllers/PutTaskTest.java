package uk.gov.hmcts.reform.dev.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.hmcts.reform.dev.model.CreatedTaskModel;
import uk.gov.hmcts.reform.dev.model.TaskModel;
import uk.gov.hmcts.reform.dev.service.TaskService;

@WebMvcTest(controllers = TaskController.class)
class PutTaskTest {

    @MockitoBean
    TaskService taskService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private transient MockMvc mockMvc;

    @DisplayName("Should update a Task by Id")
    @Test
    void putTaskEndpoint() throws Exception {

        UUID random = UUID.randomUUID();
        CreatedTaskModel createdModel = new CreatedTaskModel(random, LocalDateTime.now(ZoneId.systemDefault()),
                                                             "Write Report", CreatedTaskModel.StatusEnum.OPEN);
        TaskModel model = new TaskModel(LocalDateTime.now(ZoneId.systemDefault()),
                                        "Write Report", TaskModel.StatusEnum.OPEN);

        when(taskService.update(random, model)).thenReturn(createdModel);

        MvcResult response = mockMvc.perform(put("/tasks/{id}", random)
             .contentType(MediaType.APPLICATION_JSON)
             .content(objectMapper.writeValueAsString(model)))
            .andExpect(status().isOk()).andReturn();

        var result = objectMapper.readValue(response.getResponse().getContentAsString(), CreatedTaskModel.class);
        assertThat(result).isEqualTo(createdModel);
    }


}
