package cloudflight.integra.backend.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DishModelTests {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void init() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void cleanup() {
        factory.close();
    }

    private Dish validDish() {
        Dish d = new Dish();
        d.setName("Pasta");
        d.setRecipeId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        d.setPreparedAt(LocalDateTime.parse("2025-01-01T12:00:00"));
        d.setCalories(100);
        d.setProtein(10);
        d.setFat(5);
        d.setCarbohydrates(12);
        // id may be null initially (server generates it)
        return d;
    }

    // Happy path
    @Test
    void validDish_hasNoViolations() {
        Dish d = validDish();
        Set<ConstraintViolation<Dish>> violations = validator.validate(d);
        assertThat(violations).isEmpty();
    }

    // Field-by-field validation
    @Test
    void name_blank_isViolation() {
        Dish d = validDish();
        d.setName("  ");
        Set<ConstraintViolation<Dish>> violations = validator.validate(d);
        assertThat(violations).anySatisfy(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("name");
        });
    }

    @Test
    void recipeId_null_isViolation() {
        Dish d = validDish();
        d.setRecipeId(null);
        Set<ConstraintViolation<Dish>> violations = validator.validate(d);
        assertThat(violations).anySatisfy(v ->
                assertThat(v.getPropertyPath().toString()).isEqualTo("recipeId")
        );
    }

    @Test
    void preparedAt_null_isViolation() {
        Dish d = validDish();
        d.setPreparedAt(null);
        Set<ConstraintViolation<Dish>> violations = validator.validate(d);
        assertThat(violations).anySatisfy(v ->
                assertThat(v.getPropertyPath().toString()).isEqualTo("preparedAt")
        );
    }

    @Test
    void calories_negative_isViolation() {
        Dish d = validDish();
        d.setCalories(-1);
        Set<ConstraintViolation<Dish>> violations = validator.validate(d);
        assertThat(violations).anySatisfy(v ->
                assertThat(v.getPropertyPath().toString()).isEqualTo("calories")
        );
    }

    @Test
    void protein_negative_isViolation() {
        Dish d = validDish();
        d.setProtein(-0.1);
        Set<ConstraintViolation<Dish>> violations = validator.validate(d);
        assertThat(violations).anySatisfy(v ->
                assertThat(v.getPropertyPath().toString()).isEqualTo("protein")
        );
    }

    @Test
    void fat_negative_isViolation() {
        Dish d = validDish();
        d.setFat(-5);
        Set<ConstraintViolation<Dish>> violations = validator.validate(d);
        assertThat(violations).anySatisfy(v ->
                assertThat(v.getPropertyPath().toString()).isEqualTo("fat")
        );
    }

    @Test
    void carbohydrates_negative_isViolation() {
        Dish d = validDish();
        d.setCarbohydrates(-3);
        Set<ConstraintViolation<Dish>> violations = validator.validate(d);
        assertThat(violations).anySatisfy(v ->
                assertThat(v.getPropertyPath().toString()).isEqualTo("carbohydrates")
        );
    }

    @Test
    void zeros_for_macros_are_allowed() {
        Dish d = validDish();
        d.setCalories(0);
        d.setProtein(0);
        d.setFat(0);
        d.setCarbohydrates(0);
        Set<ConstraintViolation<Dish>> violations = validator.validate(d);
        assertThat(violations).isEmpty();
    }

    // Basic getters/setters
    @Test
    void getters_setters_roundtrip() {
        Dish d = new Dish();
        UUID id = UUID.randomUUID();
        UUID recipeId = UUID.randomUUID();
        LocalDateTime ts = LocalDateTime.parse("2025-01-01T12:00:00");

        d.setId(id);
        d.setName("Burger");
        d.setRecipeId(recipeId);
        d.setPreparedAt(ts);
        d.setCalories(800);
        d.setProtein(40);
        d.setFat(45);
        d.setCarbohydrates(60);

        assertThat(d.getId()).isEqualTo(id);
        assertThat(d.getName()).isEqualTo("Burger");
        assertThat(d.getRecipeId()).isEqualTo(recipeId);
        assertThat(d.getPreparedAt()).isEqualTo(ts);
        assertThat(d.getCalories()).isEqualTo(800);
        assertThat(d.getProtein()).isEqualTo(40);
        assertThat(d.getFat()).isEqualTo(45);
        assertThat(d.getCarbohydrates()).isEqualTo(60);
    }
}
