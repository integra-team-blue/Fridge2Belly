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
        List<UUID> dishIds = recipe.getDishes() != null ? recipe.getDishes()
                .stream()
                .map(Dish::getId)
                .collect(Collectors.toList()) : new ArrayList<>();

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

        List<UUID> dishIds = dto.getDishIds() != null ? new ArrayList<>(dto.getDishIds()) : new ArrayList<>();
        List<Dish> dishes = dishIds.isEmpty() ? new ArrayList<>() : dishRepository.findAllByIdIn(dishIds);

        if (dishes.size() != dishIds.size()) {
            throw new IllegalArgumentException("Some dishes were not found");
        }

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
