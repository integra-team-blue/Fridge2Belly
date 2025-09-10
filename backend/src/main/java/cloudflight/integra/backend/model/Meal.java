package cloudflight.integra.backend.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meal {

    private UUID id;

    @NotNull(message = "Meal type is required")
    private MealType mealType;

    @NotNull(message = "Date and time is required")
    private LocalDateTime dateTime;

    @NotNull(message = "Dish IDs list must not be null")
    @NotEmpty(message = "Meal must have at least one dish")
    private List<@NotNull(message = "Dish ID cannot be null") UUID> dishIds;

}
