package uk.gov.hmcts.reform.dev;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

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
import uk.gov.hmcts.reform.dev.model.TaskModel;
import uk.gov.hmcts.reform.dev.model.TaskModel.StatusEnum;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
properties = {"server.port=8080"})
class TaskSmokeTest {
    protected static final String CONTENT_TYPE_VALUE = "application/json";

    @Value("${TEST_URL:http://localhost:8080}")
    private String testUrl;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = testUrl;
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    void taskSmokeTest() throws Exception {
        TaskModel model = new TaskModel(LocalDateTime.now().plusMinutes(1), "Hello", StatusEnum.OPEN);

        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .body(objectMapper.writeValueAsString(model))
            .post(testUrl + "/tasks")
            .then()
            .extract().response();
        assertThat(response.getStatusCode()).isEqualTo(201);

        String location = response.getHeader("Location");
        model.setStatus(StatusEnum.CLOSED);

        Response updateResponse = given()
            .contentType(ContentType.JSON)
            .when()
            .body(objectMapper.writeValueAsString(model))
            .put(testUrl + location)
            .then()
            .extract().response();
        assertThat(updateResponse.statusCode()).isEqualTo(200);

        Response deleteResponse = given()
            .contentType(ContentType.JSON)
            .when()
            .body(objectMapper.writeValueAsString(model))
            .delete(testUrl + location)
            .then()
            .extract().response();
        assertThat(deleteResponse.getStatusCode()).isEqualTo(204);

    }
}
