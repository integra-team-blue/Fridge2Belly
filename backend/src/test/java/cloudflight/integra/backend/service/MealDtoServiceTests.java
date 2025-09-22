package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exception.MealNotFoundException;
import cloudflight.integra.backend.model.dtos.MealDto;
import cloudflight.integra.backend.model.MealType;
import cloudflight.integra.backend.repository.initial.IMealRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MealDtoServiceTests {

    private IMealRepository mealRepository;
    private MealService mealService;

    @BeforeEach
    void setup() {
        mealRepository = Mockito.mock(IMealRepository.class);
        mealService = new MealService(mealRepository);
    }

    @Test
    void testCreateMealSuccess() {
        MealDto mealDto = new MealDto(null, MealType.BREAKFAST, LocalDateTime.now(), Arrays.asList(UUID.randomUUID()));
        when(mealRepository.save(mealDto)).thenAnswer(i -> {
            mealDto.setId(UUID.randomUUID());
            return mealDto;
        });

        MealDto created = mealService.createMeal(mealDto);

        assertNotNull(created.getId());
        assertEquals(MealType.BREAKFAST, created.getMealType());
        verify(mealRepository, times(1)).save(mealDto);
    }

    @Test
    void testCreateMealMissingMealType() {
        MealDto mealDto = new MealDto(null, null, LocalDateTime.now(), Arrays.asList(UUID.randomUUID()));
        assertThrows(IllegalArgumentException.class, () -> mealService.createMeal(mealDto));
        verify(mealRepository, never()).save(mealDto);
    }

    @Test
    void testGetAllMeals() {
        List<MealDto> mealDtos = Arrays.asList(
                                               new MealDto(UUID.randomUUID(),
                                                           MealType.LUNCH,
                                                           LocalDateTime.now(),
                                                           Arrays.asList(UUID.randomUUID())),
                                               new MealDto(UUID.randomUUID(),
                                                           MealType.DINNER,
                                                           LocalDateTime.now(),
                                                           Arrays.asList(UUID.randomUUID()))
        );
        when(mealRepository.findAll()).thenReturn(mealDtos);

        List<MealDto> result = mealService.getAllMeals();

        assertEquals(mealDtos, result);
        verify(mealRepository, times(1)).findAll();
    }

    @Test
    void testGetMealByIdSuccess() {
        MealDto mealDto = new MealDto(UUID.randomUUID(),
                                      MealType.DINNER,
                                      LocalDateTime.now(),
                                      Arrays.asList(UUID.randomUUID()));
        when(mealRepository.findById(mealDto.getId())).thenReturn(mealDto);

        MealDto found = mealService.getMealById(mealDto.getId());

        assertEquals(mealDto, found);
        verify(mealRepository, times(1)).findById(mealDto.getId());
    }

    @Test
    void testGetMealByIdNotFound() {
        UUID randomId = UUID.randomUUID();
        when(mealRepository.findById(randomId)).thenThrow(new MealNotFoundException("Meal not found"));

        assertThrows(MealNotFoundException.class, () -> mealService.getMealById(randomId));
        verify(mealRepository, times(1)).findById(randomId);
    }

    @Test
    void testUpdateMealSuccess() {
        UUID id = UUID.randomUUID();
        MealDto mealDto = new MealDto(id, MealType.LUNCH, LocalDateTime.now(), Arrays.asList(UUID.randomUUID()));
        when(mealRepository.existsById(id)).thenReturn(true);
        when(mealRepository.save(mealDto)).thenReturn(mealDto);

        MealDto updated = mealService.updateMeal(id, mealDto);

        assertEquals(mealDto, updated);
        verify(mealRepository, times(1)).existsById(id);
        verify(mealRepository, times(1)).save(mealDto);
    }

    @Test
    void testUpdateMealNotFound() {
        UUID id = UUID.randomUUID();
        MealDto mealDto = new MealDto(id, MealType.LUNCH, LocalDateTime.now(), Arrays.asList(UUID.randomUUID()));
        when(mealRepository.existsById(id)).thenReturn(false);

        assertThrows(MealNotFoundException.class, () -> mealService.updateMeal(id, mealDto));
        verify(mealRepository, times(1)).existsById(id);
        verify(mealRepository, never()).save(mealDto);
    }

    @Test
    void testDeleteMealSuccess() {
        UUID id = UUID.randomUUID();
        doNothing().when(mealRepository)
                .deleteById(id);

        mealService.deleteMeal(id);

        verify(mealRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteMealNotFound() {
        UUID id = UUID.randomUUID();
        doThrow(new MealNotFoundException("Meal not found")).when(mealRepository)
                .deleteById(id);

        assertThrows(MealNotFoundException.class, () -> mealService.deleteMeal(id));
        verify(mealRepository, times(1)).deleteById(id);
    }
}

