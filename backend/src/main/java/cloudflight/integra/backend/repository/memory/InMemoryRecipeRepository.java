package cloudflight.integra.backend.repository.memory;

import cloudflight.integra.backend.model.Recipe;
import cloudflight.integra.backend.repository.RecipeRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryRecipeRepository implements RecipeRepository {

    private final Map<UUID, Recipe> storage = new ConcurrentHashMap<>();

    @Override
    public Recipe save(Recipe recipe) {
        if (recipe.getId() == null) {
            recipe.setId(UUID.randomUUID());
        }
        storage.put(recipe.getId(), recipe);
        return recipe;
    }

    @Override
    public Optional<Recipe> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Recipe> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteById(UUID id) {
        storage.remove(id);
    }
}
