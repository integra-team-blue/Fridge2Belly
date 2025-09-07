import cloudflight.integra.backend.model.Recipe;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RecipeValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidRecipe() {
        Recipe recipe = new Recipe();
        recipe.setName("Pizza");
        recipe.setDescription("Classic Italian");
        recipe.setCookingTimeMinutes(20);
        recipe.setInstructions("Bake in oven");
        recipe.setIngredientsId(List.of(UUID.randomUUID()));

        Set violations = validator.validate(recipe);
        assertTrue(violations.isEmpty(), "Recipe should be valid");
    }

    @Test
    void testInvalidRecipe_NoName() {
        Recipe recipe = new Recipe();
        recipe.setCookingTimeMinutes(10);
        recipe.setInstructions("Bake");
        recipe.setIngredientsId(List.of());

        Set violations = validator.validate(recipe);
        assertFalse(violations.isEmpty(), "Recipe without name should be invalid");
    }
}