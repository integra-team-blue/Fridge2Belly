package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exception.IngredientsExeption;
import cloudflight.integra.backend.model.dtos.IngredientDto;
import cloudflight.integra.backend.repository.initial.IIngredientsRepository;
import cloudflight.integra.backend.validation.IngredientsValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class IngredientsService {

    private final IIngredientsRepository repository;
    private final IngredientsValidator validator;

    @Autowired
    public IngredientsService(IIngredientsRepository repository, IngredientsValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    public List<IngredientDto> getAllIngredients() { return repository.getAll(); }

    public IngredientDto getIngredientById(UUID id) {
        validateId(id);

        IngredientDto ingredient = repository.getIngredient(id);
        if (ingredient == null) {
            throw new IngredientsExeption("Ingredient not found with id: " + id);
        }

        return ingredient;
    }

    public IngredientDto createIngredient(IngredientDto ingredient) {
        validator.validateIngredient(ingredient);

        if (ingredient.getId() == null) {
            ingredient.setId(UUID.randomUUID());
        }

        repository.create(ingredient);
        return ingredient;
    }

    public IngredientDto updateIngredient(UUID id, IngredientDto ingredient) {
        validateId(id);

        if (!ingredientExists(id)) {
            throw new IngredientsExeption("Ingredient not found with id: " + id);
        }

        validator.validateIngredient(ingredient);
        repository.update(id, ingredient);
        return ingredient;
    }

    public void deleteIngredient(UUID id) {
        validateId(id);

        if (!ingredientExists(id)) {
            throw new IngredientsExeption("Ingredient not found with id: " + id);
        }

        repository.delete(id);
    }

    private void validateId(UUID id) {
        if (id == null) {
            throw new IngredientsExeption("ID cannot be null");
        }
    }

    private boolean ingredientExists(UUID id) {
        return repository.getIngredient(id) != null;
    }
}

