import cloudflight.integra.backend.model.dtos.RecipeDto;
import cloudflight.integra.backend.repository.initial.memory.InMemoryRecipeRepository;
import cloudflight.integra.backend.service.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecipeDtoServiceTest {

    private RecipeService service;

    @BeforeEach
    void setup() {
        service = new RecipeService(new InMemoryRecipeRepository());
    }

    @Test
    void testCreateAndGetRecipe() {
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setName("ServiceTest");
        recipeDto.setCookingTimeMinutes(10);
        recipeDto.setInstructions("Test");
        recipeDto.setDishIds(List.of());

        RecipeDto saved = service.createRecipe(recipeDto);

        assertNotNull(saved.getId());
        assertEquals("ServiceTest", service.getRecipe(saved.getId()).getName());
    }

    @Test
    void testUpdateRecipe() {
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setName("Old");
        recipeDto.setCookingTimeMinutes(5);
        recipeDto.setInstructions("Old");
        recipeDto.setDishIds(List.of());

        RecipeDto saved = service.createRecipe(recipeDto);
        RecipeDto update = new RecipeDto();
        update.setName("New");
        update.setCookingTimeMinutes(15);
        update.setInstructions("New");
        update.setDishIds(List.of());

        RecipeDto updated = service.updateRecipe(saved.getId(), update);

        assertEquals("New", updated.getName());
        assertEquals(15, updated.getCookingTimeMinutes());
    }
}
