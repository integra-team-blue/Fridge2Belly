package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IngredientRepository extends JpaRepository<Ingredient, UUID> {}
