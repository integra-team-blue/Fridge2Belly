

import com.fasterxml.jackson.databind.ObjectMapper;
import cloudflight.integra.backend.BackendApplication;
import cloudflight.integra.backend.model.Recipe;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = BackendApplication.class)   // 👈 specificăm clasa principală
@AutoConfigureMockMvc
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateRecipe() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setName("ControllerTest");
        recipe.setCookingTimeMinutes(30);
        recipe.setInstructions("Bake");
        recipe.setIngredientsId(List.of());

        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipe)))
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
        Recipe recipe = new Recipe(); // lipsesc câmpurile obligatorii

        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipe)))
                .andExpect(status().isBadRequest());
    }
}
