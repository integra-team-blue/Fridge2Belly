package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.BackendApplication;
import cloudflight.integra.backend.model.Meal;
import cloudflight.integra.backend.model.MealType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = BackendApplication.class)
@AutoConfigureMockMvc
public class MealControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Meal testMeal;

    @BeforeEach
    void setup() {
        testMeal = new Meal(
                UUID.randomUUID(),
                MealType.LUNCH,
                LocalDateTime.now(),
                Collections.singletonList(UUID.randomUUID())
        );
    }

    // POST /api/meals - success
    @Test
    void createMeal_success() throws Exception {
        mockMvc.perform(post("/api/meals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMeal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testMeal.getId().toString()))
                .andExpect(jsonPath("$.mealType").value("LUNCH"));
    }

    // POST /api/meals - fail, no MealType
    @Test
    void createMeal_fail_noMealType() throws Exception {
        testMeal.setMealType(null);

        mockMvc.perform(post("/api/meals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMeal)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Meal type is required"));
    }

    // GET /api/meals - success
    @Test
    void getAllMeals_success() throws Exception {
        // adaugă un meal pentru a te asigura că există cel puțin unul
        mockMvc.perform(post("/api/meals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMeal)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/meals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
    }

    // GET /api/meals/{id} - success
    @Test
    void getMealById_success() throws Exception {
        // creează meal
        mockMvc.perform(post("/api/meals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMeal)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/meals/" + testMeal.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testMeal.getId().toString()));
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
        // creează meal
        mockMvc.perform(post("/api/meals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMeal)))
                .andExpect(status().isOk());

        testMeal.setMealType(MealType.DINNER);

        mockMvc.perform(put("/api/meals/" + testMeal.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMeal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mealType").value("DINNER"));
    }

    // PUT /api/meals/{id} - not found
    @Test
    void updateMeal_notFound() throws Exception {
        UUID randomId = UUID.randomUUID();
        mockMvc.perform(put("/api/meals/" + randomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMeal)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Meal not found with id: " + randomId));
    }

    // DELETE /api/meals/{id} - success
    @Test
    void deleteMeal_success() throws Exception {
        // creează meal
        mockMvc.perform(post("/api/meals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMeal)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/meals/" + testMeal.getId()))
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
