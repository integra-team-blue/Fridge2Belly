package cloudflight.integra.backend.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public class Recipe {

    private UUID id;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    private String description;

    @Min(value = 1, message = "Cooking time must be at least 1 minute")
    private int cookingTimeMinutes;

    @NotBlank(message = "Instructions are required")
    private String instructions;

    @NotNull(message = "Ingredients cannot be null")
    private List<UUID> ingredientsId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getCookingTimeMinutes() { return cookingTimeMinutes; }
    public void setCookingTimeMinutes(int cookingTimeMinutes) { this.cookingTimeMinutes = cookingTimeMinutes; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public List<UUID> getIngredientsId() { return ingredientsId; }
    public void setIngredientsId(List<UUID> ingredientsId) { this.ingredientsId = ingredientsId; }
}