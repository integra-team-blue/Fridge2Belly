package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exception.MealNotFoundException;
import cloudflight.integra.backend.model.dtos.MealDto;
import cloudflight.integra.backend.repository.initial.IMealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MealService {

    private final IMealRepository mealRepository;

    @Autowired
    public MealService(IMealRepository mealRepository) {
        this.mealRepository = mealRepository;
    }

    public MealDto createMeal(MealDto mealDto) {
        if (mealDto.getMealType() == null) {
            throw new IllegalArgumentException("MealType is required.");
        }
        if (mealDto.getId() == null) {
            mealDto.setId(UUID.randomUUID());
        }
        return mealRepository.save(mealDto);
    }

    public List<MealDto> getAllMeals() {
        return mealRepository.findAll();
    }

    public MealDto getMealById(UUID id) {
        return mealRepository.findById(id);
    }

    public MealDto updateMeal(UUID id, MealDto updatedMealDto) {
        if (!mealRepository.existsById(id)) {
            throw new MealNotFoundException("Meal not found with id: " + id);
        }
        updatedMealDto.setId(id);
        return mealRepository.save(updatedMealDto);
    }

    public void deleteMeal(UUID id) {
        mealRepository.deleteById(id);
    }
}
