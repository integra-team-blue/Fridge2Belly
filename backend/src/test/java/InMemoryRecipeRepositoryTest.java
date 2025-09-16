
import cloudflight.integra.backend.model.Recipe;
import cloudflight.integra.backend.repository.InMemoryRecipeRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryRecipeRepositoryTest {

    private final InMemoryRecipeRepository repository = new InMemoryRecipeRepository();

    @Test
    void testSaveAndFind() {
        Recipe recipe = new Recipe();
        recipe.setName("Test");
        recipe.setCookingTimeMinutes(5);
        recipe.setInstructions("Test");
        recipe.setIngredientsId(List.of());

        Recipe saved = repository.save(recipe);

        assertNotNull(saved.getId());
        assertTrue(repository.findById(saved.getId()).isPresent());
    }

    @Test
    void testDelete() {
        Recipe recipe = new Recipe();
        recipe.setName("DeleteMe");
        recipe.setCookingTimeMinutes(5);
        recipe.setInstructions("Test");
        recipe.setIngredientsId(List.of());

        Recipe saved = repository.save(recipe);
        repository.deleteById(saved.getId());

        assertTrue(repository.findAll().isEmpty());
    }
}
