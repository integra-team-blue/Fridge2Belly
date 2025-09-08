package cloudflight.integra.backend.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Dish {

    @EqualsAndHashCode.Include
    private UUID id;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotNull(message = "Recipe ID is required")
    private UUID recipeId;

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
}
