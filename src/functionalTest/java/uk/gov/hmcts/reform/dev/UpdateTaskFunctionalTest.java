package uk.gov.hmcts.reform.dev;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.reform.dev.model.CreatedTaskModel;
import uk.gov.hmcts.reform.dev.model.TaskModel;
import uk.gov.hmcts.reform.dev.model.TaskModel.StatusEnum;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties =
    {
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:public",
        "spring.datasource.username=sa",
        "spring.datasource.password=password"
    })
class UpdateTaskFunctionalTest {

    protected static final String CONTENT_TYPE_VALUE = "application/json";

    @Autowired
    ObjectMapper objectMapper;

    @Value("${TEST_URL:http://localhost:4000}")
    private String testUrl;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = testUrl;
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    void updateTaskFunctionalTest() throws JsonProcessingException {
        TaskModel model = new TaskModel(LocalDateTime.now().plusMinutes(1), "Hello", StatusEnum.OPEN);

        String location = given()
            .contentType(ContentType.JSON)
            .when()
            .body(objectMapper.writeValueAsString(model))
            .post(testUrl + "/tasks")
            .then()
            .extract().response().getHeader("Location");

        model.setStatus(StatusEnum.CLOSED);

        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .body(objectMapper.writeValueAsString(model))
            .put(testUrl + location)
            .then()
            .extract().response();

        assertThat(response.statusCode()).isEqualTo(200);
        CreatedTaskModel updated = objectMapper.readValue(response.getBody().asString(), CreatedTaskModel.class);
        assertThat(updated.getStatus()).isEqualTo(CreatedTaskModel.StatusEnum.CLOSED);
    }

}
