package cloudflight.integra.backend.repository.initial;

import cloudflight.integra.backend.model.dtos.MealDto;

import java.util.List;
import java.util.UUID;

public interface IMealRepository {
    MealDto save(MealDto mealDto);
    MealDto findById(UUID id);
    List<MealDto> findAll();
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
