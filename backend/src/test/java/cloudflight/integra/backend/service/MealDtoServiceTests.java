package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exception.MealNotFoundException;
import cloudflight.integra.backend.model.Meal;
import cloudflight.integra.backend.model.dtos.DishDto;
import cloudflight.integra.backend.model.dtos.MealDto;
import cloudflight.integra.backend.model.mappers.MealMapper;
import cloudflight.integra.backend.model.MealType;
import cloudflight.integra.backend.repository.MealRepository;
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
public class MealDtoServiceTests {

    @Mock
    private MealRepository mealRepository;

    @Mock
    private MealMapper mealMapper;

    @InjectMocks
    private MealService mealService;

    private MealDto testMealDto;
    private Meal testMeal;
    private UUID testId;

    @BeforeEach
    void setup() {
        testId = UUID.randomUUID();
        testMealDto = createTestMealDto(MealType.BREAKFAST);
        testMeal = createTestMeal(MealType.BREAKFAST);
    }

    private MealDto createTestMealDto(MealType mealType) {
        DishDto dish = DishDto.builder()
                .id(UUID.randomUUID())
                .name("Test Dish")
                .build();

        return MealDto.builder()
                .id(testId)
                .mealType(mealType)
                .dateTime(LocalDateTime.now())
                .dishes(List.of(dish))
                .build();
    }

    private Meal createTestMeal(MealType mealType) {
        return Meal.builder()
                .id(testId)
                .mealType(mealType)
                .dateTime(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateMealSuccess() {
        DishDto dish = DishDto.builder()
                .id(UUID.randomUUID())
                .name("Test Dish")
                .build();

        MealDto inputDto = MealDto.builder()
                .id(null)
                .mealType(MealType.BREAKFAST)
                .dateTime(LocalDateTime.now())
                .dishes(List.of(dish))
                .build();

        MealDto expectedResult = createTestMealDto(MealType.BREAKFAST);

        when(mealMapper.toEntity(any(MealDto.class))).thenReturn(testMeal);
        when(mealRepository.save(any(Meal.class))).thenReturn(testMeal);
        when(mealMapper.toDto(any(Meal.class))).thenReturn(expectedResult);

        MealDto created = mealService.createMeal(inputDto);

        assertNotNull(created.getId());
        assertEquals(MealType.BREAKFAST, created.getMealType());
        verify(mealMapper).toEntity(any(MealDto.class));
        verify(mealRepository).save(any(Meal.class));
        verify(mealMapper).toDto(testMeal);
    }

    @Test
    void testCreateMealMissingMealType() {
        DishDto dish = DishDto.builder()
                .id(UUID.randomUUID())
                .name("Test Dish")
                .build();

        MealDto mealDto = MealDto.builder()
                .id(null)
                .mealType(null)
                .dateTime(LocalDateTime.now())
                .dishes(List.of(dish))
                .build();

        assertThrows(IllegalArgumentException.class, () -> mealService.createMeal(mealDto));
        verify(mealRepository, never()).save(any());
        verify(mealMapper, never()).toEntity(any());
    }

    @Test
    void testGetAllMeals() {
        List<Meal> meals = Arrays.asList(testMeal);
        List<MealDto> expectedDtos = Arrays.asList(testMealDto);

        when(mealRepository.findAll()).thenReturn(meals);
        when(mealMapper.toDto(testMeal)).thenReturn(testMealDto);

        List<MealDto> result = mealService.getAllMeals();

        assertEquals(1, result.size());
        assertEquals(testMealDto.getMealType(),
                     result.get(0)
                             .getMealType());
        verify(mealRepository).findAll();
        verify(mealMapper).toDto(testMeal);
    }

    @Test
    void testGetMealByIdSuccess() {
        when(mealRepository.findById(testId)).thenReturn(Optional.of(testMeal));
        when(mealMapper.toDto(testMeal)).thenReturn(testMealDto);

        MealDto found = mealService.getMealById(testId);

        assertEquals(testMealDto.getId(), found.getId());
        assertEquals(testMealDto.getMealType(), found.getMealType());
        verify(mealRepository).findById(testId);
        verify(mealMapper).toDto(testMeal);
    }

    @Test
    void testGetMealByIdNotFound() {
        UUID randomId = UUID.randomUUID();
        when(mealRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(MealNotFoundException.class, () -> mealService.getMealById(randomId));
        verify(mealRepository).findById(randomId);
        verify(mealMapper, never()).toDto(any());
    }

    @Test
    void testUpdateMealSuccess() {
        UUID id = UUID.randomUUID();

        DishDto dish = DishDto.builder()
                .id(UUID.randomUUID())
                .name("Updated Dish")
                .build();

        MealDto inputDto = MealDto.builder()
                .id(null)
                .mealType(MealType.LUNCH)
                .dateTime(LocalDateTime.now())
                .dishes(List.of(dish))
                .build();

        MealDto expectedResult = MealDto.builder()
                .id(id)
                .mealType(MealType.LUNCH)
                .dateTime(LocalDateTime.now())
                .dishes(List.of(dish))
                .build();

        Meal updatedMeal = createTestMeal(MealType.LUNCH);
        updatedMeal.setId(id);

        when(mealRepository.existsById(id)).thenReturn(true);
        when(mealMapper.toEntity(any(MealDto.class))).thenReturn(updatedMeal);
        when(mealRepository.save(updatedMeal)).thenReturn(updatedMeal);
        when(mealMapper.toDto(updatedMeal)).thenReturn(expectedResult);

        MealDto updated = mealService.updateMeal(id, inputDto);

        assertEquals(expectedResult.getId(), updated.getId());
        assertEquals(MealType.LUNCH, updated.getMealType());
        verify(mealRepository).existsById(id);
        verify(mealMapper).toEntity(any(MealDto.class));
        verify(mealRepository).save(updatedMeal);
        verify(mealMapper).toDto(updatedMeal);
    }

    @Test
    void testUpdateMealNotFound() {
        UUID id = UUID.randomUUID();

        DishDto dish = DishDto.builder()
                .id(UUID.randomUUID())
                .name("Test Dish")
                .build();

        MealDto mealDto = MealDto.builder()
                .id(id)
                .mealType(MealType.LUNCH)
                .dateTime(LocalDateTime.now())
                .dishes(List.of(dish))
                .build();

        when(mealRepository.existsById(id)).thenReturn(false);

        assertThrows(MealNotFoundException.class, () -> mealService.updateMeal(id, mealDto));
        verify(mealRepository).existsById(id);
        verify(mealRepository, never()).save(any());
        verify(mealMapper, never()).toEntity(any());
        verify(mealMapper, never()).toDto(any());
    }

    @Test
    void testDeleteMealSuccess() {
        UUID id = UUID.randomUUID();
        when(mealRepository.existsById(id)).thenReturn(true);
        doNothing().when(mealRepository)
                .deleteById(id);

        mealService.deleteMeal(id);

        verify(mealRepository).existsById(id);
        verify(mealRepository).deleteById(id);
    }

    @Test
    void testDeleteMealNotFound() {
        UUID id = UUID.randomUUID();
        when(mealRepository.existsById(id)).thenReturn(false);

        assertThrows(MealNotFoundException.class, () -> mealService.deleteMeal(id));
        verify(mealRepository).existsById(id);
        verify(mealRepository, never()).deleteById(any());
    }
}
