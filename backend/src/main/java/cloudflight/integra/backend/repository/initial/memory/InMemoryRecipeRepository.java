package cloudflight.integra.backend.repository.initial.memory;

import cloudflight.integra.backend.model.dtos.RecipeDto;
import cloudflight.integra.backend.repository.initial.IRecipeRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryRecipeRepository implements IRecipeRepository {

    private final Map<UUID, RecipeDto> storage = new ConcurrentHashMap<>();

    @Override
    public RecipeDto save(RecipeDto recipeDto) {
        if (recipeDto.getId() == null) {
            recipeDto.setId(UUID.randomUUID());
        }
        storage.put(recipeDto.getId(), recipeDto);
        return recipeDto;
    }

    @Override
    public Optional<RecipeDto> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<RecipeDto> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteById(UUID id) {
        storage.remove(id);
    }
}
