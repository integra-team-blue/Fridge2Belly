package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exception.IngredientsExeption;
import cloudflight.integra.backend.model.Ingredient;
import cloudflight.integra.backend.model.dtos.IngredientDto;
import cloudflight.integra.backend.model.mappers.IngredientMapper;
import cloudflight.integra.backend.repository.DishRepository;
import cloudflight.integra.backend.repository.IngredientRepository;
import cloudflight.integra.backend.validation.IngredientsValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngredientDtoServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private IngredientsValidator validator;

    @Mock
    private IngredientMapper ingredientMapper;

    @Mock
    private DishRepository dishRepository;

    @InjectMocks
    private IngredientService ingredientService;

    @Test
    void getAllIngredients_ShouldReturnEmptyList_WhenNoIngredients() {
        when(ingredientRepository.findAll()).thenReturn(Collections.emptyList());


        List<IngredientDto> result = ingredientService.getAllIngredients();


        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(ingredientRepository).findAll();
    }

    @Test
    void getAllIngredients_ShouldReturnAllIngredients_WhenIngredientsExist() {

        List<Ingredient> ingredients = Arrays.asList(
                                                     createTestIngredient("Ingredient 1"),
                                                     createTestIngredient("Ingredient 2")
        );
        List<IngredientDto> expectedDtos = Arrays.asList(
                                                         createTestIngredientDto("Ingredient 1"),
                                                         createTestIngredientDto("Ingredient 2")
        );

        when(ingredientRepository.findAll()).thenReturn(ingredients);
        when(ingredientMapper.toDto(ingredients.get(0))).thenReturn(expectedDtos.get(0));
        when(ingredientMapper.toDto(ingredients.get(1))).thenReturn(expectedDtos.get(1));


        List<IngredientDto> result = ingredientService.getAllIngredients();


        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedDtos.get(0)
                .getName(),
                     result.get(0)
                             .getName());
        assertEquals(expectedDtos.get(1)
                .getName(),
                     result.get(1)
                             .getName());
        verify(ingredientRepository).findAll();
        verify(ingredientMapper, times(2)).toDto(any(Ingredient.class));
    }

    @Test
    void getIngredientById_ShouldReturnIngredient_WhenIngredientExists() {

        UUID id = UUID.randomUUID();
        Ingredient ingredient = createTestIngredient("Test Ingredient");
        ingredient.setId(id);
        IngredientDto expectedDto = createTestIngredientDto("Test Ingredient");
        expectedDto.setId(id);

        when(ingredientRepository.findById(id)).thenReturn(Optional.of(ingredient));
        when(ingredientMapper.toDto(ingredient)).thenReturn(expectedDto);


        IngredientDto result = ingredientService.getIngredientById(id);


        assertNotNull(result);
        assertEquals(expectedDto.getName(), result.getName());
        assertEquals(id, result.getId());
        verify(ingredientRepository).findById(id);
        verify(ingredientMapper).toDto(ingredient);
    }

    @Test
    void getIngredientById_ShouldThrowException_WhenIngredientNotFound() {

        UUID id = UUID.randomUUID();
        when(ingredientRepository.findById(id)).thenReturn(Optional.empty());


        IngredientsExeption exception = assertThrows(IngredientsExeption.class,
                                                     () -> ingredientService.getIngredientById(id));

        assertEquals("Ingredient not found with id: " + id, exception.getMessage());
        verify(ingredientRepository).findById(id);
        verify(ingredientMapper, never()).toDto(any());
    }

    @Test
    void createIngredient_ShouldReturnCreatedIngredient_WhenValidIngredient() {

        IngredientDto inputDto = createTestIngredientDto("New Ingredient");
        inputDto.setId(null);

        Ingredient entityToSave = createTestIngredient("New Ingredient");
        Ingredient savedEntity = createTestIngredient("New Ingredient");
        savedEntity.setId(UUID.randomUUID());

        IngredientDto expectedDto = createTestIngredientDto("New Ingredient");
        expectedDto.setId(savedEntity.getId());

        doNothing().when(validator)
                .validateIngredient(inputDto);
        when(ingredientMapper.toEntity(any(IngredientDto.class))).thenReturn(entityToSave);
        when(ingredientRepository.save(entityToSave)).thenReturn(savedEntity);
        when(ingredientMapper.toDto(savedEntity)).thenReturn(expectedDto);


        IngredientDto result = ingredientService.createIngredient(inputDto);


        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("New Ingredient", result.getName());
        verify(validator).validateIngredient(any(IngredientDto.class));
        verify(ingredientMapper).toEntity(any(IngredientDto.class));
        verify(ingredientRepository).save(entityToSave);
        verify(ingredientMapper).toDto(savedEntity);
    }

    @Test
    void updateIngredient_ShouldReturnUpdatedIngredient_WhenIngredientExists() {

        UUID id = UUID.randomUUID();
        IngredientDto updateDto = createTestIngredientDto("Updated Ingredient");

        Ingredient entityToSave = createTestIngredient("Updated Ingredient");
        entityToSave.setId(id);
        Ingredient savedEntity = createTestIngredient("Updated Ingredient");
        savedEntity.setId(id);

        IngredientDto expectedDto = createTestIngredientDto("Updated Ingredient");
        expectedDto.setId(id);

        when(ingredientRepository.existsById(id)).thenReturn(true);
        doNothing().when(validator)
                .validateIngredient(any(IngredientDto.class));
        when(ingredientMapper.toEntity(any(IngredientDto.class))).thenReturn(entityToSave);
        when(ingredientRepository.save(entityToSave)).thenReturn(savedEntity);
        when(ingredientMapper.toDto(savedEntity)).thenReturn(expectedDto);


        IngredientDto result = ingredientService.updateIngredient(id, updateDto);


        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Updated Ingredient", result.getName());
        verify(ingredientRepository).existsById(id);
        verify(validator).validateIngredient(any(IngredientDto.class));
        verify(ingredientMapper).toEntity(any(IngredientDto.class));
        verify(ingredientRepository).save(entityToSave);
        verify(ingredientMapper).toDto(savedEntity);
    }

    @Test
    void updateIngredient_ShouldThrowException_WhenIngredientNotFound() {
        UUID id = UUID.randomUUID();
        IngredientDto updateDto = createTestIngredientDto("Updated Ingredient");

        when(ingredientRepository.existsById(id)).thenReturn(false);

        IngredientsExeption exception = assertThrows(IngredientsExeption.class,
                                                     () -> ingredientService.updateIngredient(id, updateDto));

        assertEquals("Ingredient not found with id: " + id, exception.getMessage());
        verify(ingredientRepository).existsById(id);
        verify(validator, never()).validateIngredient(any());
        verify(ingredientRepository, never()).save(any());
    }

    @Test
    void updateIngredient_ShouldThrowException_WhenValidationFails() {
        UUID id = UUID.randomUUID();
        IngredientDto updateDto = createTestIngredientDto("Invalid Ingredient");

        when(ingredientRepository.existsById(id)).thenReturn(true);
        doThrow(new IngredientsExeption("Validation failed")).when(validator)
                .validateIngredient(any(IngredientDto.class));

        IngredientsExeption exception = assertThrows(IngredientsExeption.class,
                                                     () -> ingredientService.updateIngredient(id, updateDto));

        assertEquals("Validation failed", exception.getMessage());
        verify(ingredientRepository).existsById(id);
        verify(validator).validateIngredient(any(IngredientDto.class));
        verify(ingredientRepository, never()).save(any());
    }

    @Test
    void deleteIngredient_ShouldDeleteSuccessfully_WhenIngredientExists() {
        UUID id = UUID.randomUUID();

        when(ingredientRepository.existsById(id)).thenReturn(true);

        when(dishRepository.findAll()).thenReturn(List.of());

        assertDoesNotThrow(() -> ingredientService.deleteIngredient(id));

        verify(ingredientRepository).existsById(id);
        verify(dishRepository).findAll();
        verify(ingredientRepository).deleteById(id);
    }

    @Test
    void deleteIngredient_ShouldThrowException_WhenIngredientNotFound() {
        UUID id = UUID.randomUUID();

        when(ingredientRepository.existsById(id)).thenReturn(false);

        IngredientsExeption exception = assertThrows(
                IngredientsExeption.class,
                () -> ingredientService.deleteIngredient(id)
        );

        assertEquals("Ingredient not found with id: " + id, exception.getMessage());

        verify(ingredientRepository).existsById(id);
        verify(ingredientRepository, never()).deleteById(any());
    }

    // Helper methods
    private Ingredient createTestIngredient(String name) {
        return Ingredient.builder()
                .name(name)
                .quantity(100.0)
                .unit("grams")
                .calories(50.0)
                .protein(5.0)
                .fat(2.0)
                .carbohydrates(8.0)
                .expirationDate(LocalDate.now()
                        .plusDays(7))
                .build();
    }

    private IngredientDto createTestIngredientDto(String name) {
        return IngredientDto.builder()
                .name(name)
                .quantity(100.0)
                .unit("grams")
                .calories(50.0)
                .protein(5.0)
                .fat(2.0)
                .carbohydrates(8.0)
                .expirationDate(LocalDate.now()
                        .plusDays(7))
                .build();
    }
}
