

import com.fasterxml.jackson.databind.ObjectMapper;
import cloudflight.integra.backend.BackendApplication;
import cloudflight.integra.backend.model.dtos.RecipeDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = BackendApplication.class)
@AutoConfigureMockMvc
class RecipeDtoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateRecipe() throws Exception {
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setName("ControllerTest");
        recipeDto.setCookingTimeMinutes(30);
        recipeDto.setInstructions("Bake");
        recipeDto.setDishIds(List.of(UUID.randomUUID()));

        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipeDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("ControllerTest"));
    }

    @Test
    void testGetAllRecipes() throws Exception {
        mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk());
    }

    @Test
    void testValidationError() throws Exception {
        RecipeDto recipeDto = new RecipeDto(); // lipsesc câmpurile obligatorii

        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipeDto)))
                .andExpect(status().isBadRequest());
    }
}
