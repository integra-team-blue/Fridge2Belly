package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.model.Ingredients;
import cloudflight.integra.backend.service.IngredientsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/ingredients")
public class IngredientsController {

    public final IngredientsService ingredientsService;

    @Autowired
    public IngredientsController(IngredientsService ingredientsService) {
        this.ingredientsService = ingredientsService;
    }

    // Get all ingredients
    @GetMapping
    public ResponseEntity<List<Ingredients>> getAllIngredients() {
        List<Ingredients> ingredients = ingredientsService.getAllIngredients();
        return ResponseEntity.ok(ingredients);
    }

    //Get an ingredient by id
    @GetMapping("/{id}")
    public ResponseEntity<Ingredients> getIngredientById(@PathVariable UUID id) {
        Ingredients ingredient = ingredientsService.getIngredientById(id);
        return ResponseEntity.ok(ingredient);
    }

    //Create a new ingredient
    @PostMapping
    public ResponseEntity<Ingredients> addIngredient(@RequestBody Ingredients ingredient) {
        Ingredients createdIngredient = ingredientsService.createIngredient(ingredient);
        return ResponseEntity.ok(createdIngredient);
    }

    //Update an existing ingredient
    @PutMapping("/{id}")
    public ResponseEntity<Ingredients> updateIngredient(
            @PathVariable UUID id,
            @Valid @RequestBody Ingredients ingredient) {
        Ingredients updatedIngredient = ingredientsService.updateIngredient(id, ingredient);
        return ResponseEntity.ok(updatedIngredient);
    }

    //Delete an ingredient
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable UUID id) {
        ingredientsService.deleteIngredient(id);
        return ResponseEntity.noContent().build();
    }



}
