package cloudflight.integra.backend.model;

import cloudflight.integra.backend.model.dtos.MealDto;
import cloudflight.integra.backend.model.MealType;
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

public class MealDtoTests {

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
        MealDto mealDto = new MealDto(UUID.randomUUID(), MealType.BREAKFAST, LocalDateTime.now(), dishes);

        Set<ConstraintViolation<MealDto>> violations = validator.validate(mealDto);
        assertEquals(0, violations.size());
    }

    @Test
    void testMealValidationFail_MealTypeNull() {
        List<UUID> dishes = new ArrayList<>();
        dishes.add(UUID.randomUUID());
        MealDto mealDto = new MealDto(UUID.randomUUID(), null, LocalDateTime.now(), dishes);

        Set<ConstraintViolation<MealDto>> violations = validator.validate(mealDto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("mealType")));
    }

    @Test
    void testMealValidationFail_DateTimeNull() {
        List<UUID> dishes = new ArrayList<>();
        dishes.add(UUID.randomUUID());
        MealDto mealDto = new MealDto(UUID.randomUUID(), MealType.LUNCH, null, dishes);

        Set<ConstraintViolation<MealDto>> violations = validator.validate(mealDto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("dateTime")));
    }

    @Test
    void testMealValidationFail_DishIdsNull() {
        MealDto mealDto = new MealDto(UUID.randomUUID(), MealType.DINNER, LocalDateTime.now(), null);

        Set<ConstraintViolation<MealDto>> violations = validator.validate(mealDto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("dishIds")));
    }

    @Test
    void testMealValidationFail_DishIdsEmpty() {
        List<UUID> dishes = new ArrayList<>();
        MealDto mealDto = new MealDto(UUID.randomUUID(), MealType.DINNER, LocalDateTime.now(), dishes);

        Set<ConstraintViolation<MealDto>> violations = validator.validate(mealDto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("dishIds")));
    }

    @Test
    void testMealValidationFail_DishIdElementNull() {
        List<UUID> dishes = new ArrayList<>();
        dishes.add(null);
        MealDto mealDto = new MealDto(UUID.randomUUID(), MealType.DINNER, LocalDateTime.now(), dishes);

        Set<ConstraintViolation<MealDto>> violations = validator.validate(mealDto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().contains("dishIds")));
    }
}
