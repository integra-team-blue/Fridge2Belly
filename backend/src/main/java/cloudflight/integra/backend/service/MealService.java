package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exception.MealNotFoundException;
import cloudflight.integra.backend.model.Meal;
import cloudflight.integra.backend.repository.MealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MealService {

    private final MealRepository mealRepository;

    @Autowired
    public MealService(MealRepository mealRepository) {
        this.mealRepository = mealRepository;
    }

    public Meal createMeal(Meal meal) {
        if (meal.getMealType() == null) {
            throw new IllegalArgumentException("MealType is required.");
        }
        if (meal.getId() == null) {
            meal.setId(UUID.randomUUID());
        }
        return mealRepository.save(meal);
    }

    public List<Meal> getAllMeals() {
        return mealRepository.findAll();
    }

    public Meal getMealById(UUID id) {
        return mealRepository.findById(id);
    }

    public Meal updateMeal(UUID id, Meal updatedMeal) {
        if (!mealRepository.existsById(id)) {
            throw new MealNotFoundException("Meal not found with id: " + id);
        }
        updatedMeal.setId(id);
        return mealRepository.save(updatedMeal);
    }

    public void deleteMeal(UUID id) {
        mealRepository.deleteById(id);
    }
}
