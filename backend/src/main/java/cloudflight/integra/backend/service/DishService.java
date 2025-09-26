package cloudflight.integra.backend.service;

import cloudflight.integra.backend.model.Dish;
import cloudflight.integra.backend.model.dtos.DishDto;
import cloudflight.integra.backend.model.mappers.DishMapper;
import cloudflight.integra.backend.repository.DishRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DishService {
    private final DishRepository dishRepository;
    private final DishMapper dishMapper;

    public DishService(DishRepository dishRepository, DishMapper dishMapper) {
        this.dishRepository = dishRepository;
        this.dishMapper = dishMapper;
    }

    public DishDto create(DishDto dishDto) {
        Dish dish = dishMapper.toEntity(dishDto);
        Dish savedDish = dishRepository.save(dish);
        return dishMapper.toDto(savedDish);
    }

    @Transactional
    public List<DishDto> getAll() {
        return dishRepository.findAll()
                .stream()
                .map(dishMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public DishDto getById(UUID id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dish not found: " + id));
        return dishMapper.toDto(dish);
    }

    @Transactional
    public DishDto update(UUID id, DishDto dishDto) {
        if (!dishRepository.existsById(id)) {
            throw new RuntimeException("Dish not found: " + id);
        }
        dishDto.setId(id);
        Dish dish = dishMapper.toEntity(dishDto);
        Dish savedDish = dishRepository.save(dish);
        return dishMapper.toDto(savedDish);
    }

    public void delete(UUID id) {
        if (!dishRepository.existsById(id)) {
            throw new RuntimeException("Dish not found: " + id);
        }
        dishRepository.deleteById(id);
    }
}

