package cloudflight.integra.backend.model.mappers;

import cloudflight.integra.backend.model.Recipe;
import cloudflight.integra.backend.model.Dish;
import cloudflight.integra.backend.model.dtos.RecipeDto;
import cloudflight.integra.backend.repository.DishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RecipeMapper {

    private final DishRepository dishRepository;

    public RecipeDto toDto(Recipe recipe) {
        if (recipe == null) {
            return null;
        }
        UUID dishId = recipe.getDish() != null ? recipe.getDish()
                .getId() : null;

        return RecipeDto.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .description(recipe.getDescription())
                .cookingTimeMinutes(recipe.getCookingTimeMinutes())
                .instructions(recipe.getInstructions())
                .dishId(dishId)
                .build();
    }

    public Recipe toEntity(RecipeDto dto) {
        if (dto == null) {
            return null;
        }

        Dish dish = null;
        if (dto.getDishId() != null) {
            dish = dishRepository.findById(dto.getDishId())
                    .orElseThrow(() -> new IllegalArgumentException("Dish not found with id: " + dto.getDishId()));
        }

        return Recipe.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .cookingTimeMinutes(dto.getCookingTimeMinutes())
                .instructions(dto.getInstructions())
                .dish(dish)
                .build();
    }
}
