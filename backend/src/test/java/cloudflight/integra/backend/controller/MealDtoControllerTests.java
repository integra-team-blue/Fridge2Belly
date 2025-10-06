package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.BackendApplication;
import cloudflight.integra.backend.model.Dish;
import cloudflight.integra.backend.model.Ingredient;
import cloudflight.integra.backend.model.Recipe;
import cloudflight.integra.backend.model.dtos.DishDto;
import cloudflight.integra.backend.model.dtos.MealDto;
import cloudflight.integra.backend.model.MealType;
import cloudflight.integra.backend.repository.DishRepository;
import cloudflight.integra.backend.repository.IngredientRepository;
import cloudflight.integra.backend.repository.MealRepository;
import cloudflight.integra.backend.repository.RecipeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = BackendApplication.class)
@AutoConfigureMockMvc
public class MealDtoControllerTests {
    @Container
    @ServiceConnection
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:14.6")
            .withDatabaseName("integration-tests-db")
            .withUsername("it")
            .withPassword("it");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
                .dishes(List.of(DishDto.builder()
                        .id(realDishId)
                        .build()))
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
                .expirationDate(LocalDate.now()
                        .plusDays(7))
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
    void createMeal_success() throws Exception {
        mockMvc.perform(post("/api/meals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMealDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.mealType").value("LUNCH"));
    }

    // POST /api/meals - fail, no MealType
    @Test
    void createMeal_fail_noMealType() throws Exception {
        testMealDto.setMealType(null);

        mockMvc.perform(post("/api/meals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMealDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.mealType").value("Meal type is required"));
    }

    // GET /api/meals - success
    @Test
    void getAllMeals_success() throws Exception {
        mockMvc.perform(post("/api/meals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMealDto)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/meals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
    }

    // GET /api/meals/{id} - success
    @Test
    void getMealById_success() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/meals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMealDto)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse()
                .getContentAsString();
        MealDto createdMeal = objectMapper.readValue(responseContent, MealDto.class);

        mockMvc.perform(get("/api/meals/" + createdMeal.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdMeal.getId()
                        .toString()));
    }

    // GET /api/meals/{id} - not found
    @Test
    void getMealById_notFound() throws Exception {
        UUID randomId = UUID.randomUUID();
        mockMvc.perform(get("/api/meals/" + randomId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Meal not found with id: " + randomId));
    }

    // PUT /api/meals/{id} - success
    @Test
    void updateMeal_success() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/meals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMealDto)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse()
                .getContentAsString();
        MealDto createdMeal = objectMapper.readValue(responseContent, MealDto.class);

        createdMeal.setMealType(MealType.DINNER);

        mockMvc.perform(put("/api/meals/" + createdMeal.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createdMeal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mealType").value("DINNER"));
    }

    // PUT /api/meals/{id} - not found
    @Test
    void updateMeal_notFound() throws Exception {
        UUID randomId = UUID.randomUUID();
        mockMvc.perform(put("/api/meals/" + randomId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMealDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Meal not found with id: " + randomId));
    }

    // DELETE /api/meals/{id} - success
    @Test
    void deleteMeal_success() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/meals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMealDto)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse()
                .getContentAsString();
        MealDto createdMeal = objectMapper.readValue(responseContent, MealDto.class);

        mockMvc.perform(delete("/api/meals/" + createdMeal.getId()))
                .andExpect(status().isOk());
    }

    // DELETE /api/meals/{id} - not found
    @Test
    void deleteMeal_notFound() throws Exception {
        UUID randomId = UUID.randomUUID();
        mockMvc.perform(delete("/api/meals/" + randomId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Meal not found with id: " + randomId));
    }
}
