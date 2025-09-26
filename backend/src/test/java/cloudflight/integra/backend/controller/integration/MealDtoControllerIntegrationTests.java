package cloudflight.integra.backend.controller.integration;

import cloudflight.integra.backend.BackendApplication;
import cloudflight.integra.backend.model.dtos.DishDto;
import cloudflight.integra.backend.model.dtos.MealDto;
import cloudflight.integra.backend.model.*;
import cloudflight.integra.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private MealRepository mealRepository;

    private MealDto testMealDto;
    private UUID realDishId;
    private List<DishDto> dishes;

    @BeforeEach
    void setup() {
        mealRepository.deleteAll();
        dishRepository.deleteAll();
        recipeRepository.deleteAll();
        ingredientRepository.deleteAll();

        realDishId = createTestDish();

        testMealDto = MealDto.builder()
                .id(UUID.randomUUID())
                .mealType(MealType.LUNCH)
                .dateTime(LocalDateTime.now())
                .dishIds(Collections.singletonList(realDishId))
                .build();
    }

    private UUID createTestDish() {
        Ingredient ingredient = Ingredient.builder()
                .name("Test Ingredient")
                .quantity(100.0)
                .unit("grams")
                .calories(50.0)
                .protein(5.0)
                .fat(2.0)
                .carbohydrates(8.0)
                .expirationDate(LocalDate.now().plusDays(7))
                .build();
        ingredient = ingredientRepository.save(ingredient);

        Recipe recipe = Recipe.builder()
                .name("Test Recipe")
                .cookingTimeMinutes(30)
                .instructions("Test instructions")
                .dishes(new ArrayList<>())
                .build();
        recipe = recipeRepository.save(recipe);

        Dish dish = Dish.builder()
                .name("Test Dish")
                .calories(100)
                .protein(10)
                .fat(5)
                .carbohydrates(12)
                .preparedAt(LocalDateTime.now())
                .ingredients(List.of(ingredient))
                .recipes(List.of(recipe))
                .build();
        dish = dishRepository.save(dish);

        return dish.getId();
    }

    // POST /api/meals - success
    @Test
    void testCreateMealSuccess() {
        ResponseEntity<MealDto> response = restTemplate.postForEntity(
                "/api/meals",
                testMealDto,
                MealDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getMealType()).isEqualTo(MealType.LUNCH);
        assertThat(response.getBody().getDishIds()).containsExactly(realDishId);
    }

    // POST /api/meals - fail, no MealType
    @Test
    void testCreateMealFailNoMealType() {
        testMealDto.setMealType(null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MealDto> entity = new HttpEntity<>(testMealDto, headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "/api/meals",
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
        ResponseEntity<MealDto> createResponse = restTemplate.postForEntity(
                "/api/meals",
                testMealDto,
                MealDto.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<MealDto[]> response = restTemplate.getForEntity(
                "/api/meals",
                MealDto[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    // GET /api/meals/{id} - success
    @Test
    void testGetMealByIdSuccess() {
        ResponseEntity<MealDto> createResponse = restTemplate.postForEntity(
                "/api/meals",
                testMealDto,
                MealDto.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        UUID createdMealId = createResponse.getBody().getId();

        ResponseEntity<MealDto> response = restTemplate.getForEntity(
                "/api/meals/" + createdMealId,
                MealDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(createdMealId);
    }

    // GET /api/meals/{id} - not found
    @Test
    void testGetMealByIdNotFound() {
        UUID randomId = UUID.randomUUID();

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "/api/meals/" + randomId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // PUT /api/meals/{id} - success
    @Test
    void testUpdateMealSuccess() {
        ResponseEntity<MealDto> createResponse = restTemplate.postForEntity(
                "/api/meals",
                testMealDto,
                MealDto.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        MealDto createdMeal = createResponse.getBody();
        createdMeal.setMealType(MealType.DINNER);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MealDto> entity = new HttpEntity<>(createdMeal, headers);

        ResponseEntity<MealDto> response = restTemplate.exchange(
                "/api/meals/" + createdMeal.getId(),
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
                "/api/meals/" + randomId,
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // DELETE /api/meals/{id} - success
    @Test
    void testDeleteMealSuccess() {
        ResponseEntity<MealDto> createResponse = restTemplate.postForEntity(
                "/api/meals",
                testMealDto,
                MealDto.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        UUID createdMealId = createResponse.getBody().getId();

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/meals/" + createdMealId,
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
                "/api/meals/" + randomId,
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
