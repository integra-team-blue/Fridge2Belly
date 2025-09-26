package cloudflight.integra.backend.service;

import cloudflight.integra.backend.model.*;
import cloudflight.integra.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SampleDataService {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private MealRepository mealRepository;

    @Transactional
    public void generateAllSampleData() {
        clearExistingData();
        List<Ingredient> ingredients = generateIngredients();
        List<Recipe> recipes = generateRecipes();
        List<Dish> dishes = generateDishes(ingredients);
        linkRecipesToDishes(recipes, dishes);
        generateMeals(dishes);
    }

    // For deleting existing data to avoid duplication
    private void clearExistingData() {
        mealRepository.deleteAll();
        dishRepository.deleteAll();
        recipeRepository.deleteAll();
        ingredientRepository.deleteAll();
    }

    private List<Ingredient> generateIngredients() {
        List<Ingredient> ingredients = new ArrayList<>(Arrays.asList(
                Ingredient.builder()
                        .name("Chicken Breast")
                        .quantity(500.0)
                        .unit("grams")
                        .expirationDate(LocalDate.now().plusDays(3))
                        .calories(165.0)
                        .protein(31.0)
                        .fat(3.6)
                        .carbohydrates(0.0)
                        .build(),

                Ingredient.builder()
                        .name("Jasmine Rice")
                        .quantity(1000.0)
                        .unit("grams")
                        .expirationDate(LocalDate.now().plusMonths(6))
                        .calories(130.0)
                        .protein(2.7)
                        .fat(0.3)
                        .carbohydrates(28.0)
                        .build(),

                Ingredient.builder()
                        .name("Fresh Tomatoes")
                        .quantity(300.0)
                        .unit("grams")
                        .expirationDate(LocalDate.now().plusDays(5))
                        .calories(18.0)
                        .protein(0.9)
                        .fat(0.2)
                        .carbohydrates(3.9)
                        .build(),

                Ingredient.builder()
                        .name("Extra Virgin Olive Oil")
                        .quantity(250.0)
                        .unit("ml")
                        .expirationDate(LocalDate.now().plusMonths(12))
                        .calories(884.0)
                        .protein(0.0)
                        .fat(100.0)
                        .carbohydrates(0.0)
                        .build(),

                Ingredient.builder()
                        .name("Fresh Garlic")
                        .quantity(100.0)
                        .unit("grams")
                        .expirationDate(LocalDate.now().plusWeeks(3))
                        .calories(149.0)
                        .protein(6.4)
                        .fat(0.5)
                        .carbohydrates(33.0)
                        .build()
        ));

        ingredientRepository.saveAll(ingredients);
        return ingredients;
    }

    private List<Recipe> generateRecipes() {
        List<Recipe> recipes = new ArrayList<>(Arrays.asList(
                Recipe.builder()
                        .name("Garlic Chicken Rice")
                        .description("Tender chicken breast with aromatic garlic rice")
                        .cookingTimeMinutes(25)
                        .instructions("1. Season chicken with salt and pepper\n2. Heat olive oil in pan\n3. Cook chicken 6-7 minutes each side\n4. Cook rice separately with garlic\n5. Serve chicken over rice")
                        .build(),

                Recipe.builder()
                        .name("Mediterranean Tomato Chicken")
                        .description("Juicy chicken with fresh tomatoes and herbs")
                        .cookingTimeMinutes(30)
                        .instructions("1. Cut tomatoes into chunks\n2. Heat olive oil in skillet\n3. Brown chicken on both sides\n4. Add tomatoes and garlic\n5. Simmer until chicken is cooked through")
                        .build(),

                Recipe.builder()
                        .name("Simple Fried Rice")
                        .description("Quick and tasty fried rice with garlic")
                        .cookingTimeMinutes(15)
                        .instructions("1. Cook rice and let cool\n2. Heat oil in wok\n3. Add minced garlic\n4. Add rice and stir-fry\n5. Season to taste")
                        .build(),

                Recipe.builder()
                        .name("Tomato Garlic Saute")
                        .description("Fresh tomatoes sauteed with aromatic garlic")
                        .cookingTimeMinutes(10)
                        .instructions("1. Heat olive oil in pan\n2. Add minced garlic\n3. Add diced tomatoes\n4. Cook until tomatoes are soft\n5. Season with salt and pepper")
                        .build(),

                Recipe.builder()
                        .name("Herb Roasted Chicken")
                        .description("Perfectly seasoned roasted chicken breast")
                        .cookingTimeMinutes(35)
                        .instructions("1. Preheat oven to 375°F\n2. Season chicken generously\n3. Drizzle with olive oil\n4. Roast for 25-30 minutes\n5. Let rest before serving")
                        .build()
        ));

        recipeRepository.saveAll(recipes);
        return recipes;
    }

    private List<Dish> generateDishes(List<Ingredient> ingredients) {
        List<Dish> dishes = new ArrayList<>(Arrays.asList(
                Dish.builder()
                        .name("Grilled Chicken with Rice")
                        .preparedAt(LocalDateTime.now().minusHours(2))
                        .calories(450.0)
                        .protein(35.0)
                        .fat(8.0)
                        .carbohydrates(35.0)
                        .ingredients(new ArrayList<>(Arrays.asList(ingredients.get(0), ingredients.get(1), ingredients.get(3))))
                        .build(),

                Dish.builder()
                        .name("Mediterranean Chicken Bowl")
                        .preparedAt(LocalDateTime.now().minusHours(1))
                        .calories(380.0)
                        .protein(32.0)
                        .fat(12.0)
                        .carbohydrates(25.0)
                        .ingredients(new ArrayList<>(Arrays.asList(ingredients.get(0), ingredients.get(2), ingredients.get(3), ingredients.get(4))))
                        .build(),

                Dish.builder()
                        .name("Garlic Fried Rice")
                        .preparedAt(LocalDateTime.now().minusMinutes(30))
                        .calories(320.0)
                        .protein(6.0)
                        .fat(8.0)
                        .carbohydrates(58.0)
                        .ingredients(new ArrayList<>(Arrays.asList(ingredients.get(1), ingredients.get(3), ingredients.get(4))))
                        .build(),

                Dish.builder()
                        .name("Fresh Tomato Salad")
                        .preparedAt(LocalDateTime.now().minusMinutes(15))
                        .calories(120.0)
                        .protein(2.0)
                        .fat(8.0)
                        .carbohydrates(10.0)
                        .ingredients(new ArrayList<>(Arrays.asList(ingredients.get(2), ingredients.get(3), ingredients.get(4))))
                        .build(),

                Dish.builder()
                        .name("Herb Crusted Chicken")
                        .preparedAt(LocalDateTime.now().minusHours(3))
                        .calories(280.0)
                        .protein(45.0)
                        .fat(9.0)
                        .carbohydrates(2.0)
                        .ingredients(new ArrayList<>(Arrays.asList(ingredients.get(0), ingredients.get(3), ingredients.get(4))))
                        .build()
        ));

        dishRepository.saveAll(dishes);
        return dishes;
    }

    private void linkRecipesToDishes(List<Recipe> recipes, List<Dish> dishes) {
        try {
            recipes.get(0).setDishes(new ArrayList<>(Arrays.asList(dishes.get(0))));
            recipes.get(1).setDishes(new ArrayList<>(Arrays.asList(dishes.get(1))));
            recipes.get(2).setDishes(new ArrayList<>(Arrays.asList(dishes.get(2))));
            recipes.get(3).setDishes(new ArrayList<>(Arrays.asList(dishes.get(3))));
            recipes.get(4).setDishes(new ArrayList<>(Arrays.asList(dishes.get(4))));

            recipeRepository.saveAll(recipes);
        } catch (Exception e) {
            System.err.println("Error linking recipes to dishes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void generateMeals(List<Dish> dishes) {
        List<Meal> meals = new ArrayList<>(Arrays.asList(
                Meal.builder()
                        .mealType(MealType.BREAKFAST)
                        .dateTime(LocalDateTime.now().withHour(8).withMinute(0))
                        .dishes(new ArrayList<>(Arrays.asList(dishes.get(2), dishes.get(3)))) // Rice + Salad
                        .build(),

                Meal.builder()
                        .mealType(MealType.LUNCH)
                        .dateTime(LocalDateTime.now().withHour(12).withMinute(30))
                        .dishes(new ArrayList<>(Arrays.asList(dishes.get(0), dishes.get(3)))) // Chicken Rice + Salad
                        .build(),

                Meal.builder()
                        .mealType(MealType.DINNER)
                        .dateTime(LocalDateTime.now().withHour(19).withMinute(0))
                        .dishes(new ArrayList<>(Arrays.asList(dishes.get(1), dishes.get(2)))) // Mediterranean Bowl + Rice
                        .build(),

                Meal.builder()
                        .mealType(MealType.SNACK)
                        .dateTime(LocalDateTime.now().withHour(15).withMinute(30))
                        .dishes(new ArrayList<>(Arrays.asList(dishes.get(3)))) // Just Tomato Salad
                        .build(),

                Meal.builder()
                        .mealType(MealType.DINNER)
                        .dateTime(LocalDateTime.now().plusDays(1).withHour(18).withMinute(45))
                        .dishes(new ArrayList<>(Arrays.asList(dishes.get(4), dishes.get(2)))) // Herb Chicken + Rice
                        .build()
        ));

        mealRepository.saveAll(meals);
    }
}

