package cloudflight.integra.backend.service;

import cloudflight.integra.backend.model.Recipe;
import cloudflight.integra.backend.model.dtos.RecipeDto;
import cloudflight.integra.backend.model.mappers.RecipeMapper;
import cloudflight.integra.backend.repository.RecipeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;

    public RecipeService(RecipeRepository recipeRepository, RecipeMapper recipeMapper) {
        this.recipeRepository = recipeRepository;
        this.recipeMapper = recipeMapper;
    }

    public RecipeDto createRecipe(RecipeDto recipeDto) {
        Recipe recipe = recipeMapper.toEntity(recipeDto);
        Recipe savedRecipe = recipeRepository.save(recipe);
        return recipeMapper.toDto(savedRecipe);
    }

    public RecipeDto getRecipe(UUID id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));
        return recipeMapper.toDto(recipe);
    }

    @Transactional
    public List<RecipeDto> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        recipeRepository.findAll().forEach(recipes::add);

        return recipes.stream()
                .map(recipeMapper::toDto)
                .collect(Collectors.toList());
    }

    public RecipeDto updateRecipe(UUID id, RecipeDto updated) {
        Recipe existingRecipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        existingRecipe.setName(updated.getName());
        existingRecipe.setDescription(updated.getDescription());
        existingRecipe.setCookingTimeMinutes(updated.getCookingTimeMinutes());
        existingRecipe.setInstructions(updated.getInstructions());

        Recipe savedRecipe = recipeRepository.save(existingRecipe);
        return recipeMapper.toDto(savedRecipe);
    }

    public void deleteRecipe(UUID id) {
        recipeRepository.deleteById(id);
    }
}

