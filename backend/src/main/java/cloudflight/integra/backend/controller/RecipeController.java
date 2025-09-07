package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.model.Recipe;
import cloudflight.integra.backend.service.RecipeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService service;

    public RecipeController(RecipeService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Recipe createRecipe(@Valid @RequestBody Recipe recipe) {
        return service.createRecipe(recipe);
    }

    @GetMapping("/{id}")
    public Recipe getRecipe(@PathVariable UUID id) {
        return service.getRecipe(id);
    }

    @GetMapping
    public List<Recipe> getAllRecipes() {
        return service.getAllRecipes();
    }

    @PutMapping("/{id}")
    public Recipe updateRecipe(@PathVariable UUID id, @Valid @RequestBody Recipe recipe) {
        return service.updateRecipe(id, recipe);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecipe(@PathVariable UUID id) {
        service.deleteRecipe(id);
    }
}
