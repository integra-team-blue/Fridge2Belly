package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.model.Recipe;
import java.util.*;

public interface RecipeRepository {
    Recipe save(Recipe recipe);
    Optional<Recipe> findById(UUID id);
    List<Recipe> findAll();
    void deleteById(UUID id);
}