package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.exception.IngredientsExeption;
import cloudflight.integra.backend.model.Ingredients;
import cloudflight.integra.backend.service.IngredientsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class IngredientsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IngredientsService ingredientsService;

    private ObjectMapper objectMapper;
    private Ingredients testIngredient;
    private UUID testId;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders
                .standaloneSetup(new IngredientsController(ingredientsService))
                .build();

        testId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        testIngredient = createTestIngredient();
    }

    private Ingredients createTestIngredient() {
        Ingredients ingredient = new Ingredients();
        ingredient.setId(testId);
        ingredient.setName("Test Tomato");
        ingredient.setQuantity(2.5);
        ingredient.setUnit("kg");
        ingredient.setExpirationDate(LocalDate.of(2025, 9, 15));
        ingredient.setCalories(18.0);
        ingredient.setProtein(0.9);
        ingredient.setFat(0.2);
        ingredient.setCarbohydrates(3.9);
        return ingredient;
    }



    @Test
    void getAllIngredients_ShouldReturn200WithEmptyList_WhenNoIngredients() throws Exception {
        when(ingredientsService.getAllIngredients()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/ingredients"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(ingredientsService).getAllIngredients();
    }

    @Test
    void getAllIngredients_ShouldReturn200WithList_WhenIngredientsExist() throws Exception {
        List<Ingredients> ingredients = Arrays.asList(testIngredient);
        when(ingredientsService.getAllIngredients()).thenReturn(ingredients);

        mockMvc.perform(get("/api/ingredients"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testId.toString())))
                .andExpect(jsonPath("$[0].name", is("Test Tomato")))
                .andExpect(jsonPath("$[0].quantity", is(2.5)))
                .andExpect(jsonPath("$[0].unit", is("kg")));

        verify(ingredientsService).getAllIngredients();
    }



    @Test
    void getIngredientById_ShouldReturn200_WhenIngredientExists() throws Exception {
        when(ingredientsService.getIngredientById(testId)).thenReturn(testIngredient);

        mockMvc.perform(get("/api/ingredients/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testId.toString())))
                .andExpect(jsonPath("$.name", is("Test Tomato")))
                .andExpect(jsonPath("$.quantity", is(2.5)));

        verify(ingredientsService).getIngredientById(testId);
    }


    @Test
    void getIngredientById_ShouldReturn400_WhenInvalidUUID() throws Exception {
        mockMvc.perform(get("/api/ingredients/{id}", "invalid-uuid"))
                .andExpect(status().isBadRequest());

        verify(ingredientsService, never()).getIngredientById(any());
    }


    @Test
    void createIngredient_ShouldReturn415_WhenWrongContentType() throws Exception {
        mockMvc.perform(post("/api/ingredients")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("plain text"))
                .andExpect(status().isUnsupportedMediaType());

        verify(ingredientsService, never()).createIngredient(any());
    }

    @Test
    void createIngredient_ShouldReturn400_WhenEmptyBody() throws Exception {
        mockMvc.perform(post("/api/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());

        verify(ingredientsService, never()).createIngredient(any());
    }



    @Test
    void updateIngredient_ShouldReturn200_WhenValidData() throws Exception {
        Ingredients updatedIngredient = createTestIngredient();
        updatedIngredient.setName("Updated Tomato");
        updatedIngredient.setQuantity(3.0);

        when(ingredientsService.updateIngredient(eq(testId), any(Ingredients.class)))
                .thenReturn(updatedIngredient);

        String requestBody = """
            {
                "name": "Updated Tomato",
                "quantity": 3.0,
                "unit": "kg"
            }
            """;

        mockMvc.perform(put("/api/ingredients/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Updated Tomato")))
                .andExpect(jsonPath("$.quantity", is(3.0)));

        verify(ingredientsService).updateIngredient(eq(testId), any(Ingredients.class));
    }



    @Test
    void updateIngredient_ShouldReturn400_WhenInvalidUUID() throws Exception {
        String requestBody = """
            {
                "name": "Test",
                "quantity": 1.0,
                "unit": "kg"
            }
            """;

        mockMvc.perform(put("/api/ingredients/{id}", "invalid-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(ingredientsService, never()).updateIngredient(any(), any());
    }


    @Test
    void deleteIngredient_ShouldReturn204_WhenIngredientExists() throws Exception {
        doNothing().when(ingredientsService).deleteIngredient(testId);

        mockMvc.perform(delete("/api/ingredients/{id}", testId))
                .andExpect(status().isNoContent());

        verify(ingredientsService).deleteIngredient(testId);
    }


    @Test
    void deleteIngredient_ShouldReturn400_WhenInvalidUUID() throws Exception {
        mockMvc.perform(delete("/api/ingredients/{id}", "invalid-uuid"))
                .andExpect(status().isBadRequest());

        verify(ingredientsService, never()).deleteIngredient(any());
    }





}