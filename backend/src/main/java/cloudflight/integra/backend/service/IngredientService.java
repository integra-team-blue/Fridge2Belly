package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exception.IngredientsExeption;
import cloudflight.integra.backend.model.Ingredient;
import cloudflight.integra.backend.model.dtos.IngredientDto;
import cloudflight.integra.backend.model.mappers.IngredientMapper;
import cloudflight.integra.backend.repository.IngredientRepository;
import cloudflight.integra.backend.validation.IngredientsValidator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final IngredientsValidator validator;
    private final IngredientMapper ingredientMapper;

    @Autowired
    public IngredientService(IngredientRepository ingredientRepository, IngredientsValidator validator,
                             IngredientMapper ingredientMapper) {
        this.ingredientRepository = ingredientRepository;
        this.validator = validator;
        this.ingredientMapper = ingredientMapper;
    }

    @Transactional
    public List<IngredientDto> getAllIngredients() {
        return ingredientRepository.findAll()
                .stream()
                .map(ingredientMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public IngredientDto getIngredientById(UUID id) {
        validateId(id);

        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new IngredientsExeption("Ingredient not found with id: " + id));

        return ingredientMapper.toDto(ingredient);
    }

    public IngredientDto createIngredient(IngredientDto ingredientDto) {
        validator.validateIngredient(ingredientDto);

        if (ingredientDto.getId() == null) {
            ingredientDto.setId(UUID.randomUUID());
        }

        Ingredient ingredient = ingredientMapper.toEntity(ingredientDto);
        Ingredient savedIngredient = ingredientRepository.save(ingredient);

        return ingredientMapper.toDto(savedIngredient);
    }

    public IngredientDto updateIngredient(UUID id, IngredientDto ingredientDto) {
        validateId(id);

        if (!ingredientRepository.existsById(id)) {
            throw new IngredientsExeption("Ingredient not found with id: " + id);
        }

        validator.validateIngredient(ingredientDto);
        ingredientDto.setId(id);

        Ingredient ingredient = ingredientMapper.toEntity(ingredientDto);
        Ingredient savedIngredient = ingredientRepository.save(ingredient);

        return ingredientMapper.toDto(savedIngredient);
    }

    public void deleteIngredient(UUID id) {
        validateId(id);

        if (!ingredientRepository.existsById(id)) {
            throw new IngredientsExeption("Ingredient not found with id: " + id);
        }

        ingredientRepository.deleteById(id);
    }

    private void validateId(UUID id) {
        if (id == null) {
            throw new IngredientsExeption("ID cannot be null");
        }
    }
}
