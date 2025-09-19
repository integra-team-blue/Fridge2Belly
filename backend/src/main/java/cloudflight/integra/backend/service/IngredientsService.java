package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exception.IngredientsExeption;
import cloudflight.integra.backend.model.Ingredient;
import cloudflight.integra.backend.model.dtos.IngredientDto;
import cloudflight.integra.backend.model.mappers.IngredientMapper;
import cloudflight.integra.backend.repository.IngredientRepository;
import cloudflight.integra.backend.repository.initial.IIngredientsRepository;
import cloudflight.integra.backend.validation.IngredientsValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class IngredientsService {

    private final IngredientRepository repository;
    private final IngredientMapper mapper;
    private final IngredientsValidator validator;

    @Autowired
    public IngredientsService(IngredientRepository repository, IngredientMapper mapper, IngredientsValidator validator) {
        this.repository = repository;
        this.mapper = mapper;
        this.validator = validator;
    }

    @Transactional
    public List<IngredientDto> getAllIngredients() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public IngredientDto getIngredientById(UUID id) {
        validateId(id);

        return mapper.toDto(repository.getReferenceById(id));
    }

    @Transactional
    public IngredientDto createIngredient(IngredientDto ingredientDto) {
        validator.validateIngredient(ingredientDto);
        
        return mapper.toDto(repository.save(mapper.toEntity(ingredientDto)));
    }

    @Transactional
    public IngredientDto updateIngredient(UUID id, IngredientDto ingredientDto) {
        validateId(id);

        if (!repository.existsById(id)) {
            throw new IngredientsExeption("Ingredient not found with id: " + id);
        }
        validator.validateIngredient(ingredientDto);
        
        Ingredient ingredient = mapper.toEntity(ingredientDto);
        ingredient.setId(id);
        
        return mapper.toDto(repository.save(ingredient));
    }

    @Transactional
    public void deleteIngredient(UUID id) {
        validateId(id);

        if (!repository.existsById(id)) {
            throw new IngredientsExeption("Ingredient not found with id: " + id);
        }

        repository.deleteById(id);
    }

    private void validateId(UUID id) {
        if (id == null) {
            throw new IngredientsExeption("ID cannot be null");
        }
    }
}

