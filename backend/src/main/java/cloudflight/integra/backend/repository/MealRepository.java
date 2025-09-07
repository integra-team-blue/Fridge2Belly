package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.model.Meal;

import java.util.List;
import java.util.UUID;

public interface MealRepository {
    Meal save(Meal meal);
    Meal findById(UUID id);
    List<Meal> findAll();
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
