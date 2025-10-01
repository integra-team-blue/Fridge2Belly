package cloudflight.integra.backend.service;

import cloudflight.integra.backend.model.Dish;
import cloudflight.integra.backend.model.Ingredient;
import cloudflight.integra.backend.model.Meal;
import cloudflight.integra.backend.model.Recipe;
import cloudflight.integra.backend.model.dtos.DishDto;
import cloudflight.integra.backend.model.mappers.DishMapper;
import cloudflight.integra.backend.repository.DishRepository;
import cloudflight.integra.backend.repository.IngredientRepository;
import cloudflight.integra.backend.repository.MealRepository;
import cloudflight.integra.backend.repository.RecipeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DishService {
    private final DishRepository dishRepository;
    private final DishMapper dishMapper;
    private final MealRepository mealRepository;
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;

    public DishService(DishRepository dishRepository,
                       DishMapper dishMapper,
                       MealRepository mealRepository,
                       RecipeRepository recipeRepository,
                       IngredientRepository ingredientRepository) {
        this.dishRepository = dishRepository;
        this.dishMapper = dishMapper;
        this.mealRepository = mealRepository;
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public DishDto create(DishDto dishDto) {
        Dish dish = dishMapper.toEntity(dishDto);
        Dish savedDish = dishRepository.save(dish);
        return dishMapper.toDto(savedDish);
    }

    @Transactional
    public List<DishDto> getAll() {
        return dishRepository.findAll()
                .stream()
                .map(dishMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public DishDto getById(UUID id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dish not found: " + id));
        return dishMapper.toDto(dish);
    }

    @Transactional
    public DishDto update(UUID id, DishDto dishDto) {
        Dish existingDish = dishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dish not found: " + id));

        existingDish.setName(dishDto.getName());
        existingDish.setPreparedAt(dishDto.getPreparedAt());
        existingDish.setCalories(dishDto.getCalories());
        existingDish.setProtein(dishDto.getProtein());
        existingDish.setFat(dishDto.getFat());
        existingDish.setCarbohydrates(dishDto.getCarbohydrates());

        if (dishDto.getIngredientIds() != null) {
            List<Ingredient> ingredients = dishDto.getIngredientIds()
                    .stream()
                    .map(ingredientId -> ingredientRepository.findById(ingredientId)
                            .orElseThrow(() -> new RuntimeException("Ingredient not found: " + ingredientId)))
                    .collect(Collectors.toList());
            existingDish.setIngredients(ingredients);
        }
        ;

        if (dishDto.getRecipeIds() != null) {
            List<Recipe> recipes = dishDto.getRecipeIds()
                    .stream()
                    .map(recipeId -> recipeRepository.findById(recipeId)
                            .orElseThrow(() -> new RuntimeException("Recipe not found: " + recipeId)))
                    .collect(Collectors.toList());
            existingDish.setRecipes(recipes);
        }


        Dish savedDish = dishRepository.save(existingDish);
        return dishMapper.toDto(savedDish);
    }


    @Transactional
    public void delete(UUID id) {
        if (!dishRepository.existsById(id)) {
            throw new RuntimeException("Dish not found: " + id);
        }

        List<Meal> mealsUsingDish = new ArrayList<>();
        mealRepository.findAll()
                .forEach(meal -> {
                    if (meal.getDishes() != null && meal.getDishes()
                            .stream()
                            .anyMatch(d -> d.getId()
                                    .equals(id))) {
                        mealsUsingDish.add(meal);
                    }
                });

        for (Meal meal : mealsUsingDish) {
            meal.getDishes()
                    .removeIf(d -> d.getId()
                            .equals(id));
            mealRepository.save(meal);
        }

        List<Recipe> recipesUsingDish = new ArrayList<>();
        recipeRepository.findAll()
                .forEach(recipe -> {
                    if (recipe.getDishes() != null && recipe.getDishes()
                            .stream()
                            .anyMatch(d -> d.getId()
                                    .equals(id))) {
                        recipesUsingDish.add(recipe);
                    }
                });

        for (Recipe recipe : recipesUsingDish) {
            recipe.getDishes()
                    .removeIf(d -> d.getId()
                            .equals(id));
            recipeRepository.save(recipe);
        }

        dishRepository.deleteById(id);
    }
}
