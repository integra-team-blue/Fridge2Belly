package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.model.dtos.MealDto;
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
    public MealDto createMeal(@Valid @RequestBody MealDto mealDto) {
        return mealService.createMeal(mealDto);
    }

    @GetMapping
    public List<MealDto> getAllMeals() { return mealService.getAllMeals(); }

    @GetMapping("/{id}")
    public MealDto getMealById(@PathVariable UUID id) {
        return mealService.getMealById(id);
    }

    @PutMapping("/{id}")
    public MealDto updateMeal(@PathVariable UUID id, @Valid @RequestBody MealDto mealDto) {
        return mealService.updateMeal(id, mealDto);
    }

    @DeleteMapping("/{id}")
    public void deleteMeal(@PathVariable UUID id) {
        mealService.deleteMeal(id);
    }
}
