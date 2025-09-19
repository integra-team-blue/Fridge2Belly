package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface RecipeRepository extends JpaRepository<Recipe, UUID> {}
