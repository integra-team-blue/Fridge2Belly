package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exception.DishNotFoundException;
import cloudflight.integra.backend.model.Dish;
import cloudflight.integra.backend.model.dtos.DishCreateDto;
import cloudflight.integra.backend.model.dtos.DishDto;
import cloudflight.integra.backend.model.dtos.IngredientDto;
import cloudflight.integra.backend.model.dtos.RecipeDto;
import cloudflight.integra.backend.model.mappers.DishMapper;
import cloudflight.integra.backend.repository.DishRepository;
import cloudflight.integra.backend.repository.MealRepository;
import cloudflight.integra.backend.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DishDtoServiceTests {

    @Mock
    private DishRepository dishRepository;

    @Mock
    private DishMapper dishMapper;

    @Mock
    private MealRepository mealRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private DishService dishService;

    private DishDto testDishDto;
    private Dish testDish;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testDishDto = createTestDishDto("Test Dish");
        testDish = createTestDish("Test Dish");
    }

    private DishDto createTestDishDto(String name) {
        RecipeDto recipe = RecipeDto.builder()
                .id(UUID.randomUUID())
                .name("Test Recipe")
                .build();

        IngredientDto ingredient = IngredientDto.builder()
                .id(UUID.randomUUID())
                .name("Test Ingredient")
                .build();

        DishDto d = new DishDto();
        d.setId(testId);
        d.setName(name);
        d.setRecipes(List.of(recipe));
        d.setIngredients(List.of(ingredient));
        d.setPreparedAt(LocalDateTime.now());
        d.setCalories(120);
        d.setProtein(7);
        d.setFat(3);
        d.setCarbohydrates(15);
        return d;
    }

    private Dish createTestDish(String name) {
        return Dish.builder()
                .id(testId)
                .name(name)
                .preparedAt(LocalDateTime.now())
                .calories(120.0)
                .protein(7.0)
                .fat(3.0)
                .carbohydrates(15.0)
                .build();
    }

    private DishCreateDto createTestDishCreateDto(String name) {
        return DishCreateDto.builder()
                .name(name)
                .preparedAt(LocalDateTime.now())
                .calories(100)
                .protein(5)
                .fat(2)
                .carbohydrates(10)
                .recipeIds(List.of(UUID.randomUUID()))
                .ingredientIds(List.of(UUID.randomUUID()))
                .build();
    }


    @Test
    void create_setsId_andPersists() {
        DishCreateDto inputDto = createTestDishCreateDto("Salad");

        DishDto expectedDto = createTestDishDto("Salad");
        expectedDto.setId(testId);

        when(dishMapper.fromCreateDto(any(DishCreateDto.class))).thenReturn(testDish);
        when(dishRepository.save(any(Dish.class))).thenReturn(testDish);
        when(dishMapper.toDto(any(Dish.class))).thenReturn(expectedDto);

        DishDto created = dishService.create(inputDto);

        assertNotNull(created.getId());
        assertEquals("Salad", created.getName());
        verify(dishMapper).fromCreateDto(inputDto);
        verify(dishRepository).save(any(Dish.class));
        verify(dishMapper).toDto(testDish);
    }

    @Test
    void getAll_returnsAllDishes() {
        List<Dish> dishes = Arrays.asList(testDish);
        List<DishDto> expectedDtos = Arrays.asList(testDishDto);

        when(dishRepository.findAll()).thenReturn(dishes);
        when(dishMapper.toDto(testDish)).thenReturn(testDishDto);

        List<DishDto> result = dishService.getAll();

        assertEquals(1, result.size());
        assertEquals(testDishDto.getName(),
                     result.get(0)
                             .getName());
        verify(dishRepository).findAll();
        verify(dishMapper).toDto(testDish);
    }

    @Test
    void getById_returnsOrThrows() {
        when(dishRepository.findById(testId)).thenReturn(Optional.of(testDish));
        when(dishMapper.toDto(testDish)).thenReturn(testDishDto);

        DishDto result = dishService.getById(testId);

        assertEquals("Test Dish", result.getName());
        verify(dishRepository).findById(testId);
        verify(dishMapper).toDto(testDish);

        UUID missingId = UUID.randomUUID();
        when(dishRepository.findById(missingId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> dishService.getById(missingId));
    }

    @Test
    void delete_removes() {
        when(dishRepository.existsById(testId)).thenReturn(true);

        when(mealRepository.findAll()).thenReturn(List.of());
        when(recipeRepository.findAll()).thenReturn(List.of());

        dishService.delete(testId);

        verify(dishRepository).existsById(testId);
        verify(mealRepository).findAll();
        verify(recipeRepository).findAll();
        verify(dishRepository).deleteById(testId);
    }

    @Test
    void delete_throwsWhenNotFound() {
        when(dishRepository.existsById(testId)).thenReturn(false);

        DishNotFoundException exception = assertThrows(
                                                       DishNotFoundException.class,
                                                       () -> dishService.delete(testId)
        );

        assertEquals("Dish not found with id: " + testId, exception.getMessage());

        verify(dishRepository).existsById(testId);
        verify(dishRepository, never()).deleteById(any());
    }
}

