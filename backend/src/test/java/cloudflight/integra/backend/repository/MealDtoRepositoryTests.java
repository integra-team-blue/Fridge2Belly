package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.exception.MealNotFoundException;
import cloudflight.integra.backend.model.dtos.MealDto;
import cloudflight.integra.backend.model.MealType;
import cloudflight.integra.backend.repository.initial.memory.InMemoryMealRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class MealDtoRepositoryTests {

    private InMemoryMealRepository repository;

    @BeforeEach
    void setup() {
        repository = new InMemoryMealRepository();
    }

    @Test
    void testSaveAndFindAllMeals() {
        UUID mealId1 = UUID.randomUUID();
        UUID mealId2 = UUID.randomUUID();
        List<UUID> dishes1 = new ArrayList<>();
        dishes1.add(UUID.randomUUID());
        List<UUID> dishes2 = new ArrayList<>();
        dishes2.add(UUID.randomUUID());

        MealDto mealDto1 = new MealDto(mealId1, MealType.BREAKFAST, LocalDateTime.now(), dishes1);
        MealDto mealDto2 = new MealDto(mealId2, MealType.DINNER, LocalDateTime.now(), dishes2);

        repository.save(mealDto1);
        repository.save(mealDto2);

        List<MealDto> allMealDtos = repository.findAll();
        assertTrue(allMealDtos.contains(mealDto1));
        assertTrue(allMealDtos.contains(mealDto2));
        assertTrue(allMealDtos.size() >= 2);
    }

    @Test
    void testFindById() {
        MealDto existingMealDto = repository.findAll()
                .get(0);
        MealDto found = repository.findById(existingMealDto.getId());
        assertEquals(existingMealDto, found);
    }

    @Test
    void testFindByIdNotFound() {
        UUID randomId = UUID.randomUUID();
        assertThrows(MealNotFoundException.class, () -> repository.findById(randomId));
    }

    @Test
    void testDeleteById() {
        MealDto mealDto = new MealDto(UUID.randomUUID(),
                                      MealType.LUNCH,
                                      LocalDateTime.now(),
                                      List.of(UUID.randomUUID()));
        repository.save(mealDto);

        assertTrue(repository.existsById(mealDto.getId()));
        repository.deleteById(mealDto.getId());
        assertFalse(repository.existsById(mealDto.getId()));
    }

    @Test
    void testExistsById() {
        MealDto mealDto = new MealDto(UUID.randomUUID(),
                                      MealType.DINNER,
                                      LocalDateTime.now(),
                                      List.of(UUID.randomUUID()));
        repository.save(mealDto);

        assertTrue(repository.existsById(mealDto.getId()));
        assertFalse(repository.existsById(UUID.randomUUID()));
    }
}

