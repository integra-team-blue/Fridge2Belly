package cloudflight.integra.backend.service;

import cloudflight.integra.backend.model.Recipe;
import cloudflight.integra.backend.model.dtos.RecipeDto;
import cloudflight.integra.backend.model.mappers.RecipeMapper;
import cloudflight.integra.backend.repository.RecipeRepository;
import cloudflight.integra.backend.repository.initial.IRecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    private final RecipeRepository repository;
    private final RecipeMapper mapper;

    public RecipeService(RecipeRepository repository, RecipeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public RecipeDto createRecipe(RecipeDto recipeDto) {
        return mapper.toDto(repository.save(mapper.toEntity(recipeDto)));
    }

    @Transactional
    public RecipeDto getRecipe(UUID id) {
        return mapper.toDto(repository.getReferenceById(id));
    }

    @Transactional
    public List<RecipeDto> getAllRecipes() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public RecipeDto updateRecipe(UUID id, RecipeDto recipeDto) {
        Recipe existing = mapper.toEntity(recipeDto);
        existing.setId(id);
        return mapper.toDto(repository.save(existing));
    }

    @Transactional
    public void deleteRecipe(UUID id) {
        repository.deleteById(id);
    }
}

