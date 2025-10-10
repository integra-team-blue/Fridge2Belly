package cloudflight.integra.backend.model;

import cloudflight.integra.backend.model.dtos.DishDto;
import cloudflight.integra.backend.model.dtos.MealDto;
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
        DishDto dish = DishDto.builder()
                .id(UUID.randomUUID())
                .name("Test Dish")
                .build();

        MealDto mealDto = MealDto.builder()
                .id(UUID.randomUUID())
                .mealType(MealType.BREAKFAST)
                .dateTime(LocalDateTime.now())
                .dishes(List.of(dish))
                .build();

        Set<ConstraintViolation<MealDto>> violations = validator.validate(mealDto);
        assertEquals(0, violations.size());
    }

    @Test
    void testMealValidationFail_DishesNull() {
        MealDto mealDto = MealDto.builder()
                .id(UUID.randomUUID())
                .mealType(MealType.DINNER)
                .dateTime(LocalDateTime.now())
                .dishes(null)
                .build();

        Set<ConstraintViolation<MealDto>> violations = validator.validate(mealDto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath()
                        .toString()
                        .equals("dishes")));
    }

    @Test
    void testMealValidationFail_DishesEmpty() {
        List<DishDto> dishes = new ArrayList<>();

        MealDto mealDto = MealDto.builder()
                .id(UUID.randomUUID())
                .mealType(MealType.DINNER)
                .dateTime(LocalDateTime.now())
                .dishes(dishes)
                .build();

        Set<ConstraintViolation<MealDto>> violations = validator.validate(mealDto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath()
                        .toString()
                        .equals("dishes")));
    }

}
