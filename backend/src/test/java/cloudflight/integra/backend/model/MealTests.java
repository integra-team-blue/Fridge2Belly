package cloudflight.integra.backend.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class MealTests {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testMealValidationSuccess() {
        List<UUID> dishes = new ArrayList<>();
        dishes.add(UUID.randomUUID());
        Meal meal = new Meal(UUID.randomUUID(), MealType.BREAKFAST, LocalDateTime.now(), dishes);

        Set<ConstraintViolation<Meal>> violations = validator.validate(meal);
        assertEquals(0, violations.size());
    }

    @Test
    void testMealValidationFail_MealTypeNull() {
        List<UUID> dishes = new ArrayList<>();
        dishes.add(UUID.randomUUID());
        Meal meal = new Meal(UUID.randomUUID(), null, LocalDateTime.now(), dishes);

        Set<ConstraintViolation<Meal>> violations = validator.validate(meal);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("mealType")));
    }

    @Test
    void testMealValidationFail_DateTimeNull() {
        List<UUID> dishes = new ArrayList<>();
        dishes.add(UUID.randomUUID());
        Meal meal = new Meal(UUID.randomUUID(), MealType.LUNCH, null, dishes);

        Set<ConstraintViolation<Meal>> violations = validator.validate(meal);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("dateTime")));
    }

    @Test
    void testMealValidationFail_DishIdsNull() {
        Meal meal = new Meal(UUID.randomUUID(), MealType.DINNER, LocalDateTime.now(), null);

        Set<ConstraintViolation<Meal>> violations = validator.validate(meal);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("dishIds")));
    }

    @Test
    void testMealValidationFail_DishIdsEmpty() {
        List<UUID> dishes = new ArrayList<>();
        Meal meal = new Meal(UUID.randomUUID(), MealType.DINNER, LocalDateTime.now(), dishes);

        Set<ConstraintViolation<Meal>> violations = validator.validate(meal);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("dishIds")));
    }

    @Test
    void testMealValidationFail_DishIdElementNull() {
        List<UUID> dishes = new ArrayList<>();
        dishes.add(null);
        Meal meal = new Meal(UUID.randomUUID(), MealType.DINNER, LocalDateTime.now(), dishes);

        Set<ConstraintViolation<Meal>> violations = validator.validate(meal);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().contains("dishIds")));
    }
}
