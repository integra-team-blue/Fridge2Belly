package cloudflight.integra.backend.model.mappers;

import cloudflight.integra.backend.model.Recipe;
import cloudflight.integra.backend.model.Dish;
import cloudflight.integra.backend.model.dtos.RecipeDto;
import cloudflight.integra.backend.repository.DishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
        List<UUID> dishIds = recipe.getDishes() != null
                ? recipe.getDishes().stream()
                .map(Dish::getId)
                .collect(Collectors.toList())
                : List.of();

        return RecipeDto.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .description(recipe.getDescription())
                .cookingTimeMinutes(recipe.getCookingTimeMinutes())
                .instructions(recipe.getInstructions())
                .dishIds(dishIds)
                .build();
    }

    public Recipe toEntity(RecipeDto dto) {
        if (dto == null) {
            return null;
        }
        
        List<Dish> dishes = dto.getDishIds() != null
                ? dto.getDishIds().stream()
                .map(id -> dishRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Dish not found: " + id)))
                .collect(Collectors.toList())
                : List.of();

        return Recipe.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .cookingTimeMinutes(dto.getCookingTimeMinutes())
                .instructions(dto.getInstructions())
                .dishes(dishes)
                .build();
    }
}