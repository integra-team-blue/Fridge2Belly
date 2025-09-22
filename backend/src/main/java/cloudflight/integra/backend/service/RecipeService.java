package cloudflight.integra.backend.service;

import cloudflight.integra.backend.model.dtos.RecipeDto;
import cloudflight.integra.backend.repository.initial.IRecipeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RecipeService {

    private final IRecipeRepository repository;

    public RecipeService(IRecipeRepository repository) {
        this.repository = repository;
    }

    public RecipeDto createRecipe(RecipeDto recipeDto) {
        return repository.save(recipeDto);
    }

    public RecipeDto getRecipe(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));
    }

    public List<RecipeDto> getAllRecipes() { return repository.findAll(); }

    public RecipeDto updateRecipe(UUID id, RecipeDto updated) {
        RecipeDto existing = getRecipe(id);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setCookingTimeMinutes(updated.getCookingTimeMinutes());
        existing.setInstructions(updated.getInstructions());
        existing.setDishIds(updated.getDishIds());
        return repository.save(existing);
    }

    public void deleteRecipe(UUID id) {
        repository.deleteById(id);
    }
}

