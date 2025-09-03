package uk.gov.hmcts.reform.dev;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.reform.dev.model.ErrorModel;
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
class CreateTaskFunctionalTest {

    protected static final String CONTENT_TYPE_VALUE = "application/json";

    @Autowired
    ObjectMapper objectMapper;

    @Value("${TEST_URL:http://localhost:4000/tasks}")
    private String testUrl;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = testUrl;
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    void createTaskFunctionalTest() throws JsonProcessingException {
        TaskModel model = new TaskModel(LocalDateTime.now().plusMinutes(1), "Hello", StatusEnum.OPEN);

        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .body(objectMapper.writeValueAsString(model))
            .post(testUrl)
            .then()
            .extract().response();

        Assertions.assertEquals(201, response.statusCode());
        assertThat(response.getHeader("Location")).isNotEmpty();
    }

    @Test
    void createTaskFunctionalTest_baddate_isUnprocessable() throws JsonProcessingException {
        TaskModel model = new TaskModel(LocalDateTime.now().minusDays(1), "Hello", StatusEnum.OPEN);

        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .body(objectMapper.writeValueAsString(model))
            .post(testUrl)
            .then()
            .extract().response().andReturn();

        Assertions.assertEquals(422, response.statusCode());
        ErrorModel error = objectMapper.readValue(response.getBody().asString(), ErrorModel.class);
        assertThat(error.getMessage()).isEqualTo("The due date should be in the future");
    }
}
