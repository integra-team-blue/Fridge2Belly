package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.model.Dish;

import java.util.*;

public interface DishRepository {
    Dish save(Dish dish);
    Optional<Dish> findById(UUID id);
    List<Dish> findAll();
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
