package cloudflight.integra.backend.model.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DishDto {

    @EqualsAndHashCode.Include
    private UUID id;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotNull(message = "PreparedAt is required")
    private LocalDateTime preparedAt;

    @PositiveOrZero(message = "Calories must be >= 0")
    private double calories;

    @PositiveOrZero(message = "Protein must be >= 0")
    private double protein;

    @PositiveOrZero(message = "Fat must be >= 0")
    private double fat;

    @PositiveOrZero(message = "Carbohydrates must be >= 0")
    private double carbohydrates;

    //@NotNull(message = "Recipes cannot be null")
    private List<RecipeDto> recipes;

    //@NotNull(message = "Ingredients cannot be null")
    private List<IngredientDto> ingredients;
}

