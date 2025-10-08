package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.model.Recipe;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface RecipeRepository extends CrudRepository<Recipe, UUID> {
    List<Recipe> findAllByIdIn(List<UUID> ids);
}
