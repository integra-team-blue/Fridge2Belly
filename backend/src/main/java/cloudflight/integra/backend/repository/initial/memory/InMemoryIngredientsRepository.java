package cloudflight.integra.backend.repository.initial.memory;

import cloudflight.integra.backend.model.dtos.IngredientDto;
import cloudflight.integra.backend.repository.initial.IIngredientsRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryIngredientsRepository implements IIngredientsRepository {

    private final Map<UUID, IngredientDto> ingredientsStorage = new ConcurrentHashMap<>();

    @Override
    public List<IngredientDto> getAll() {
        return new ArrayList<>(ingredientsStorage.values());
    }

    @Override
    public IngredientDto getIngredient(UUID id) {
        return ingredientsStorage.get(id);
    }

    @Override
    public void create(IngredientDto ingredient) {
        ingredientsStorage.put(ingredient.getId(), ingredient);
    }

    @Override
    public void update(UUID id, IngredientDto ingredient) {
        ingredient.setId(id);
        ingredientsStorage.put(id, ingredient);
    }

    @Override
    public void delete(UUID id) {
        ingredientsStorage.remove(id);
    }
}

