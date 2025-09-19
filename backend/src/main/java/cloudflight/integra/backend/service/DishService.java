package cloudflight.integra.backend.service;

import cloudflight.integra.backend.model.dtos.DishDto;
import cloudflight.integra.backend.repository.initial.IDishRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DishService {
    private final IDishRepository repo;

    public DishService(IDishRepository repo) { this.repo = repo; }

    public DishDto create(DishDto dishDto) {
        dishDto.setId(UUID.randomUUID());
        return repo.save(dishDto);
    }

    public List<DishDto> getAll() { return repo.findAll(); }

    public DishDto getById(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Dish not found: " + id));
    }

    public DishDto update(UUID id, DishDto dishDto) {
        if (!repo.existsById(id)) throw new RuntimeException("Dish not found: " + id);
        dishDto.setId(id);
        return repo.save(dishDto);
    }

    public void delete(UUID id) {
        if (!repo.existsById(id)) throw new RuntimeException("Dish not found: " + id);
        repo.deleteById(id);
    }
}
