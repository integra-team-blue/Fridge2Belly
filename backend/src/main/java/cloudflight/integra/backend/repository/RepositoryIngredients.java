package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.model.Ingredients;
import java.util.UUID;
import java.util.List;

public interface RepositoryIngredients {

    List<Ingredients> getAll();
    Ingredients getIngredient(UUID id);
    void create(Ingredients ingredient);
    void update(UUID id, Ingredients ingredient);
    void delete(UUID id);

}
