package cloudflight.integra.backend.model.mappers;

import cloudflight.integra.backend.model.Meal;
import cloudflight.integra.backend.model.Dish;
import cloudflight.integra.backend.model.dtos.DishDto;
import cloudflight.integra.backend.model.dtos.MealDto;
import cloudflight.integra.backend.repository.DishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MealMapper {
    private final DishRepository dishRepository;
    private final DishMapper dishMapper;

    public MealDto toDto(Meal meal) {
        if (meal == null) {
            return null;
        }

        List<DishDto> dishes = meal.getDishes() != null ? meal.getDishes()
                .stream()
                .map(dishMapper::toDto)
                .collect(Collectors.toList()) : new ArrayList<>();

        return MealDto.builder()
                .id(meal.getId())
                .mealType(meal.getMealType())
                .dateTime(meal.getDateTime())
                .dishes(dishes)
                .build();
    }

    public Meal toEntity(MealDto dto) {
        if (dto == null) {
            return null;
        }

        List<UUID> dishIds = dto.getDishes() != null ? dto.getDishes()
                .stream()
                .map(DishDto::getId)
                .collect(Collectors.toList()) : new ArrayList<>();

        List<Dish> dishes = dishIds.isEmpty() ? new ArrayList<>() : dishRepository.findAllByIdIn(dishIds);

//        if (dishes.size() != dishIds.size()) {
//            throw new IllegalArgumentException("Some dishes were not found");
//        }
        if (dishes.size() != dishIds.size()) {
            System.out.println("Some dishes were not found");
        }

        return Meal.builder()
                .id(dto.getId())
                .mealType(dto.getMealType())
                .dateTime(dto.getDateTime())
                .dishes(dishes)
                .build();
    }
}
