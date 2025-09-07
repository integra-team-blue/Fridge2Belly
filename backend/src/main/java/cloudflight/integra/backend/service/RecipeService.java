package cloudflight.integra.backend.service;

import cloudflight.integra.backend.model.Recipe;
import cloudflight.integra.backend.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RecipeService {

    private final RecipeRepository repository;

    public RecipeService(RecipeRepository repository) {
        this.repository = repository;
    }

    public Recipe createRecipe(Recipe recipe) {
        return repository.save(recipe);
    }

    public Recipe getRecipe(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));
    }

    public List<Recipe> getAllRecipes() {
        return repository.findAll();
    }

    public Recipe updateRecipe(UUID id, Recipe updated) {
        Recipe existing = getRecipe(id);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setCookingTimeMinutes(updated.getCookingTimeMinutes());
        existing.setInstructions(updated.getInstructions());
        existing.setIngredientsId(updated.getIngredientsId());
        return repository.save(existing);
    }

    public void deleteRecipe(UUID id) {
        repository.deleteById(id);
    }
}

