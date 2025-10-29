package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.model.Meal;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface MealRepository extends CrudRepository<Meal, UUID> {}
