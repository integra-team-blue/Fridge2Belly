package cloudflight.integra.backend.model.mappers;

import cloudflight.integra.backend.model.Dish;
import cloudflight.integra.backend.model.Ingredient;
import cloudflight.integra.backend.model.Recipe;
import cloudflight.integra.backend.model.dtos.DishCreateDto;
import cloudflight.integra.backend.model.dtos.DishDto;
import cloudflight.integra.backend.model.dtos.IngredientDto;
import cloudflight.integra.backend.model.dtos.RecipeDto;
import cloudflight.integra.backend.repository.IngredientRepository;
import cloudflight.integra.backend.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DishMapper {
    private final IngredientRepository ingredientRepository;
    private final RecipeRepository recipeRepository;
    private final IngredientMapper ingredientMapper;

    public Dish toEntity(DishDto dto) {
        List<UUID> recipeIds = dto.getRecipes() != null ? dto.getRecipes()
                .stream()
                .map(RecipeDto::getId)
                .collect(Collectors.toList()) : new ArrayList<>();

        List<Recipe> recipes = recipeIds.isEmpty() ? new ArrayList<>() : recipeRepository.findAllByIdIn(recipeIds);

        if (recipes.size() != recipeIds.size()) {
            throw new IllegalArgumentException("Some recipes were not found");
        }

        List<UUID> ingredientIds = dto.getIngredients() != null ? dto.getIngredients()
                .stream()
                .map(IngredientDto::getId)
                .collect(Collectors.toList()) : new ArrayList<>();

        List<Ingredient> ingredients = ingredientIds.isEmpty() ? new ArrayList<>() : ingredientRepository.findAllByIdIn(
                                                                                                                        ingredientIds);

        if (ingredients.size() != ingredientIds.size()) {
            throw new IllegalArgumentException("Some ingredients were not found");
        }

        Dish dish = Dish.builder()
                .id(dto.getId())
                .name(dto.getName())
                .preparedAt(dto.getPreparedAt())
                .calories(dto.getCalories())
                .protein(dto.getProtein())
                .fat(dto.getFat())
                .carbohydrates(dto.getCarbohydrates())
                .recipes(recipes)
                .ingredients(ingredients)
                .build();

        for (Recipe recipe : recipes) {
            recipe.setDish(dish);
        }

        return dish;
    }

    public DishDto toDto(Dish dish) {
        List<RecipeDto> recipes = dish.getRecipes() != null ? dish.getRecipes()
                .stream()
                .map(recipe -> RecipeDto.builder()
                        .id(recipe.getId())
                        .name(recipe.getName())
                        .description(recipe.getDescription())
                        .cookingTimeMinutes(recipe.getCookingTimeMinutes())
                        .instructions(recipe.getInstructions())
                        .dishId(recipe.getDish() != null ? recipe.getDish()
                                .getId() : null)
                        .build())
                .collect(Collectors.toList()) : new ArrayList<>();

        List<IngredientDto> ingredients = dish.getIngredients() != null ? dish.getIngredients()
                .stream()
                .map(ingredientMapper::toDto)
                .collect(Collectors.toList()) : new ArrayList<>();
        return DishDto.builder()
                .id(dish.getId())
                .name(dish.getName())
                .preparedAt(dish.getPreparedAt())
                .calories(dish.getCalories())
                .protein(dish.getProtein())
                .fat(dish.getFat())
                .carbohydrates(dish.getCarbohydrates())
                .recipes(recipes)
                .ingredients(ingredients)
                .build();
    }

    public Dish fromCreateDto(DishCreateDto dto) {
        List<Recipe> recipes = dto.getRecipeIds() != null && !dto.getRecipeIds()
                .isEmpty() ? recipeRepository.findAllByIdIn(dto.getRecipeIds()) : new ArrayList<>();

        List<Ingredient> ingredients = dto.getIngredientIds() != null && !dto.getIngredientIds()
                .isEmpty() ? ingredientRepository.findAllByIdIn(dto.getIngredientIds()) : new ArrayList<>();

        if (recipes.size() != (dto.getRecipeIds() != null ? dto.getRecipeIds()
                .size() : 0)) {
            throw new IllegalArgumentException("Some recipes not found");
        }

        if (ingredients.size() != (dto.getIngredientIds() != null ? dto.getIngredientIds()
                .size() : 0)) {
            throw new IllegalArgumentException("Some ingredients not found");
        }

        Dish dish = Dish.builder()
                .name(dto.getName())
                .preparedAt(dto.getPreparedAt())
                .calories(dto.getCalories())
                .protein(dto.getProtein())
                .fat(dto.getFat())
                .carbohydrates(dto.getCarbohydrates())
                .recipes(recipes)
                .ingredients(ingredients)
                .build();

        for (Recipe recipe : recipes) {
            recipe.setDish(dish);
        }

        return dish;
    }

}
