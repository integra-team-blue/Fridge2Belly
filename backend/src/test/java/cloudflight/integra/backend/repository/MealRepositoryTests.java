package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.exception.MealNotFoundException;
import cloudflight.integra.backend.model.Meal;
import cloudflight.integra.backend.model.MealType;
import cloudflight.integra.backend.repository.impl.InMemoryMealRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class MealRepositoryTests {

    private InMemoryMealRepository repository;

    @BeforeEach
    void setup() {
        repository = new InMemoryMealRepository();
    }

    @Test
    void testSaveAndFindAllMeals() {
        UUID mealId1 = UUID.randomUUID();
        UUID mealId2 = UUID.randomUUID();
        List<UUID> dishes1 = new ArrayList<>();
        dishes1.add(UUID.randomUUID());
        List<UUID> dishes2 = new ArrayList<>();
        dishes2.add(UUID.randomUUID());

        Meal meal1 = new Meal(mealId1, MealType.BREAKFAST, LocalDateTime.now(), dishes1);
        Meal meal2 = new Meal(mealId2, MealType.DINNER, LocalDateTime.now(), dishes2);

        repository.save(meal1);
        repository.save(meal2);

        List<Meal> allMeals = repository.findAll();
        assertTrue(allMeals.contains(meal1));
        assertTrue(allMeals.contains(meal2));
        assertTrue(allMeals.size() >= 2); // include si meal-ul initial din constructor
    }

    @Test
    void testFindById() {
        Meal existingMeal = repository.findAll().get(0);
        Meal found = repository.findById(existingMeal.getId());
        assertEquals(existingMeal, found);
    }

    @Test
    void testFindByIdNotFound() {
        UUID randomId = UUID.randomUUID();
        assertThrows(MealNotFoundException.class, () -> repository.findById(randomId));
    }

    @Test
    void testDeleteById() {
        Meal meal = new Meal(UUID.randomUUID(), MealType.LUNCH, LocalDateTime.now(),
                List.of(UUID.randomUUID()));
        repository.save(meal);

        assertTrue(repository.existsById(meal.getId()));
        repository.deleteById(meal.getId());
        assertFalse(repository.existsById(meal.getId()));
    }

    @Test
    void testExistsById() {
        Meal meal = new Meal(UUID.randomUUID(), MealType.DINNER, LocalDateTime.now(),
                List.of(UUID.randomUUID()));
        repository.save(meal);

        assertTrue(repository.existsById(meal.getId()));
        assertFalse(repository.existsById(UUID.randomUUID()));
    }
}

