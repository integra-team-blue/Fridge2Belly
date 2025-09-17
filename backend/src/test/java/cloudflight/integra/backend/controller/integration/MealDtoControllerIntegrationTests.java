package cloudflight.integra.backend.controller.integration;

import cloudflight.integra.backend.BackendApplication;
import cloudflight.integra.backend.model.dtos.MealDto;
import cloudflight.integra.backend.model.MealType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = BackendApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MealDtoControllerIntegrationTests {
    @Container
    @ServiceConnection
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:14.6")
            .withDatabaseName("integration-tests-db")
            .withUsername("it")
            .withPassword("it");
    
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;
    private MealDto testMealDto;

    @BeforeEach
    void setup() {
        baseUrl = "http://localhost:" + port + "/api/meals";
        testMealDto = new MealDto(
                UUID.randomUUID(),
                MealType.LUNCH,
                LocalDateTime.now(),
                Collections.singletonList(UUID.randomUUID())
        );
    }

    // POST /api/meals - success
    @Test
    void testCreateMealSuccess() {
        ResponseEntity<MealDto> response = restTemplate.postForEntity(baseUrl, testMealDto, MealDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(testMealDto.getId());
        assertThat(response.getBody().getMealType()).isEqualTo(MealType.LUNCH);
    }

    // POST /api/meals - fail, no MealType
    @Test
    void testCreateMealFailNoMealType() {
        testMealDto.setMealType(null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MealDto> entity = new HttpEntity<>(testMealDto, headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {}
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();

        Map<String, String> fieldErrors = (Map<String, String>) response.getBody().get("fieldErrors");
        assertThat(fieldErrors.get("mealType")).isEqualTo("Meal type is required");
    }

    // GET /api/meals - success
    @Test
    void testGetAllMealsSuccess() {
        restTemplate.postForEntity(baseUrl, testMealDto, MealDto.class);

        ResponseEntity<MealDto[]> response = restTemplate.getForEntity(baseUrl, MealDto[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    // GET /api/meals/{id} - success
    @Test
    void testGetMealByIdSuccess() {
        restTemplate.postForEntity(baseUrl, testMealDto, MealDto.class);

        ResponseEntity<MealDto> response = restTemplate.getForEntity(baseUrl + "/" + testMealDto.getId(), MealDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(testMealDto.getId());
    }

    // GET /api/meals/{id} - not found
    @Test
    void testGetMealByIdNotFound() {
        UUID randomId = UUID.randomUUID();

        ResponseEntity<Map<String, Object>> response =
                restTemplate.exchange(baseUrl + "/" + randomId, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().get("message")).isEqualTo("Meal not found with id: " + randomId);
    }

    // PUT /api/meals/{id} - success
    @Test
    void testUpdateMealSuccess() {
        restTemplate.postForEntity(baseUrl, testMealDto, MealDto.class);

        testMealDto.setMealType(MealType.DINNER);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MealDto> entity = new HttpEntity<>(testMealDto, headers);

        ResponseEntity<MealDto> response = restTemplate.exchange(
                baseUrl + "/" + testMealDto.getId(),
                HttpMethod.PUT,
                entity,
                MealDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getMealType()).isEqualTo(MealType.DINNER);
    }

    // PUT /api/meals/{id} - not found
    @Test
    void testUpdateMealNotFound() {
        UUID randomId = UUID.randomUUID();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MealDto> entity = new HttpEntity<>(testMealDto, headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                baseUrl + "/" + randomId,
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().get("message")).isEqualTo("Meal not found with id: " + randomId);
    }

    // DELETE /api/meals/{id} - success
    @Test
    void testDeleteMealSuccess() {
        restTemplate.postForEntity(baseUrl, testMealDto, MealDto.class);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/" + testMealDto.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    // DELETE /api/meals/{id} - not found
    @Test
    void testDeleteMealNotFound() {
        UUID randomId = UUID.randomUUID();

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                baseUrl + "/" + randomId,
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().get("message")).isEqualTo("Meal not found with id: " + randomId);
    }
}
