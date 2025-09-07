package cloudflight.integra.backend.repository.impl;

import cloudflight.integra.backend.exception.MealNotFoundException;
import cloudflight.integra.backend.model.Meal;
import cloudflight.integra.backend.model.MealType;
import cloudflight.integra.backend.repository.MealRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final List<Meal> meals = new ArrayList<>();

    public InMemoryMealRepository() {
        //
        meals.add(new Meal(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                MealType.LUNCH,
                LocalDateTime.now(),
                List.of(UUID.fromString("22222222-2222-2222-2222-222222222222"))
        ));
    }

    @Override
    public Meal save(Meal meal) {
        meals.add(meal);
        return meal;
    }

    @Override
    public Meal findById(UUID id) {
        return meals.stream()
                .filter(meal -> meal.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new MealNotFoundException("Meal not found with id: " + id));
    }

    @Override
    public List<Meal> findAll() {
        return new ArrayList<>(meals);
    }

    @Override
    public void deleteById(UUID id) {
        boolean removed = meals.removeIf(meal -> meal.getId().equals(id));
        if (!removed) {
            throw new MealNotFoundException("Meal not found with id: " + id);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        return meals.stream().anyMatch(meal -> meal.getId().equals(id));
    }
}
