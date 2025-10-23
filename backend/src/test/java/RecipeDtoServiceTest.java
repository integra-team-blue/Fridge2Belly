import cloudflight.integra.backend.model.Recipe;
import cloudflight.integra.backend.model.dtos.RecipeDto;
import cloudflight.integra.backend.model.mappers.RecipeMapper;
import cloudflight.integra.backend.repository.RecipeRepository;
import cloudflight.integra.backend.service.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeDtoServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeMapper recipeMapper;

    @InjectMocks
    private RecipeService service;

    private RecipeDto testRecipeDto;
    private Recipe testRecipe;
    private UUID testId;

    @BeforeEach
    void setup() {
        testId = UUID.randomUUID();
        testRecipeDto = createTestRecipeDto("ServiceTest");
        testRecipe = createTestRecipe("ServiceTest");
    }

    private RecipeDto createTestRecipeDto(String name) {
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setId(testId);
        recipeDto.setName(name);
        recipeDto.setCookingTimeMinutes(10);
        recipeDto.setInstructions("Test");
        recipeDto.setDishId(null);
        return recipeDto;
    }

    private Recipe createTestRecipe(String name) {
        return Recipe.builder()
                .id(testId)
                .name(name)
                .cookingTimeMinutes(10)
                .instructions("Test")
                .build();
    }

    @Test
    void testCreateAndGetRecipe() {
        when(recipeMapper.toEntity(any(RecipeDto.class))).thenReturn(testRecipe);
        when(recipeRepository.save(any(Recipe.class))).thenReturn(testRecipe);
        when(recipeMapper.toDto(any(Recipe.class))).thenReturn(testRecipeDto);
        when(recipeRepository.findById(testId)).thenReturn(Optional.of(testRecipe));

        RecipeDto saved = service.createRecipe(testRecipeDto);

        assertNotNull(saved.getId());
        assertEquals("ServiceTest",
                     service.getRecipe(saved.getId())
                             .getName());
    }

    @Test
    void testUpdateRecipe() {
        RecipeDto oldDto = createTestRecipeDto("Old");
        oldDto.setCookingTimeMinutes(5);
        oldDto.setInstructions("Old");

        RecipeDto newDto = createTestRecipeDto("New");
        newDto.setCookingTimeMinutes(15);
        newDto.setInstructions("New");

        Recipe existingRecipe = createTestRecipe("Old");
        Recipe updatedRecipe = createTestRecipe("New");

        when(recipeRepository.findById(testId)).thenReturn(Optional.of(existingRecipe));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(updatedRecipe);
        when(recipeMapper.toDto(updatedRecipe)).thenReturn(newDto);

        RecipeDto updated = service.updateRecipe(testId, newDto);

        assertEquals("New", updated.getName());
        assertEquals(15, updated.getCookingTimeMinutes());
    }
}
