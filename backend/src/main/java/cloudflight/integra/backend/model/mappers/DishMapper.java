package cloudflight.integra.backend.model.mappers;

import cloudflight.integra.backend.model.Dish;
import cloudflight.integra.backend.model.Ingredient;
import cloudflight.integra.backend.model.Recipe;
import cloudflight.integra.backend.model.dtos.DishDto;
import cloudflight.integra.backend.repository.IngredientRepository;
import cloudflight.integra.backend.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DishMapper {
    private final IngredientRepository ingredientRepository;
    private final RecipeRepository recipeRepository;
    
    public Dish toEntity(DishDto dto) {
        List<Recipe> recipes = dto.getRecipeIds() != null
                ? dto.getRecipeIds().stream()
                .map(id -> recipeRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Recipe not found: " + id)))
                .collect(Collectors.toList())
                : List.of();

        List<Ingredient> ingredients = dto.getIngredientIds() != null
                ? dto.getIngredientIds().stream()
                .map(id -> ingredientRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Ingredient not found: " + id)))
                .collect(Collectors.toList())
                : List.of();

        return Dish.builder()
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
    }
    
    public DishDto toDto(Dish dish) {
        List<UUID> recipeIds = dish.getRecipes() != null
                ? dish.getRecipes().stream()
                .map(Recipe::getId)
                .collect(Collectors.toList())
                : List.of();

        List<UUID> ingredientIds = dish.getIngredients() != null
                ? dish.getIngredients().stream()
                .map(Ingredient::getId)
                .collect(Collectors.toList())
                : List.of();
        
        return DishDto.builder()
                .id(dish.getId())
                .name(dish.getName())
                .preparedAt(dish.getPreparedAt())
                .calories(dish.getCalories())
                .protein(dish.getProtein())
                .fat(dish.getFat())
                .carbohydrates(dish.getCarbohydrates())
                .recipeIds(recipeIds)
                .ingredientIds(ingredientIds)
                .build();
    }
}
