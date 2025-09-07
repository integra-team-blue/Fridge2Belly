package cloudflight.integra.backend.dish.Repository;

import cloudflight.integra.backend.dish.Model.Dish;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryDishRepository implements DishRepository {
    private final Map<UUID, Dish> store = new ConcurrentHashMap<>();

    @Override public Dish save(Dish dish) { store.put(dish.getId(), dish); return dish; }
    @Override public Optional<Dish> findById(UUID id) { return Optional.ofNullable(store.get(id)); }
    @Override public List<Dish> findAll() { return new ArrayList<>(store.values()); }
    @Override public void deleteById(UUID id) { store.remove(id); }
    @Override public boolean existsById(UUID id) { return store.containsKey(id); }
}
