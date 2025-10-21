package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exception.MealNotFoundException;
import cloudflight.integra.backend.model.Meal;
import cloudflight.integra.backend.model.dtos.MealCreateDto;
import cloudflight.integra.backend.model.dtos.MealDto;
import cloudflight.integra.backend.model.mappers.MealMapper;
import cloudflight.integra.backend.repository.MealRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Service
public class MealService {

    private final MealRepository mealRepository;
    private final MealMapper mealMapper;

    @Autowired
    public MealService(MealRepository mealRepository, MealMapper mealMapper) {
        this.mealRepository = mealRepository;
        this.mealMapper = mealMapper;
    }

    @Transactional
    public MealDto createMeal(MealCreateDto mealCreateDto) {
        if (mealCreateDto.getMealType() == null) {
            throw new IllegalArgumentException("MealType is required.");
        }

        Meal meal = mealMapper.fromCreateDto(mealCreateDto);
        Meal savedMeal = mealRepository.save(meal);
        return mealMapper.toDto(savedMeal);
    }

    @Transactional(readOnly = true)
    public List<MealDto> getAllMeals() {
        return StreamSupport.stream(mealRepository.findAll()
                .spliterator(), false)
                .map(mealMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MealDto getMealById(UUID id) {
        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new MealNotFoundException("Meal not found with id: " + id));
        return mealMapper.toDto(meal);
    }

    @Transactional
    public MealDto updateMeal(UUID id, MealCreateDto updatedMealDto) {
        if (!mealRepository.existsById(id)) {
            throw new MealNotFoundException("Meal not found with id: " + id);
        }

        Meal meal = mealMapper.fromCreateDto(updatedMealDto);
        meal.setId(id);

        Meal savedMeal = mealRepository.save(meal);
        return mealMapper.toDto(savedMeal);
    }

    public void deleteMeal(UUID id) {
        if (!mealRepository.existsById(id)) {
            throw new MealNotFoundException("Meal not found with id: " + id);
        }
        mealRepository.deleteById(id);
    }
}

