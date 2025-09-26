

import com.fasterxml.jackson.databind.ObjectMapper;
import cloudflight.integra.backend.BackendApplication;
import cloudflight.integra.backend.model.dtos.RecipeDto;
import cloudflight.integra.backend.model.Dish;
import cloudflight.integra.backend.model.Ingredient;
import cloudflight.integra.backend.repository.DishRepository;
import cloudflight.integra.backend.repository.IngredientRepository;
import cloudflight.integra.backend.repository.RecipeRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = BackendApplication.class)
@AutoConfigureMockMvc
class RecipeDtoControllerTest {
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
    private RecipeRepository recipeRepository;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @BeforeEach
    void setUp() {
        recipeRepository.deleteAll();
        dishRepository.deleteAll();
        ingredientRepository.deleteAll();
    }

    @Test
    void testCreateRecipe() throws Exception {
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setName("ControllerTest");
        recipeDto.setCookingTimeMinutes(30);
        recipeDto.setInstructions("Bake");
        recipeDto.setDishIds(Collections.emptyList());

        MvcResult result = mockMvc.perform(post("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(recipeDto)))
                .andReturn();

        String responseBody = result.getResponse()
                .getContentAsString();
        System.out.println("Response: " + responseBody);
    }


    @Test
    void testCreateRecipeWithExistingDish() throws Exception {
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

        Dish dish = Dish.builder()
                .name("Test Dish")
                .calories(100)
                .protein(10)
                .fat(5)
                .carbohydrates(12)
                .preparedAt(LocalDateTime.now())
                .ingredients(List.of(ingredient))
                .build();
        dish = dishRepository.save(dish);

        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setName("Recipe with Dish");
        recipeDto.setDescription("Test recipe with existing dish");
        recipeDto.setCookingTimeMinutes(45);
        recipeDto.setInstructions("Cook and serve");
        recipeDto.setDishIds(List.of(dish.getId())); // UUID direct, nu String

        mockMvc.perform(post("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(recipeDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Recipe with Dish"));
    }

    @Test
    void testGetAllRecipes() throws Exception {
        mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk());
    }

    @Test
    void testValidationError() throws Exception {
        RecipeDto recipeDto = new RecipeDto();

        mockMvc.perform(post("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(recipeDto)))
                .andExpect(status().isBadRequest());
    }
}
