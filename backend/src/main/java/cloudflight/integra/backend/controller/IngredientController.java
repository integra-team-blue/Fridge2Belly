package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.model.Ingredient;
import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.model.dtos.IngredientDto;
import cloudflight.integra.backend.model.dtos.UserDto;
import cloudflight.integra.backend.service.IngredientService;
import cloudflight.integra.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("api/ingredients")
public class IngredientController {

    public final IngredientService ingredientsService;

    @Autowired
    public IngredientController(IngredientService ingredientsService) {
        this.ingredientsService = ingredientsService;
    }

    // Get all ingredients
    @GetMapping
    public ResponseEntity<List<IngredientDto>> getAllIngredients() {
        List<IngredientDto> ingredients = ingredientsService.getAllIngredients();
        return ResponseEntity.ok(ingredients);
    }

    //Get an ingredient by id
    @GetMapping("/{id}")
    public ResponseEntity<IngredientDto> getIngredientById(@PathVariable UUID id) {
        IngredientDto ingredient = ingredientsService.getIngredientById(id);
        return ResponseEntity.ok(ingredient);
    }

    //Create a new ingredient
    @PostMapping
    public ResponseEntity<IngredientDto> addIngredient(@RequestBody IngredientDto ingredient) {
        IngredientDto createdIngredient = ingredientsService.createIngredient(ingredient);
        return ResponseEntity.ok(createdIngredient);
    }

    //Update an existing ingredient
    @PutMapping("/{id}")
    public ResponseEntity<IngredientDto> updateIngredient(
                                                          @PathVariable UUID id,
                                                          @Valid @RequestBody IngredientDto ingredient) {
        IngredientDto updatedIngredient = ingredientsService.updateIngredient(id, ingredient);
        return ResponseEntity.ok(updatedIngredient);
    }

    //Delete an ingredient
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable UUID id) {
        ingredientsService.deleteIngredient(id);
        return ResponseEntity.noContent()
                .build();
    }

}

