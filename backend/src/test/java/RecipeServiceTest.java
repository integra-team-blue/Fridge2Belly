import cloudflight.integra.backend.model.Recipe;
import cloudflight.integra.backend.repository.InMemoryRecipeRepository;
import cloudflight.integra.backend.service.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecipeServiceTest {

    private RecipeService service;

    @BeforeEach
    void setup() {
        service = new RecipeService(new InMemoryRecipeRepository());
    }

    @Test
    void testCreateAndGetRecipe() {
        Recipe recipe = new Recipe();
        recipe.setName("ServiceTest");
        recipe.setCookingTimeMinutes(10);
        recipe.setInstructions("Test");
        recipe.setIngredientsId(List.of());

        Recipe saved = service.createRecipe(recipe);

        assertNotNull(saved.getId());
        assertEquals("ServiceTest", service.getRecipe(saved.getId()).getName());
    }

    @Test
    void testUpdateRecipe() {
        Recipe recipe = new Recipe();
        recipe.setName("Old");
        recipe.setCookingTimeMinutes(5);
        recipe.setInstructions("Old");
        recipe.setIngredientsId(List.of());

        Recipe saved = service.createRecipe(recipe);
        Recipe update = new Recipe();
        update.setName("New");
        update.setCookingTimeMinutes(15);
        update.setInstructions("New");
        update.setIngredientsId(List.of());

        Recipe updated = service.updateRecipe(saved.getId(), update);

        assertEquals("New", updated.getName());
        assertEquals(15, updated.getCookingTimeMinutes());
    }
}