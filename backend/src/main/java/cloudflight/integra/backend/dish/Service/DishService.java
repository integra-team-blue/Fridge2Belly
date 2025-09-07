package cloudflight.integra.backend.dish.Service;

import cloudflight.integra.backend.dish.Model.Dish;
import cloudflight.integra.backend.dish.Repository.DishRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DishService {
    private final DishRepository repo;

    public DishService(DishRepository repo) { this.repo = repo; }

    public Dish create(Dish dish) {
        dish.setId(UUID.randomUUID());
        return repo.save(dish);
    }

    public List<Dish> getAll() { return repo.findAll(); }

    public Dish getById(UUID id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Dish not found: " + id));
    }

    public Dish update(UUID id, Dish dish) {
        if (!repo.existsById(id)) throw new RuntimeException("Dish not found: " + id);
        dish.setId(id);
        return repo.save(dish);
    }

    public void delete(UUID id) {
        if (!repo.existsById(id)) throw new RuntimeException("Dish not found: " + id);
        repo.deleteById(id);
    }
}
