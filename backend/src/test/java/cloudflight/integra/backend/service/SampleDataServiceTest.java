package cloudflight.integra.backend.service;

import cloudflight.integra.backend.model.*;
import cloudflight.integra.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SampleDataServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private DishRepository dishRepository;

    @Mock
    private MealRepository mealRepository;


    @InjectMocks
    private SampleDataService sampleDataService;

    private List<Ingredient> mockIngredients;
    private List<Recipe> mockRecipes;
    private List<Dish> mockDishes;
    private List<Meal> mockMeals;

    @BeforeEach
    void setUp() {
        mockIngredients = createMockIngredients();
        mockRecipes = createMockRecipes();
        mockDishes = createMockDishes();
        mockMeals = createMockMeals();
    }

    @Test
    void generateAllSampleData_success() {
        when(ingredientRepository.saveAll(anyList())).thenReturn(mockIngredients);
        when(recipeRepository.saveAll(anyList())).thenReturn(mockRecipes);
        when(dishRepository.saveAll(anyList())).thenReturn(mockDishes);
        when(mealRepository.saveAll(anyList())).thenReturn(mockMeals);

        assertDoesNotThrow(() -> sampleDataService.generateAllSampleData());

        verify(ingredientRepository).deleteAll();
        verify(recipeRepository).deleteAll();
        verify(dishRepository).deleteAll();
        verify(mealRepository).deleteAll();

        verify(ingredientRepository).saveAll(argThat(ingredients -> ((Collection<?>) ingredients).size() == 5));
        verify(recipeRepository, times(2)).saveAll(anyList());
        verify(dishRepository).saveAll(argThat(dishes -> ((Collection<?>) dishes).size() == 5));
        verify(mealRepository).saveAll(argThat(meals -> ((Collection<?>) meals).size() == 5));
    }

    @Test
    void generateAllSampleData_clearsExistingData() {
        when(ingredientRepository.saveAll(anyList())).thenReturn(mockIngredients);
        when(recipeRepository.saveAll(anyList())).thenReturn(mockRecipes);
        when(dishRepository.saveAll(anyList())).thenReturn(mockDishes);
        when(mealRepository.saveAll(anyList())).thenReturn(mockMeals);

        sampleDataService.generateAllSampleData();

        verify(mealRepository).deleteAll();
        verify(dishRepository).deleteAll();
        verify(recipeRepository).deleteAll();
        verify(ingredientRepository).deleteAll();
    }

    @Test
    void generateAllSampleData_savesDataInCorrectOrder() {
        when(ingredientRepository.saveAll(anyList())).thenReturn(mockIngredients);
        when(recipeRepository.saveAll(anyList())).thenReturn(mockRecipes);
        when(dishRepository.saveAll(anyList())).thenReturn(mockDishes);
        when(mealRepository.saveAll(anyList())).thenReturn(mockMeals);

        sampleDataService.generateAllSampleData();

        var inOrder = inOrder(ingredientRepository, recipeRepository, dishRepository, mealRepository);
        inOrder.verify(ingredientRepository).saveAll(anyList());
        inOrder.verify(recipeRepository).saveAll(anyList());
        inOrder.verify(dishRepository).saveAll(anyList());
        inOrder.verify(recipeRepository).saveAll(anyList());
        inOrder.verify(mealRepository).saveAll(anyList());
    }

    @Test
    void generateAllSampleData_ingredientRepositoryThrowsException_propagatesException() {
        when(ingredientRepository.saveAll(anyList())).thenThrow(new RuntimeException("Database error"));
        assertThrows(RuntimeException.class, () -> sampleDataService.generateAllSampleData());
    }


    @Test
    void generateAllSampleData_verifyIngredientGeneration() {
        when(ingredientRepository.saveAll(anyList())).thenReturn(mockIngredients);
        when(recipeRepository.saveAll(anyList())).thenReturn(mockRecipes);
        when(dishRepository.saveAll(anyList())).thenReturn(mockDishes);
        when(mealRepository.saveAll(anyList())).thenReturn(mockMeals);

        sampleDataService.generateAllSampleData();

        verify(ingredientRepository).saveAll(argThat(ingredients -> {
            List<Ingredient> ingredientList = new ArrayList<>((Collection<Ingredient>) ingredients);
            return ingredientList.size() == 5 &&
                    ingredientList.stream().anyMatch(i -> i.getName().equals("Chicken Breast")) &&
                    ingredientList.stream().anyMatch(i -> i.getUnit().equals("grams")) &&
                    ingredientList.stream().allMatch(i -> i.getCalories() != null);
        }));
    }


    private List<Ingredient> createMockIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ingredients.add(Ingredient.builder().name("ingredient" + i).build());
        }
        return ingredients;
    }

    private List<Recipe> createMockRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            recipes.add(Recipe.builder().name("recipe" + i).build());
        }
        return recipes;
    }

    private List<Dish> createMockDishes() {
        List<Dish> dishes = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            dishes.add(Dish.builder().name("dish" + i).build());
        }
        return dishes;
    }

    private List<Meal> createMockMeals() {
        List<Meal> meals = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            meals.add(Meal.builder().mealType(MealType.BREAKFAST).build());
        }
        return meals;
    }
}