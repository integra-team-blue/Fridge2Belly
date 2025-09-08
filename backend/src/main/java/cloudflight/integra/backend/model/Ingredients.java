package cloudflight.integra.backend.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ingredients {

    private UUID id;

    @NotBlank(message = "Ingredient name is required")
    private String name;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Double quantity;

    @NotBlank(message = "Unit is required")
    private String unit;

    private LocalDate expirationDate;
    private Double calories;
    private Double protein;
    private Double fat;
    private Double carbohydrates;

    // Constructor personalizat pentru a genera UUID automat
    public Ingredients(String name, Double quantity, String unit, LocalDate expirationDate,
                       Double calories, Double protein, Double fat, Double carbohydrates) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expirationDate = expirationDate;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrates = carbohydrates;
    }

    // Override pentru NoArgsConstructor să genereze UUID
    public Ingredients() {
        this.id = UUID.randomUUID();
    }
}