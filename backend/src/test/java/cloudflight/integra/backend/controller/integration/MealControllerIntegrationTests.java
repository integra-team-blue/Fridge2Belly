package cloudflight.integra.backend.controller.integration;

import cloudflight.integra.backend.BackendApplication;
import cloudflight.integra.backend.model.Meal;
import cloudflight.integra.backend.model.MealType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import cloudflight.integra.backend.exception.GlobalExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = BackendApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MealControllerIntegrationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;
    private Meal testMeal;

    @BeforeEach
    void setup() {
        baseUrl = "http://localhost:" + port + "/api/meals";
        testMeal = new Meal(
                UUID.randomUUID(),
                MealType.LUNCH,
                LocalDateTime.now(),
                Collections.singletonList(UUID.randomUUID())
        );
    }

    // POST /api/meals - success
    @Test
    void testCreateMealSuccess() {
        ResponseEntity<Meal> response = restTemplate.postForEntity(baseUrl, testMeal, Meal.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(testMeal.getId());
        assertThat(response.getBody().getMealType()).isEqualTo(MealType.LUNCH);
    }

    // POST /api/meals - fail, no MealType
    @Test
    void testCreateMealFailNoMealType() {
        testMeal.setMealType(null);

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            restTemplate.postForEntity(baseUrl, testMeal, Map.class);
        });

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        String responseBody = exception.getResponseBodyAsString();
        assertThat(responseBody).contains("Validation failed");
        assertThat(responseBody).contains("mealType");
    }


    // GET /api/meals - success
    @Test
    void testGetAllMealsSuccess() {
        restTemplate.postForEntity(baseUrl, testMeal, Meal.class);

        ResponseEntity<Meal[]> response = restTemplate.getForEntity(baseUrl, Meal[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    // GET /api/meals/{id} - success
    @Test
    void testGetMealByIdSuccess() {
        restTemplate.postForEntity(baseUrl, testMeal, Meal.class);

        ResponseEntity<Meal> response = restTemplate.getForEntity(baseUrl + "/" + testMeal.getId(), Meal.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(testMeal.getId());
    }

    // GET /api/meals/{id} - not found
    @Test
    void testGetMealByIdNotFound() {
        UUID randomId = UUID.randomUUID();

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            restTemplate.getForEntity(baseUrl + "/" + randomId, Map.class);
        });

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        String responseBody = exception.getResponseBodyAsString();
        assertThat(responseBody).contains("Meal not found with id: " + randomId);

    }

    // PUT /api/meals/{id} - success
    @Test
    void testUpdateMealSuccess() {
        restTemplate.postForEntity(baseUrl, testMeal, Meal.class);

        testMeal.setMealType(MealType.DINNER);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Meal> entity = new HttpEntity<>(testMeal, headers);

        ResponseEntity<Meal> response = restTemplate.exchange(
                baseUrl + "/" + testMeal.getId(),
                HttpMethod.PUT,
                entity,
                Meal.class
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
        HttpEntity<Meal> entity = new HttpEntity<>(testMeal, headers);

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            restTemplate.exchange(
                    baseUrl + "/" + randomId,
                    HttpMethod.PUT,
                    entity,
                    Map.class
            );
        });

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        String responseBody = exception.getResponseBodyAsString();
        assertThat(responseBody).contains("Meal not found with id: " + randomId);
    }

    // DELETE /api/meals/{id} - success
    @Test
    void testDeleteMealSuccess() {
        restTemplate.postForEntity(baseUrl, testMeal, Meal.class);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/" + testMeal.getId(),
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

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            restTemplate.exchange(
                    baseUrl + "/" + randomId,
                    HttpMethod.DELETE,
                    null,
                    Map.class
            );
        });

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        String responseBody = exception.getResponseBodyAsString();
        assertThat(responseBody).contains("Meal not found with id: " + randomId);
    }
}
