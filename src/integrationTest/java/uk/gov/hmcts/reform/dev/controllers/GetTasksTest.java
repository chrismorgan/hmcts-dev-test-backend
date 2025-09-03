package uk.gov.hmcts.reform.dev.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.hmcts.reform.dev.model.CreatedTaskModel;
import uk.gov.hmcts.reform.dev.model.CreatedTaskModel.StatusEnum;
import uk.gov.hmcts.reform.dev.service.TaskService;

@WebMvcTest(controllers = TaskController.class)
class GetTasksTest {

    @MockitoBean
    TaskService taskService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private transient MockMvc mockMvc;

    @DisplayName("Should retrieve all Tasks")
    @Test
    void getTasksEndpoint() throws Exception {

        UUID random1 = UUID.randomUUID();
        UUID random2 = UUID.randomUUID();
        CreatedTaskModel model1 = new CreatedTaskModel(random1, LocalDateTime.now(ZoneId.systemDefault()),
                                                      "Write a Report", StatusEnum.OPEN);
        CreatedTaskModel model2 = new CreatedTaskModel(random2, LocalDateTime.now(ZoneId.systemDefault()),
                                                       "Write another Report", StatusEnum.CLOSED);

        when(taskService.getAll()).thenReturn(List.of(model1, model2));

        MvcResult response = mockMvc.perform(get("/tasks"))
            .andExpect(status().isOk()).andReturn();

        var result = objectMapper.readValue(
            response.getResponse().getContentAsString(), new TypeReference<List<CreatedTaskModel>>() {}
        );
        assertThat(result).isEqualTo(List.of(model1, model2));
    }


}
