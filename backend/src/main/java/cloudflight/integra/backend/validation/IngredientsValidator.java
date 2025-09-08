package cloudflight.integra.backend.validation;

import cloudflight.integra.backend.exception.IngredientsExeption;
import cloudflight.integra.backend.model.Ingredients;
import org.springframework.stereotype.Component;

@Component
public class IngredientsValidator {

    public void validateIngredient(Ingredients ingredient) {
        if (ingredient == null) {
            throw new IngredientsExeption("Ingredient cannot be null");
        }

        validateName(ingredient.getName());
        validateQuantity(ingredient.getQuantity());
        validateUnit(ingredient.getUnit());
        validateNutritionalValues(ingredient);
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IngredientsExeption("Ingredient name is required");
        }

        if (name.length() > 100) {
            throw new IngredientsExeption("Ingredient name cannot exceed 100 characters");
        }
    }

    private void validateQuantity(Double quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IngredientsExeption("Quantity must be positive");
        }

        if (quantity > 10000) {
            throw new IngredientsExeption("Quantity cannot exceed 10000");
        }
    }

    private void validateUnit(String unit) {
        if (unit == null || unit.trim().isEmpty()) {
            throw new IngredientsExeption("Unit is required");
        }


        String[] validUnits = {"kg", "g", "l", "ml", "pieces", "cups", "tbsp", "tsp"};
        boolean isValidUnit = false;
        for (String validUnit : validUnits) {
            if (validUnit.equalsIgnoreCase(unit.trim())) {
                isValidUnit = true;
                break;
            }
        }

        if (!isValidUnit) {
            throw new IngredientsExeption("Invalid unit. Accepted units: kg, g, l, ml, pieces, cups, tbsp, tsp");
        }
    }

    private void validateNutritionalValues(Ingredients ingredient) {
        if (ingredient.getCalories() != null && ingredient.getCalories() < 0) {
            throw new IngredientsExeption("Calories cannot be negative");
        }

        if (ingredient.getProtein() != null && ingredient.getProtein() < 0) {
            throw new IngredientsExeption("Protein cannot be negative");
        }

        if (ingredient.getFat() != null && ingredient.getFat() < 0) {
            throw new IngredientsExeption("Fat cannot be negative");
        }

        if (ingredient.getCarbohydrates() != null && ingredient.getCarbohydrates() < 0) {
            throw new IngredientsExeption("Carbohydrates cannot be negative");
        }

    }


}

