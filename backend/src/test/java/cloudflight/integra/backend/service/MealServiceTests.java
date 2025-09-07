package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exception.MealNotFoundException;
import cloudflight.integra.backend.model.Meal;
import cloudflight.integra.backend.model.MealType;
import cloudflight.integra.backend.repository.MealRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MealServiceTests {

    private MealRepository mealRepository;
    private MealService mealService;

    @BeforeEach
    void setup() {
        mealRepository = Mockito.mock(MealRepository.class);
        mealService = new MealService(mealRepository);
    }

    @Test
    void testCreateMealSuccess() {
        Meal meal = new Meal(null, MealType.BREAKFAST, LocalDateTime.now(), Arrays.asList(UUID.randomUUID()));
        when(mealRepository.save(meal)).thenAnswer(i -> {
            meal.setId(UUID.randomUUID());
            return meal;
        });

        Meal created = mealService.createMeal(meal);

        assertNotNull(created.getId());
        assertEquals(MealType.BREAKFAST, created.getMealType());
        verify(mealRepository, times(1)).save(meal);
    }

    @Test
    void testCreateMealMissingMealType() {
        Meal meal = new Meal(null, null, LocalDateTime.now(), Arrays.asList(UUID.randomUUID()));
        assertThrows(IllegalArgumentException.class, () -> mealService.createMeal(meal));
        verify(mealRepository, never()).save(meal);
    }

    @Test
    void testGetAllMeals() {
        List<Meal> meals = Arrays.asList(
                new Meal(UUID.randomUUID(), MealType.LUNCH, LocalDateTime.now(), Arrays.asList(UUID.randomUUID())),
                new Meal(UUID.randomUUID(), MealType.DINNER, LocalDateTime.now(), Arrays.asList(UUID.randomUUID()))
        );
        when(mealRepository.findAll()).thenReturn(meals);

        List<Meal> result = mealService.getAllMeals();

        assertEquals(meals, result);
        verify(mealRepository, times(1)).findAll();
    }

    @Test
    void testGetMealByIdSuccess() {
        Meal meal = new Meal(UUID.randomUUID(), MealType.DINNER, LocalDateTime.now(), Arrays.asList(UUID.randomUUID()));
        when(mealRepository.findById(meal.getId())).thenReturn(meal);

        Meal found = mealService.getMealById(meal.getId());

        assertEquals(meal, found);
        verify(mealRepository, times(1)).findById(meal.getId());
    }

    @Test
    void testGetMealByIdNotFound() {
        UUID randomId = UUID.randomUUID();
        when(mealRepository.findById(randomId)).thenThrow(new MealNotFoundException("Meal not found"));

        assertThrows(MealNotFoundException.class, () -> mealService.getMealById(randomId));
        verify(mealRepository, times(1)).findById(randomId);
    }

    @Test
    void testUpdateMealSuccess() {
        UUID id = UUID.randomUUID();
        Meal meal = new Meal(id, MealType.LUNCH, LocalDateTime.now(), Arrays.asList(UUID.randomUUID()));
        when(mealRepository.existsById(id)).thenReturn(true);
        when(mealRepository.save(meal)).thenReturn(meal);

        Meal updated = mealService.updateMeal(id, meal);

        assertEquals(meal, updated);
        verify(mealRepository, times(1)).existsById(id);
        verify(mealRepository, times(1)).save(meal);
    }

    @Test
    void testUpdateMealNotFound() {
        UUID id = UUID.randomUUID();
        Meal meal = new Meal(id, MealType.LUNCH, LocalDateTime.now(), Arrays.asList(UUID.randomUUID()));
        when(mealRepository.existsById(id)).thenReturn(false);

        assertThrows(MealNotFoundException.class, () -> mealService.updateMeal(id, meal));
        verify(mealRepository, times(1)).existsById(id);
        verify(mealRepository, never()).save(meal);
    }

    @Test
    void testDeleteMealSuccess() {
        UUID id = UUID.randomUUID();
        doNothing().when(mealRepository).deleteById(id);

        mealService.deleteMeal(id);

        verify(mealRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteMealNotFound() {
        UUID id = UUID.randomUUID();
        doThrow(new MealNotFoundException("Meal not found")).when(mealRepository).deleteById(id);

        assertThrows(MealNotFoundException.class, () -> mealService.deleteMeal(id));
        verify(mealRepository, times(1)).deleteById(id);
    }
}

