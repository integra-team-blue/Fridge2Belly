package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.model.Ingredients;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryRepositoryIngredients implements RepositoryIngredients {

    private final Map<UUID, Ingredients> ingredientsStorage = new ConcurrentHashMap<>();

    @Override
    public List<Ingredients> getAll() {
        return new ArrayList<>(ingredientsStorage.values());
    }

    @Override
    public Ingredients getIngredient(UUID id) {
        return ingredientsStorage.get(id);
    }

    @Override
    public void create(Ingredients ingredient) {
        if (ingredient.getId() == null) {
            ingredient.setId(UUID.randomUUID());
        }
        ingredientsStorage.put(ingredient.getId(), ingredient);
    }

    @Override
    public void update(UUID id, Ingredients ingredient) {
        ingredient.setId(id);
        ingredientsStorage.put(id, ingredient);
    }

    @Override
    public void delete(UUID id) {
        ingredientsStorage.remove(id);
    }
}
