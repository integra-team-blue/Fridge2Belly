package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exception.IngredientsExeption;
import cloudflight.integra.backend.model.Ingredients;
import cloudflight.integra.backend.repository.RepositoryIngredients;
import cloudflight.integra.backend.validation.IngredientsValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class IngredientsService {

    private final RepositoryIngredients repository;
    private final IngredientsValidator validator;

    @Autowired
    public IngredientsService(RepositoryIngredients repository, IngredientsValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    public List<Ingredients> getAllIngredients() {
        return repository.getAll();
    }

    public Ingredients getIngredientById(UUID id) {
        validateId(id);

        Ingredients ingredient = repository.getIngredient(id);
        if (ingredient == null) {
            throw new IngredientsExeption("Ingredient not found with id: " + id);
        }

        return ingredient;
    }

    public Ingredients createIngredient(Ingredients ingredient) {
        validator.validateIngredient(ingredient);

        if (ingredient.getId() == null) {
            ingredient.setId(UUID.randomUUID());
        }

        repository.create(ingredient);
        return ingredient;
    }

    public Ingredients updateIngredient(UUID id, Ingredients ingredient) {
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

