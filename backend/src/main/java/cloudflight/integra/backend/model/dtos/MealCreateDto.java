package cloudflight.integra.backend.model.dtos;

import cloudflight.integra.backend.model.MealType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealCreateDto {

    @NotNull(message = "Meal type is required")
    private MealType mealType;

    @NotNull(message = "Date and time is required")
    private LocalDateTime dateTime;

    @NotEmpty(message = "Meal must have at least one dish")
    private List<UUID> dishIds;
}
