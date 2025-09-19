package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MealRepository extends JpaRepository<Meal, UUID> {}
