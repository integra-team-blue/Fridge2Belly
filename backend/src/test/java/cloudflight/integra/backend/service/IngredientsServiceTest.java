package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exception.IngredientsExeption;
import cloudflight.integra.backend.model.Ingredients;
import cloudflight.integra.backend.repository.RepositoryIngredients;
import cloudflight.integra.backend.validation.IngredientsValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngredientsServiceTest {

    @Mock
    private RepositoryIngredients repository;

    @Mock
    private IngredientsValidator validator;

    @InjectMocks
    private IngredientsService ingredientsService;

    private Ingredients testIngredient;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testIngredient = new Ingredients();
        testIngredient.setId(testId);
        testIngredient.setName("Test Tomato");
        testIngredient.setQuantity(2.5);
        testIngredient.setUnit("kg");
        testIngredient.setExpirationDate(LocalDate.now().plusDays(7));
        testIngredient.setCalories(18.0);
        testIngredient.setProtein(0.9);
        testIngredient.setFat(0.2);
        testIngredient.setCarbohydrates(3.9);
    }



    @Test
    void getAllIngredients_ShouldReturnEmptyList_WhenNoIngredients() {
        
        when(repository.getAll()).thenReturn(Collections.emptyList());

        
        List<Ingredients> result = ingredientsService.getAllIngredients();

        
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).getAll();
    }

    @Test
    void getAllIngredients_ShouldReturnAllIngredients_WhenIngredientsExist() {
        
        List<Ingredients> expectedIngredients = Arrays.asList(testIngredient);
        when(repository.getAll()).thenReturn(expectedIngredients);

        
        List<Ingredients> result = ingredientsService.getAllIngredients();

        
        assertEquals(expectedIngredients, result);
        assertEquals(1, result.size());
        verify(repository).getAll();
    }



    @Test
    void getIngredientById_ShouldReturnIngredient_WhenIngredientExists() {
       
        when(repository.getIngredient(testId)).thenReturn(testIngredient);

        
        Ingredients result = ingredientsService.getIngredientById(testId);

        
        assertEquals(testIngredient, result);
        assertEquals(testId, result.getId());
        assertEquals("Test Tomato", result.getName());
        verify(repository).getIngredient(testId);
    }

    @Test
    void getIngredientById_ShouldThrowException_WhenIngredientNotFound() {
        
        when(repository.getIngredient(testId)).thenReturn(null);

        
        IngredientsExeption exception = assertThrows(IngredientsExeption.class,
                () -> ingredientsService.getIngredientById(testId));

        assertEquals("Ingredient not found with id: " + testId, exception.getMessage());
        verify(repository).getIngredient(testId);
    }

    @Test
    void getIngredientById_ShouldThrowException_WhenIdIsNull() {
        
        IngredientsExeption exception = assertThrows(IngredientsExeption.class,
                () -> ingredientsService.getIngredientById(null));

        assertEquals("ID cannot be null", exception.getMessage());
        verify(repository, never()).getIngredient(any());
    }



    @Test
    void createIngredient_ShouldReturnCreatedIngredient_WhenValidIngredient() {
        
        doNothing().when(validator).validateIngredient(testIngredient);
        doNothing().when(repository).create(testIngredient);

        
        Ingredients result = ingredientsService.createIngredient(testIngredient);

        
        assertEquals(testIngredient, result);
        verify(validator).validateIngredient(testIngredient);
        verify(repository).create(testIngredient);
    }

    @Test
    void createIngredient_ShouldThrowException_WhenValidationFails() {
        
        doThrow(new IngredientsExeption("Name is required"))
                .when(validator).validateIngredient(testIngredient);

        
        IngredientsExeption exception = assertThrows(IngredientsExeption.class,
                () -> ingredientsService.createIngredient(testIngredient));

        assertEquals("Name is required", exception.getMessage());
        verify(validator).validateIngredient(testIngredient);
        verify(repository, never()).create(any());
    }



    @Test
    void updateIngredient_ShouldReturnUpdatedIngredient_WhenIngredientExists() {
        
        when(repository.getIngredient(testId)).thenReturn(testIngredient);
        doNothing().when(validator).validateIngredient(testIngredient);
        doNothing().when(repository).update(testId, testIngredient);

        
        Ingredients result = ingredientsService.updateIngredient(testId, testIngredient);

        
        assertEquals(testIngredient, result);
        verify(repository).getIngredient(testId);
        verify(validator).validateIngredient(testIngredient);
        verify(repository).update(testId, testIngredient);
    }

    @Test
    void updateIngredient_ShouldThrowException_WhenIngredientNotFound() {
        
        when(repository.getIngredient(testId)).thenReturn(null);

        
        IngredientsExeption exception = assertThrows(IngredientsExeption.class,
                () -> ingredientsService.updateIngredient(testId, testIngredient));

        assertEquals("Ingredient not found with id: " + testId, exception.getMessage());
        verify(repository).getIngredient(testId);
        verify(validator, never()).validateIngredient(any());
        verify(repository, never()).update(any(), any());
    }

    @Test
    void updateIngredient_ShouldThrowException_WhenIdIsNull() {
        
        IngredientsExeption exception = assertThrows(IngredientsExeption.class,
                () -> ingredientsService.updateIngredient(null, testIngredient));

        assertEquals("ID cannot be null", exception.getMessage());
        verify(repository, never()).getIngredient(any());
        verify(validator, never()).validateIngredient(any());
        verify(repository, never()).update(any(), any());
    }

    @Test
    void updateIngredient_ShouldThrowException_WhenValidationFails() {
        
        when(repository.getIngredient(testId)).thenReturn(testIngredient);
        doThrow(new IngredientsExeption("Quantity must be positive"))
                .when(validator).validateIngredient(testIngredient);

        
        IngredientsExeption exception = assertThrows(IngredientsExeption.class,
                () -> ingredientsService.updateIngredient(testId, testIngredient));

        assertEquals("Quantity must be positive", exception.getMessage());
        verify(repository).getIngredient(testId);
        verify(validator).validateIngredient(testIngredient);
        verify(repository, never()).update(any(), any());
    }



    @Test
    void deleteIngredient_ShouldDeleteSuccessfully_WhenIngredientExists() {
        
        when(repository.getIngredient(testId)).thenReturn(testIngredient);
        doNothing().when(repository).delete(testId);

        
        assertDoesNotThrow(() -> ingredientsService.deleteIngredient(testId));

        
        verify(repository).getIngredient(testId);
        verify(repository).delete(testId);
    }

    @Test
    void deleteIngredient_ShouldThrowException_WhenIngredientNotFound() {
        
        when(repository.getIngredient(testId)).thenReturn(null);

        
        IngredientsExeption exception = assertThrows(IngredientsExeption.class,
                () -> ingredientsService.deleteIngredient(testId));

        assertEquals("Ingredient not found with id: " + testId, exception.getMessage());
        verify(repository).getIngredient(testId);
        verify(repository, never()).delete(any());
    }

    @Test
    void deleteIngredient_ShouldThrowException_WhenIdIsNull() {
        
        IngredientsExeption exception = assertThrows(IngredientsExeption.class,
                () -> ingredientsService.deleteIngredient(null));

        assertEquals("ID cannot be null", exception.getMessage());
        verify(repository, never()).getIngredient(any());
        verify(repository, never()).delete(any());
    }


}