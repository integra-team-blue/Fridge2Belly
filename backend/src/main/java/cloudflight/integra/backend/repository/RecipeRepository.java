package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.model.Recipe;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface RecipeRepository extends CrudRepository<Recipe, UUID> {}
