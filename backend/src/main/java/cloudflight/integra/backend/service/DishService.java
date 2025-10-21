package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exception.DishNotFoundException;
import cloudflight.integra.backend.model.Dish;
import cloudflight.integra.backend.model.Meal;
import cloudflight.integra.backend.model.Recipe;
import cloudflight.integra.backend.model.dtos.DishCreateDto;
import cloudflight.integra.backend.model.dtos.DishDto;
import cloudflight.integra.backend.model.mappers.DishMapper;
import cloudflight.integra.backend.repository.DishRepository;
import cloudflight.integra.backend.repository.MealRepository;
import cloudflight.integra.backend.repository.RecipeRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DishService {
    private final DishRepository dishRepository;
    private final DishMapper dishMapper;
    private final MealRepository mealRepository;
    private final RecipeRepository recipeRepository;

    public DishService(DishRepository dishRepository,
                       DishMapper dishMapper,
                       MealRepository mealRepository,
                       RecipeRepository recipeRepository) {
        this.dishRepository = dishRepository;
        this.dishMapper = dishMapper;
        this.mealRepository = mealRepository;
        this.recipeRepository = recipeRepository;
    }

    @Transactional
    public DishDto create(DishCreateDto createDto) {
        Dish dish = dishMapper.fromCreateDto(createDto);
        Dish savedDish = dishRepository.save(dish);
        return dishMapper.toDto(savedDish);
    }

    @Transactional(readOnly = true)
    public List<DishDto> getAll() {
        return dishRepository.findAll()
                .stream()
                .map(dishMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DishDto getById(UUID id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new DishNotFoundException("Dish not found with id: " + id));
        return dishMapper.toDto(dish);
    }

    @Transactional
    public DishDto update(UUID id, DishDto dishDto) {
        if (!dishRepository.existsById(id)) {
            throw new DishNotFoundException("Dish not found with id: " + id);
        }

        Dish dish = dishMapper.toEntity(dishDto);
        dish.setId(id);

        Dish savedDish = dishRepository.save(dish);
        return dishMapper.toDto(savedDish);
    }

    @Transactional
    public void delete(UUID id) {
        if (!dishRepository.existsById(id)) {
            throw new DishNotFoundException("Dish not found with id: " + id);
        }

        List<Meal> mealsUsingDish = new ArrayList<>();
        mealRepository.findAll()
                .forEach(meal -> {
                    if (meal.getDishes() != null && meal.getDishes()
                            .stream()
                            .anyMatch(d -> d.getId()
                                    .equals(id))) {
                        mealsUsingDish.add(meal);
                    }
                });

        for (Meal meal : mealsUsingDish) {
            meal.getDishes()
                    .removeIf(d -> d.getId()
                            .equals(id));
            mealRepository.save(meal);
        }

        List<Recipe> recipesUsingDish = new ArrayList<>();
        recipeRepository.findAll()
                .forEach(recipe -> {
                    if (recipe.getDishes() != null && recipe.getDishes()
                            .stream()
                            .anyMatch(d -> d.getId()
                                    .equals(id))) {
                        recipesUsingDish.add(recipe);
                    }
                });

        for (Recipe recipe : recipesUsingDish) {
            recipe.getDishes()
                    .removeIf(d -> d.getId()
                            .equals(id));
            recipeRepository.save(recipe);
        }

        dishRepository.deleteById(id);
    }
}
