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

        List<Dish> dishes = new ArrayList<>();

        if (dto.getDishes() != null && !dto.getDishes()
                .isEmpty()) {
            for (DishDto dishDto : dto.getDishes()) {
                Dish dish = null;

                if (dishDto.getId() != null) {
                    dish = dishRepository.findById(dishDto.getId())
                            .orElse(null);
                }

                if (dish == null && dishDto.getName() != null) {
                    dish = dishRepository.findByName(dishDto.getName())
                            .orElse(null);
                }

                if (dish != null) {
                    dishes.add(dish);
                } else {
                    System.out.println("Dish not found for DTO: " + dishDto);
                }
            }
        }

        else if (dto.getDishIds() != null && !dto.getDishIds()
                .isEmpty()) {
                    dishes = dishRepository.findAllByIdIn(dto.getDishIds());
                }

        if (dishes.isEmpty()) {
            System.out.println("No dishes found for meal DTO: " + dto.getId());
        }

        return Meal.builder()
                .id(dto.getId())
                .mealType(dto.getMealType())
                .dateTime(dto.getDateTime())
                .dishes(dishes)
                .build();
    }
}
