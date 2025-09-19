package cloudflight.integra.backend.repository.initial.memory;

import cloudflight.integra.backend.exception.MealNotFoundException;
import cloudflight.integra.backend.model.dtos.MealDto;
import cloudflight.integra.backend.model.MealType;
import cloudflight.integra.backend.repository.initial.IMealRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class InMemoryMealRepository implements IMealRepository {
    private final List<MealDto> mealDtos = new ArrayList<>();

    public InMemoryMealRepository() {
        mealDtos.add(new MealDto(
                                 UUID.fromString("11111111-1111-1111-1111-111111111111"),
                                 MealType.LUNCH,
                                 LocalDateTime.now(),
                                 List.of(UUID.fromString("22222222-2222-2222-2222-222222222222"))
        ));
    }

    @Override
    public MealDto save(MealDto mealDto) {
        mealDtos.add(mealDto);
        return mealDto;
    }

    @Override
    public MealDto findById(UUID id) {
        return mealDtos.stream()
                .filter(meal -> meal.getId()
                        .equals(id))
                .findFirst()
                .orElseThrow(() -> new MealNotFoundException("Meal not found with id: " + id));
    }

    @Override
    public List<MealDto> findAll() {
        return new ArrayList<>(mealDtos);
    }

    @Override
    public void deleteById(UUID id) {
        boolean removed = mealDtos.removeIf(meal -> meal.getId()
                .equals(id));
        if (!removed) {
            throw new MealNotFoundException("Meal not found with id: " + id);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        return mealDtos.stream()
                .anyMatch(meal -> meal.getId()
                        .equals(id));
    }
}
