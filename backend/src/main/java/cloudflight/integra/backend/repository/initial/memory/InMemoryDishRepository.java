package cloudflight.integra.backend.repository.initial.memory;

import cloudflight.integra.backend.repository.initial.IDishRepository;
import cloudflight.integra.backend.model.dtos.DishDto;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryDishRepository implements IDishRepository {
    private final Map<UUID, DishDto> store = new ConcurrentHashMap<>();

    @Override
    public DishDto save(DishDto dishDto) {
        store.put(dishDto.getId(), dishDto);
        return dishDto;
    }

    @Override
    public Optional<DishDto> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<DishDto> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteById(UUID id) {
        store.remove(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return store.containsKey(id);
    }
}
