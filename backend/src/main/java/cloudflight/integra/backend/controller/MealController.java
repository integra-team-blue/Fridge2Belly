package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.model.Meal;
import cloudflight.integra.backend.service.MealService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/meals")
public class MealController {

    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @PostMapping
    public Meal createMeal(@Valid @RequestBody Meal meal) {
        return mealService.createMeal(meal);
    }

    @GetMapping
    public List<Meal> getAllMeals() {
        return mealService.getAllMeals();
    }

    @GetMapping("/{id}")
    public Meal getMealById(@PathVariable UUID id) {
        return mealService.getMealById(id);
    }

    @PutMapping("/{id}")
    public Meal updateMeal(@PathVariable UUID id, @Valid @RequestBody Meal meal) {
        return mealService.updateMeal(id, meal);
    }

    @DeleteMapping("/{id}")
    public void deleteMeal(@PathVariable UUID id) {
        mealService.deleteMeal(id);
    }
}
