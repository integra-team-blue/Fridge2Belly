
import cloudflight.integra.backend.model.dtos.RecipeDto;
import cloudflight.integra.backend.repository.initial.memory.InMemoryRecipeRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryRecipeDtoRepositoryTest {

    private final InMemoryRecipeRepository repository = new InMemoryRecipeRepository();

    @Test
    void testSaveAndFind() {
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setName("Test");
        recipeDto.setCookingTimeMinutes(5);
        recipeDto.setInstructions("Test");
        recipeDto.setDishIds(List.of());

        RecipeDto saved = repository.save(recipeDto);

        assertNotNull(saved.getId());
        assertTrue(repository.findById(saved.getId()).isPresent());
    }

    @Test
    void testDelete() {
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setName("DeleteMe");
        recipeDto.setCookingTimeMinutes(5);
        recipeDto.setInstructions("Test");
        recipeDto.setDishIds(List.of());

        RecipeDto saved = repository.save(recipeDto);
        repository.deleteById(saved.getId());

        assertTrue(repository.findAll().isEmpty());
    }
}
