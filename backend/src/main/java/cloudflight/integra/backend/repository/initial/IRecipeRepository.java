package cloudflight.integra.backend.repository.initial;

import cloudflight.integra.backend.model.dtos.RecipeDto;
import java.util.*;

public interface IRecipeRepository {
    RecipeDto save(RecipeDto recipeDto);
    Optional<RecipeDto> findById(UUID id);
    List<RecipeDto> findAll();
    void deleteById(UUID id);

}
