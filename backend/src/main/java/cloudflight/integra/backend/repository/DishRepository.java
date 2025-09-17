package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.model.Dish;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DishRepository extends JpaRepository<Dish, UUID> {}
