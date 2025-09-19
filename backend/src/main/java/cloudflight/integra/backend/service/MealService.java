package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exception.MealNotFoundException;
import cloudflight.integra.backend.model.Meal;
import cloudflight.integra.backend.model.dtos.MealDto;
import cloudflight.integra.backend.model.mappers.MealMapper;
import cloudflight.integra.backend.repository.MealRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MealService {

    private final MealRepository repository;
    private final MealMapper mapper;

    public MealService(MealRepository repository, MealMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public MealDto createMeal(MealDto mealDto) {
        return mapper.toDto(repository.save(mapper.toEntity(mealDto)));
    }

    @Transactional
    public List<MealDto> getAllMeals() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public MealDto getMealById(UUID id) {
        return mapper.toDto(repository.getReferenceById(id));
    }

    @Transactional
    public MealDto updateMeal(UUID id, MealDto updatedMealDto) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Meal not found with id: " + id);
        }
        
        Meal meal = mapper.toEntity(updatedMealDto);
        meal.setId(id);

        return mapper.toDto(repository.save(meal));
    }

    @Transactional
    public void deleteMeal(UUID id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Meal not found with id: " + id);
        }
        
        repository.deleteById(id);
    }
}
