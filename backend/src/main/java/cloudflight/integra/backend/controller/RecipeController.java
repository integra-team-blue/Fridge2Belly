package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.model.dtos.RecipeDto;
import cloudflight.integra.backend.service.RecipeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "https://localhost:4200")
@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService service;

    public RecipeController(RecipeService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeDto createRecipe(@Valid @RequestBody RecipeDto recipeDto) {
        return service.createRecipe(recipeDto);
    }

    @GetMapping("/{id}")
    public RecipeDto getRecipe(@PathVariable UUID id) {
        return service.getRecipe(id);
    }

    @GetMapping
    public List<RecipeDto> getAllRecipes() { return service.getAllRecipes(); }

    @PutMapping("/{id}")
    public RecipeDto updateRecipe(@PathVariable UUID id, @Valid @RequestBody RecipeDto recipeDto) {
        return service.updateRecipe(id, recipeDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecipe(@PathVariable UUID id) {
        service.deleteRecipe(id);
    }
}
