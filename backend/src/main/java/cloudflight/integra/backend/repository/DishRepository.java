package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.model.Dish;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DishRepository extends JpaRepository<Dish, UUID> {
    List<Dish> findAllByIdIn(List<UUID> ids);

    Optional<Dish> findByName(String name);
}
