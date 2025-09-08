package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.model.Dish;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DishControllerIntegrationTests {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;

    private String validBody(String name) {
        return """
            {
              "name": "%s",
              "recipeId": "11111111-1111-1111-1111-111111111111",
              "preparedAt": "2025-01-01T12:00:00",
              "calories": 100,
              "protein": 10,
              "fat": 5,
              "carbohydrates": 12
            }
            """.formatted(name);
    }

    @Test
    void create_read_update_delete_flow() throws Exception {
        // CREATE
        MvcResult createdResult = mvc.perform(post("/api/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody("Pasta")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Pasta"))
                .andReturn();

        Dish created = mapper.readValue(createdResult.getResponse().getContentAsString(), Dish.class);
        UUID id = created.getId();
        assertThat(id).isNotNull();

        // READ ONE
        mvc.perform(get("/api/dishes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));

        // READ ALL
        mvc.perform(get("/api/dishes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        // UPDATE
        mvc.perform(put("/api/dishes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody("NewName")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NewName"))
                .andExpect(jsonPath("$.id").value(id.toString()));

        // DELETE
        mvc.perform(delete("/api/dishes/{id}", id))
                .andExpect(status().isNoContent());

        // READ ALL -> empty
        mvc.perform(get("/api/dishes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void validation_blankName_returns400() throws Exception {
        String bad = validBody(""); // name blank

        mvc.perform(post("/api/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bad))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validation_negativeCalories_returns400() throws Exception {
        String bad = """
            {
              "name": "Bad",
              "recipeId": "11111111-1111-1111-1111-111111111111",
              "preparedAt": "2025-01-01T12:00:00",
              "calories": -1,
              "protein": 10,
              "fat": 5,
              "carbohydrates": 12
            }
            """;

        mvc.perform(post("/api/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bad))
                .andExpect(status().isBadRequest());
    }
}
