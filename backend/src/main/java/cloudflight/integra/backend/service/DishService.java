package cloudflight.integra.backend.service;

import cloudflight.integra.backend.model.Dish;
import cloudflight.integra.backend.model.dtos.DishDto;
import cloudflight.integra.backend.model.mappers.DishMapper;
import cloudflight.integra.backend.repository.DishRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DishService {
    private final DishRepository repo;
    private final DishMapper mapper;

    public DishService(DishRepository repo, DishMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Transactional
    public DishDto create(DishDto dishDto) {
        Dish dish = mapper.toEntity(dishDto);
        dish.setId(UUID.randomUUID());
        return mapper.toDto(repo.save(dish));
    }

    @Transactional
    public List<DishDto> getAll() { 
        return repo.findAll().stream().map(mapper::toDto).collect(Collectors.toList()); 
    }

    @Transactional
    public DishDto getById(UUID id) {
        return mapper.toDto(repo.findById(id).orElseThrow(() -> new RuntimeException("Dish not found: " + id)));
    }

    @Transactional
    public DishDto update(UUID id, DishDto dishDto) {
        if (!repo.existsById(id)) throw new RuntimeException("Dish not found: " + id);
        Dish dish = mapper.toEntity(dishDto);
        dish.setId(id);
        return mapper.toDto(repo.save(dish));
    }

    @Transactional
    public void delete(UUID id) {
        if (!repo.existsById(id)) throw new RuntimeException("Dish not found: " + id);
        repo.deleteById(id);
    }
}
