import cloudflight.integra.backend.model.dtos.RecipeDto;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RecipeDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidRecipe() {
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setName("Pizza");
        recipeDto.setDescription("Classic Italian");
        recipeDto.setCookingTimeMinutes(20);
        recipeDto.setInstructions("Bake in oven");
        recipeDto.setDishIds(List.of(UUID.randomUUID()));

        Set violations = validator.validate(recipeDto);
        assertTrue(violations.isEmpty(), "Recipe should be valid");
    }

    @Test
    void testInvalidRecipe_NoName() {
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setCookingTimeMinutes(10);
        recipeDto.setInstructions("Bake");
        recipeDto.setDishIds(List.of());

        Set violations = validator.validate(recipeDto);
        assertFalse(violations.isEmpty(), "Recipe without name should be invalid");
    }
}
