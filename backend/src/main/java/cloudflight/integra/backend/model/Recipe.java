package cloudflight.integra.backend.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Recipe {

    @EqualsAndHashCode.Include
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
}
