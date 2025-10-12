package cloudflight.integra.backend.model.mappers;

import cloudflight.integra.backend.model.Meal;
import cloudflight.integra.backend.model.Dish;
import cloudflight.integra.backend.model.dtos.DishDto;
import cloudflight.integra.backend.model.dtos.MealDto;
import cloudflight.integra.backend.repository.DishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
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

        List<DishDto> dishes = meal.getDishes() == null
                ? List.of()
                : meal.getDishes().stream()
                .map(dishMapper::toDto)
                .collect(Collectors.toList());

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

        List<Dish> dishes = dto.getDishes() == null
                ? List.of()
                : dto.getDishes().stream()
                    .map(d -> {
                        if (d.getId() != null) {
                            return dishRepository.findById(d.getId()).orElse(null);
                        }

                        if (d.getName() != null) {
                            return dishRepository.findByName(d.getName()).orElse(null);
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        if (dishes.isEmpty()) {
            System.out.printf("No dishes found for Meal DTO: %s%n", dto.getId());
        }

        return Meal.builder()
                .id(dto.getId())
                .mealType(dto.getMealType())
                .dateTime(dto.getDateTime())
                .dishes(dishes)
                .build();
    }
}
