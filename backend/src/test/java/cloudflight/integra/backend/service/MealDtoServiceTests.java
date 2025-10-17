package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exception.MealNotFoundException;
import cloudflight.integra.backend.model.Meal;
import cloudflight.integra.backend.model.dtos.DishDto;
import cloudflight.integra.backend.model.dtos.MealCreateDto;
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

    private MealCreateDto createTestMealCreateDto(MealType mealType) {
        return MealCreateDto.builder()
                .mealType(mealType)
                .dateTime(LocalDateTime.now())
                .dishIds(List.of(UUID.randomUUID()))
                .build();
    }

    @Test
    void testCreateMealSuccess() {
        MealCreateDto inputDto = createTestMealCreateDto(MealType.BREAKFAST);
        MealDto expectedResult = createTestMealDto(MealType.BREAKFAST);

        when(mealMapper.fromCreateDto(any(MealCreateDto.class))).thenReturn(testMeal);
        when(mealRepository.save(any(Meal.class))).thenReturn(testMeal);
        when(mealMapper.toDto(any(Meal.class))).thenReturn(expectedResult);

        MealDto created = mealService.createMeal(inputDto);

        assertNotNull(created.getId());
        assertEquals(MealType.BREAKFAST, created.getMealType());
        verify(mealMapper).fromCreateDto(any(MealCreateDto.class));
        verify(mealRepository).save(any(Meal.class));
        verify(mealMapper).toDto(testMeal);
    }

    @Test
    void testCreateMealMissingMealType() {
        MealCreateDto inputDto = MealCreateDto.builder()
                .dateTime(LocalDateTime.now())
                .dishIds(List.of(UUID.randomUUID()))
                .build();

        assertThrows(IllegalArgumentException.class, () -> mealService.createMeal(inputDto));

        verify(mealRepository, never()).save(any());
        verify(mealMapper, never()).fromCreateDto(any());
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

        MealCreateDto inputDto = MealCreateDto.builder()
                .mealType(MealType.LUNCH)
                .dateTime(LocalDateTime.now())
                .dishIds(List.of(UUID.randomUUID()))
                .build();

        Meal updatedMeal = Meal.builder()
                .id(id)
                .mealType(MealType.LUNCH)
                .dateTime(LocalDateTime.now())
                .build();

        MealDto expectedResult = MealDto.builder()
                .id(id)
                .mealType(MealType.LUNCH)
                .dateTime(LocalDateTime.now())
                .dishes(List.of(DishDto.builder()
                        .id(UUID.randomUUID())
                        .name("Updated Dish")
                        .build()))
                .build();

        when(mealRepository.existsById(id)).thenReturn(true);
        when(mealMapper.fromCreateDto(any(MealCreateDto.class))).thenReturn(updatedMeal);
        when(mealRepository.save(updatedMeal)).thenReturn(updatedMeal);
        when(mealMapper.toDto(updatedMeal)).thenReturn(expectedResult);

        MealDto updated = mealService.updateMeal(id, inputDto);

        assertEquals(expectedResult.getId(), updated.getId());
        assertEquals(MealType.LUNCH, updated.getMealType());
        verify(mealRepository).existsById(id);
        verify(mealMapper).fromCreateDto(any(MealCreateDto.class));
        verify(mealRepository).save(updatedMeal);
        verify(mealMapper).toDto(updatedMeal);
    }

    @Test
    void testUpdateMealNotFound() {
        UUID id = UUID.randomUUID();

        MealCreateDto inputDto = MealCreateDto.builder()
                .mealType(MealType.DINNER)
                .dateTime(LocalDateTime.now())
                .dishIds(List.of(UUID.randomUUID()))
                .build();

        when(mealRepository.existsById(id)).thenReturn(false);

        assertThrows(MealNotFoundException.class, () -> mealService.updateMeal(id, inputDto));

        verify(mealRepository).existsById(id);
        verify(mealMapper, never()).fromCreateDto(any());
        verify(mealRepository, never()).save(any());
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
