package cloudflight.integra.backend.model;

import cloudflight.integra.backend.model.dtos.DishDto;
import cloudflight.integra.backend.model.dtos.RecipeDto;
import cloudflight.integra.backend.model.dtos.IngredientDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DishDtoModelTests {

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

    private DishDto validDish() {
        RecipeDto recipe = RecipeDto.builder()
                .id(UUID.randomUUID())
                .name("Test Recipe")
                .build();

        IngredientDto ingredient = IngredientDto.builder()
                .id(UUID.randomUUID())
                .name("Test Ingredient")
                .build();

        DishDto d = new DishDto();
        d.setName("Pasta");
        d.setRecipes(List.of(recipe));
        d.setPreparedAt(LocalDateTime.parse("2025-01-01T12:00:00"));
        d.setIngredients(List.of(ingredient));
        d.setCalories(100);
        d.setProtein(10);
        d.setFat(5);
        d.setCarbohydrates(12);
        return d;
    }

    @Test
    void validDish_hasNoViolations() {
        DishDto d = validDish();
        Set<ConstraintViolation<DishDto>> violations = validator.validate(d);
        assertThat(violations).isEmpty();
    }

    @Test
    void name_blank_isViolation() {
        DishDto d = validDish();
        d.setName("  ");
        Set<ConstraintViolation<DishDto>> violations = validator.validate(d);
        assertThat(violations).anySatisfy(v -> {
            assertThat(v.getPropertyPath()
                    .toString()).isEqualTo("name");
        });
    }

    @Test
    void recipes_null_isViolation() {
        DishDto d = validDish();
        d.setRecipes(null);
        Set<ConstraintViolation<DishDto>> violations = validator.validate(d);
        assertThat(violations)
                .noneMatch(v -> v.getPropertyPath()
                        .toString()
                        .equals("recipes"));
    }

    @Test
    void preparedAt_null_isViolation() {
        DishDto d = validDish();
        d.setPreparedAt(null);
        Set<ConstraintViolation<DishDto>> violations = validator.validate(d);
        assertThat(violations).anySatisfy(v -> assertThat(v.getPropertyPath()
                .toString()).isEqualTo("preparedAt")
        );
    }

    @Test
    void calories_negative_isViolation() {
        DishDto d = validDish();
        d.setCalories(-1);
        Set<ConstraintViolation<DishDto>> violations = validator.validate(d);
        assertThat(violations).anySatisfy(v -> assertThat(v.getPropertyPath()
                .toString()).isEqualTo("calories")
        );
    }

    @Test
    void protein_negative_isViolation() {
        DishDto d = validDish();
        d.setProtein(-0.1);
        Set<ConstraintViolation<DishDto>> violations = validator.validate(d);
        assertThat(violations).anySatisfy(v -> assertThat(v.getPropertyPath()
                .toString()).isEqualTo("protein")
        );
    }

    @Test
    void fat_negative_isViolation() {
        DishDto d = validDish();
        d.setFat(-5);
        Set<ConstraintViolation<DishDto>> violations = validator.validate(d);
        assertThat(violations).anySatisfy(v -> assertThat(v.getPropertyPath()
                .toString()).isEqualTo("fat")
        );
    }

    @Test
    void carbohydrates_negative_isViolation() {
        DishDto d = validDish();
        d.setCarbohydrates(-3);
        Set<ConstraintViolation<DishDto>> violations = validator.validate(d);
        assertThat(violations).anySatisfy(v -> assertThat(v.getPropertyPath()
                .toString()).isEqualTo("carbohydrates")
        );
    }

    @Test
    void zeros_for_macros_are_allowed() {
        DishDto d = validDish();
        d.setCalories(0);
        d.setProtein(0);
        d.setFat(0);
        d.setCarbohydrates(0);
        Set<ConstraintViolation<DishDto>> violations = validator.validate(d);
        assertThat(violations).isEmpty();
    }

    @Test
    void getters_setters_roundtrip() {
        DishDto d = new DishDto();
        UUID id = UUID.randomUUID();
        UUID recipeId = UUID.randomUUID();
        LocalDateTime ts = LocalDateTime.parse("2025-01-01T12:00:00");

        RecipeDto recipe = RecipeDto.builder()
                .id(recipeId)
                .name("Test Recipe")
                .build();

        d.setId(id);
        d.setName("Burger");
        d.setRecipes(List.of(recipe));
        d.setPreparedAt(ts);
        d.setCalories(800);
        d.setProtein(40);
        d.setFat(45);
        d.setCarbohydrates(60);

        assertThat(d.getId()).isEqualTo(id);
        assertThat(d.getName()).isEqualTo("Burger");
        assertThat(d.getRecipes()).hasSize(1);
        assertThat(d.getRecipes()
                .get(0)
                .getId()).isEqualTo(recipeId);
        assertThat(d.getPreparedAt()).isEqualTo(ts);
        assertThat(d.getCalories()).isEqualTo(800);
        assertThat(d.getProtein()).isEqualTo(40);
        assertThat(d.getFat()).isEqualTo(45);
        assertThat(d.getCarbohydrates()).isEqualTo(60);
    }
}
