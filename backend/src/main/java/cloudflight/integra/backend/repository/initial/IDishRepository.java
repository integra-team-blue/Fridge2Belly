package cloudflight.integra.backend.repository.initial;

import cloudflight.integra.backend.model.dtos.DishDto;

import java.util.*;

public interface IDishRepository {
    DishDto save(DishDto dishDto);
    Optional<DishDto> findById(UUID id);
    List<DishDto> findAll();
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
