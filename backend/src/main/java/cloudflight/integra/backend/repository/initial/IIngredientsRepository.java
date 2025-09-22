package cloudflight.integra.backend.repository.initial;

import cloudflight.integra.backend.model.dtos.IngredientDto;
import java.util.UUID;
import java.util.List;

public interface IIngredientsRepository {

    List<IngredientDto> getAll();

    IngredientDto getIngredient(UUID id);

    void create(IngredientDto ingredient);

    void update(UUID id, IngredientDto ingredient);

    void delete(UUID id);

}

