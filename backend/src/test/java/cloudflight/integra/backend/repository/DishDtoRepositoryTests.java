package cloudflight.integra.backend.repository;

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
class DishDtoRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    private Ingredient testIngredient;
    private Recipe testRecipe;

    @BeforeEach
    void setup() {
        testIngredient = Ingredient.builder()
                .name("Test Ingredient")
                .quantity(100.0)
                .unit("grams")
                .calories(50.0)
                .protein(5.0)
                .fat(2.0)
                .carbohydrates(8.0)
                .expirationDate(LocalDate.now().plusDays(7))
                .build();
        testIngredient = ingredientRepository.save(testIngredient);

        testRecipe = Recipe.builder()
                .name("Test Recipe")
                .description("Test description")
                .cookingTimeMinutes(30)
                .instructions("Test instructions")
                .build();
        testRecipe = recipeRepository.save(testRecipe);
    }

    private Dish createSampleDish(String name) {
        return Dish.builder()
                .name(name)
                .preparedAt(LocalDateTime.now())
                .calories(100.0)
                .protein(10.0)
                .fat(5.0)
                .carbohydrates(12.0)
                .ingredients(new ArrayList<>(Arrays.asList(testIngredient)))
                .recipes(new ArrayList<>(Arrays.asList(testRecipe)))
                .build();
    }

    @Test
    void save_and_findById() {
        Dish dish = createSampleDish("Pasta");
        Dish savedDish = dishRepository.save(dish);

        Optional<Dish> foundDish = dishRepository.findById(savedDish.getId());

        assertTrue(foundDish.isPresent());
        assertEquals("Pasta", foundDish.get().getName());
        assertEquals(100.0, foundDish.get().getCalories());
        assertFalse(foundDish.get().getIngredients().isEmpty());
        assertFalse(foundDish.get().getRecipes().isEmpty());
    }

    @Test
    void findAll_returnsAll() {
        dishRepository.save(createSampleDish("Dish A"));
        dishRepository.save(createSampleDish("Dish B"));

        List<Dish> allDishes = dishRepository.findAll();

        assertEquals(2, allDishes.size());
        assertTrue(allDishes.stream().anyMatch(d -> d.getName().equals("Dish A")));
        assertTrue(allDishes.stream().anyMatch(d -> d.getName().equals("Dish B")));
    }

    @Test
    void deleteById_removes() {
        Dish dish = createSampleDish("ToRemove");
        Dish savedDish = dishRepository.save(dish);

        assertTrue(dishRepository.existsById(savedDish.getId()));

        dishRepository.deleteById(savedDish.getId());

        assertFalse(dishRepository.existsById(savedDish.getId()));
        Optional<Dish> deletedDish = dishRepository.findById(savedDish.getId());
        assertTrue(deletedDish.isEmpty());
    }

    @Test
    void existsById_returnsCorrectStatus() {
        Dish dish = createSampleDish("ExistenceTest");
        Dish savedDish = dishRepository.save(dish);

        assertTrue(dishRepository.existsById(savedDish.getId()));

        dishRepository.deleteById(savedDish.getId());

        assertFalse(dishRepository.existsById(savedDish.getId()));
    }

    @Test
    void updateDish_savesChanges() {
        Dish dish = createSampleDish("Original Name");
        Dish savedDish = dishRepository.save(dish);

        savedDish.setName("Updated Name");
        savedDish.setCalories(200.0);

        Dish updatedDish = dishRepository.save(savedDish);

        assertEquals("Updated Name", updatedDish.getName());
        assertEquals(200.0, updatedDish.getCalories());
    }
}
