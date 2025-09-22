package cloudflight.integra.backend.model.mappers;

import cloudflight.integra.backend.model.Meal;
import cloudflight.integra.backend.model.Dish;
import cloudflight.integra.backend.model.dtos.MealDto;
import cloudflight.integra.backend.repository.DishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MealMapper {
    private final DishRepository dishRepository;

    public MealDto toDto(Meal meal) {
        if (meal == null) {
            return null;
        }
        List<UUID> dishIds = meal.getDishes() != null ? meal.getDishes()
                .stream()
                .map(Dish::getId)
                .collect(Collectors.toList()) : List.of();

        return MealDto.builder()
                .id(meal.getId())
                .mealType(meal.getMealType())
                .dateTime(meal.getDateTime())
                .dishIds(dishIds)
                .build();
    }

    public Meal toEntity(MealDto dto) {
        if (dto == null) {
            return null;
        }
        List<Dish> dishes = dto.getDishIds() != null ? dto.getDishIds()
                .stream()
                .map(id -> dishRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Dish not found: " + id)))
                .collect(Collectors.toList()) : List.of();

        return Meal.builder()
                .id(dto.getId())
                .mealType(dto.getMealType())
                .dateTime(dto.getDateTime())
                .dishes(dishes)
                .build();
    }
}
