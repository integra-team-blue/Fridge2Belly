package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.model.Ingredient;
import cloudflight.integra.backend.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IngredientRepository extends JpaRepository<Ingredient, UUID> {
    List<Ingredient> findAllByIdIn(List<UUID> ids);
}
