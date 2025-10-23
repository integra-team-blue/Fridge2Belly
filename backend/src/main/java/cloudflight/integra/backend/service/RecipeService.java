package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exception.RecipeNotFoundException;
import cloudflight.integra.backend.model.Dish;
import cloudflight.integra.backend.model.Recipe;
import cloudflight.integra.backend.model.dtos.RecipeDto;
import cloudflight.integra.backend.model.mappers.RecipeMapper;
import cloudflight.integra.backend.repository.DishRepository;
import cloudflight.integra.backend.repository.RecipeRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final DishRepository dishRepository;
    private final RecipeMapper recipeMapper;

    public RecipeService(RecipeRepository recipeRepository,
                         DishRepository dishRepository,
                         RecipeMapper recipeMapper) {
        this.recipeRepository = recipeRepository;
        this.dishRepository = dishRepository;
        this.recipeMapper = recipeMapper;
    }

    public RecipeDto createRecipe(RecipeDto recipeDto) {
        Recipe recipe = recipeMapper.toEntity(recipeDto);
        Recipe savedRecipe = recipeRepository.save(recipe);
        return recipeMapper.toDto(savedRecipe);
    }

    @Transactional(readOnly = true)
    public RecipeDto getRecipe(UUID id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found with id: " + id));
        return recipeMapper.toDto(recipe);
    }

    @Transactional(readOnly = true)
    public List<RecipeDto> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        recipeRepository.findAll()
                .forEach(recipes::add);

        return recipes.stream()
                .map(recipeMapper::toDto)
                .collect(Collectors.toList());
    }

    public RecipeDto updateRecipe(UUID id, RecipeDto updated) {
        Recipe existingRecipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found with id: " + id));

        existingRecipe.setName(updated.getName());
        existingRecipe.setDescription(updated.getDescription());
        existingRecipe.setCookingTimeMinutes(updated.getCookingTimeMinutes());
        existingRecipe.setInstructions(updated.getInstructions());

        if (updated.getDishId() != null) {
            Dish dish = dishRepository.findById(updated.getDishId())
                    .orElseThrow(() -> new IllegalArgumentException("Dish not found with id: " + updated.getDishId()));
            existingRecipe.setDish(dish);
        }

        Recipe savedRecipe = recipeRepository.save(existingRecipe);
        return recipeMapper.toDto(savedRecipe);
    }

    @Transactional
    public void deleteRecipe(UUID id) {
        if (!recipeRepository.existsById(id)) {
            throw new RecipeNotFoundException("Recipe not found with id: " + id);
        }

        recipeRepository.deleteById(id);
    }
}
