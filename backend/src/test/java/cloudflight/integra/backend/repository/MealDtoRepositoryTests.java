package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.model.Meal;
import cloudflight.integra.backend.model.MealType;
import cloudflight.integra.backend.model.Dish;
import cloudflight.integra.backend.model.Ingredient;
import cloudflight.integra.backend.model.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class MealDtoRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    private Dish testDish1;
    private Dish testDish2;

    @BeforeEach
    void setup() {
        Ingredient ingredient = Ingredient.builder()
                .name("Test Ingredient")
                .quantity(100.0)
                .unit("grams")
                .calories(50.0)
                .protein(5.0)
                .fat(2.0)
                .carbohydrates(8.0)
                .expirationDate(LocalDate.now()
                        .plusDays(7))
                .build();
        ingredient = ingredientRepository.save(ingredient);

        Recipe recipe = Recipe.builder()
                .name("Test Recipe")
                .description("Test description")
                .cookingTimeMinutes(30)
                .instructions("Test instructions")
                .build();
        recipe = recipeRepository.save(recipe);

        testDish1 = Dish.builder()
                .name("Test Dish 1")
                .preparedAt(LocalDateTime.now())
                .calories(100.0)
                .protein(10.0)
                .fat(5.0)
                .carbohydrates(12.0)
                .ingredients(new ArrayList<>(Arrays.asList(ingredient)))
                .recipes(new ArrayList<>(Arrays.asList(recipe)))
                .build();
        testDish1 = dishRepository.save(testDish1);

        testDish2 = Dish.builder()
                .name("Test Dish 2")
                .preparedAt(LocalDateTime.now())
                .calories(150.0)
                .protein(15.0)
                .fat(8.0)
                .carbohydrates(18.0)
                .ingredients(new ArrayList<>(Arrays.asList(ingredient)))
                .recipes(new ArrayList<>(Arrays.asList(recipe)))
                .build();
        testDish2 = dishRepository.save(testDish2);
    }

    private Meal createSampleMeal(MealType mealType, List<Dish> dishes) {
        return Meal.builder()
                .mealType(mealType)
                .dateTime(LocalDateTime.now())
                .dishes(dishes)
                .build();
    }

    @Test
    void testSaveAndFindAllMeals() {
        Meal meal1 = createSampleMeal(MealType.BREAKFAST, new ArrayList<>(Arrays.asList(testDish1)));
        Meal meal2 = createSampleMeal(MealType.DINNER, new ArrayList<>(Arrays.asList(testDish2)));

        mealRepository.save(meal1);
        mealRepository.save(meal2);

        List<Meal> allMeals = (List<Meal>) mealRepository.findAll();

        assertEquals(2, allMeals.size());
        assertTrue(allMeals.stream()
                .anyMatch(m -> m.getMealType() == MealType.BREAKFAST));
        assertTrue(allMeals.stream()
                .anyMatch(m -> m.getMealType() == MealType.DINNER));
    }

    @Test
    void testFindById() {
        Meal meal = createSampleMeal(MealType.LUNCH, new ArrayList<>(Arrays.asList(testDish1)));
        Meal savedMeal = mealRepository.save(meal);

        Optional<Meal> foundMeal = mealRepository.findById(savedMeal.getId());

        assertTrue(foundMeal.isPresent());
        assertEquals(MealType.LUNCH,
                     foundMeal.get()
                             .getMealType());
        assertFalse(foundMeal.get()
                .getDishes()
                .isEmpty());
    }

    @Test
    void testFindByIdNotFound() {
        Optional<Meal> notFound = mealRepository.findById(java.util.UUID.randomUUID());
        assertTrue(notFound.isEmpty());
    }

    @Test
    void testDeleteById() {
        Meal meal = createSampleMeal(MealType.LUNCH, new ArrayList<>(Arrays.asList(testDish1)));
        Meal savedMeal = mealRepository.save(meal);

        assertTrue(mealRepository.existsById(savedMeal.getId()));

        mealRepository.deleteById(savedMeal.getId());

        assertFalse(mealRepository.existsById(savedMeal.getId()));
    }

    @Test
    void testExistsById() {
        Meal meal = createSampleMeal(MealType.DINNER, new ArrayList<>(Arrays.asList(testDish1)));
        Meal savedMeal = mealRepository.save(meal);

        assertTrue(mealRepository.existsById(savedMeal.getId()));
        assertFalse(mealRepository.existsById(java.util.UUID.randomUUID()));
    }

    @Test
    void testMealWithMultipleDishes() {
        Meal meal = createSampleMeal(MealType.DINNER, new ArrayList<>(Arrays.asList(testDish1, testDish2)));
        Meal savedMeal = mealRepository.save(meal);

        Optional<Meal> foundMeal = mealRepository.findById(savedMeal.getId());

        assertTrue(foundMeal.isPresent());
        assertEquals(2,
                     foundMeal.get()
                             .getDishes()
                             .size());
        assertTrue(foundMeal.get()
                .getDishes()
                .contains(testDish1));
        assertTrue(foundMeal.get()
                .getDishes()
                .contains(testDish2));
    }

    @Test
    void testUpdateMeal() {
        Meal meal = createSampleMeal(MealType.BREAKFAST, new ArrayList<>(Arrays.asList(testDish1)));
        Meal savedMeal = mealRepository.save(meal);

        savedMeal.setMealType(MealType.LUNCH);
        savedMeal.setDishes(new ArrayList<>(Arrays.asList(testDish1, testDish2)));

        Meal updatedMeal = mealRepository.save(savedMeal);

        assertEquals(MealType.LUNCH, updatedMeal.getMealType());
        assertEquals(2,
                     updatedMeal.getDishes()
                             .size());
    }
}
