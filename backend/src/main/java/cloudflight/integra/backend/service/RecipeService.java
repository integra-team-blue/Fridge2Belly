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

        List<Dish> dishes = new ArrayList<>();
        if (updated.getDishIds() != null && !updated.getDishIds().isEmpty()) {
            dishes = dishRepository.findAllByIdIn(updated.getDishIds());
            if (dishes.size() != updated.getDishIds().size()) {
                throw new IllegalArgumentException("Some dishes were not found");
            }
        }

        existingRecipe.setDishes(dishes);

        Recipe savedRecipe = recipeRepository.save(existingRecipe);
        return recipeMapper.toDto(savedRecipe);
    }

    @Transactional
    public void deleteRecipe(UUID id) {
        if (!recipeRepository.existsById(id)) {
            throw new RecipeNotFoundException("Recipe not found with id: " + id);
        }

        List<Dish> dishesUsingRecipe = new ArrayList<>();
        dishRepository.findAll()
                .forEach(dish -> {
                    if (dish.getRecipes() != null && dish.getRecipes()
                            .stream()
                            .anyMatch(r -> r.getId()
                                    .equals(id))) {
                        dishesUsingRecipe.add(dish);
                    }
                });

        for (Dish dish : dishesUsingRecipe) {
            dish.getRecipes()
                    .removeIf(r -> r.getId()
                            .equals(id));
            dishRepository.save(dish);
        }

        recipeRepository.deleteById(id);
    }
}
