package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.model.dtos.DishDto;
import cloudflight.integra.backend.model.*;
import cloudflight.integra.backend.repository.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DishDtoControllerIntegrationTests {
    @Container
    @ServiceConnection
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:14.6")
            .withDatabaseName("integration-tests-db")
            .withUsername("it")
            .withPassword("it");

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private DishRepository dishRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private IngredientRepository ingredientRepository;

    private UUID realRecipeId;
    private UUID realIngredientId;

    @BeforeEach
    void setUp() {
        dishRepository.deleteAll();
        recipeRepository.deleteAll();
        ingredientRepository.deleteAll();

        createTestEntities();
    }

    private void createTestEntities() {
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
        realIngredientId = ingredient.getId();

        Recipe recipe = Recipe.builder()
                .name("Test Recipe")
                .cookingTimeMinutes(30)
                .instructions("Test instructions")
                .dishes(new ArrayList<>())
                .build();
        recipe = recipeRepository.save(recipe);
        realRecipeId = recipe.getId();
    }

    private String body(String name) {
        return """
                {
                  "name": "%s",
                  "recipeIds": ["%s"],
                  "preparedAt": "2025-01-01T12:00:00",
                  "calories": 100, "protein": 10, "fat": 5, "carbohydrates": 12,
                  "ingredientIds": ["%s"]
                }""".formatted(name, realRecipeId, realIngredientId);
    }

    private DishDto createDish(String name) throws Exception {
        MvcResult res = mvc.perform(post("/api/dishes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body(name)))
                .andExpect(status().isCreated())
                .andReturn();
        return mapper.readValue(res.getResponse()
                .getContentAsString(), DishDto.class);
    }

    private DishDto getDish(UUID id) throws Exception {
        MvcResult res = mvc.perform(get("/api/dishes/{id}", id))
                .andExpect(status().isOk())
                .andReturn();
        return mapper.readValue(res.getResponse()
                .getContentAsString(), DishDto.class);
    }

    private DishDto updateDish(UUID id, String newName) throws Exception {
        MvcResult res = mvc.perform(put("/api/dishes/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body(newName)))
                .andExpect(status().isOk())
                .andReturn();
        return mapper.readValue(res.getResponse()
                .getContentAsString(), DishDto.class);
    }

    private void deleteDish(UUID id) throws Exception {
        mvc.perform(delete("/api/dishes/{id}", id))
                .andExpect(status().isNoContent());
    }

    private int listCount() throws Exception {
        MvcResult res = mvc.perform(get("/api/dishes"))
                .andExpect(status().isOk())
                .andReturn();
        DishDto[] arr = mapper.readValue(res.getResponse()
                .getContentAsByteArray(), DishDto[].class);
        return arr.length;
    }

    @Test
    void createDish_works() throws Exception {
        DishDto d = createDish("Pasta");
        assertThat(d.getId()).isNotNull();
        assertThat(d.getName()).isEqualTo("Pasta");
    }

    @Test
    void getDishById_works() throws Exception {
        DishDto created = createDish("Soup");
        DishDto got = getDish(created.getId());
        assertThat(got.getId()).isEqualTo(created.getId());
        assertThat(got.getName()).isEqualTo("Soup");
    }

    @Test
    void updateDish_works() throws Exception {
        DishDto created = createDish("Old");
        DishDto updated = updateDish(created.getId(), "New");
        assertThat(updated.getId()).isEqualTo(created.getId());
        assertThat(updated.getName()).isEqualTo("New");
    }

    @Test
    void deleteDish_works() throws Exception {
        DishDto created = createDish("Temp");
        deleteDish(created.getId());
        assertThat(listCount()).isZero();
    }

    @Test
    void listDishes_works() throws Exception {
        createDish("A");
        createDish("B");
        assertThat(listCount()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void blankName_returns400() throws Exception {
        mvc.perform(post("/api/dishes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body("")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void negativeCalories_returns400() throws Exception {
        String bad = """
                {
                  "name": "Bad",
                  "recipeIds": ["%s"],
                  "preparedAt": "2025-01-01T12:00:00",
                  "calories": -1, "protein": 10, "fat": 5, "carbohydrates": 12,
                  "ingredientIds": ["%s"]
                }""".formatted(realRecipeId, realIngredientId);
        mvc.perform(post("/api/dishes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bad))
                .andExpect(status().isBadRequest());
    }
}
