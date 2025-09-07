package cloudflight.integra.backend.dish.Model;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

public class Dish {
    private UUID id;

    @NotBlank(message = "name must not be blank")
    private String name;

    @NotNull(message = "recipeId is required")
    private UUID recipeId;

    @NotNull(message = "preparedAt is required")
    private LocalDateTime preparedAt;

    @PositiveOrZero(message = "calories must be >= 0")
    private double calories;

    @PositiveOrZero(message = "protein must be >= 0")
    private double protein;

    @PositiveOrZero(message = "fat must be >= 0")
    private double fat;

    @PositiveOrZero(message = "carbohydrates must be >= 0")
    private double carbohydrates;

    public Dish() {}

    public Dish(UUID id, String name, UUID recipeId, LocalDateTime preparedAt,
                double calories, double protein, double fat, double carbohydrates) {
        this.id = id;
        this.name = name;
        this.recipeId = recipeId;
        this.preparedAt = preparedAt;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrates = carbohydrates;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public UUID getRecipeId() { return recipeId; }
    public void setRecipeId(UUID recipeId) { this.recipeId = recipeId; }
    public LocalDateTime getPreparedAt() { return preparedAt; }
    public void setPreparedAt(LocalDateTime preparedAt) { this.preparedAt = preparedAt; }
    public double getCalories() { return calories; }
    public void setCalories(double calories) { this.calories = calories; }
    public double getProtein() { return protein; }
    public void setProtein(double protein) { this.protein = protein; }
    public double getFat() { return fat; }
    public void setFat(double fat) { this.fat = fat; }
    public double getCarbohydrates() { return carbohydrates; }
    public void setCarbohydrates(double carbohydrates) { this.carbohydrates = carbohydrates; }
}
