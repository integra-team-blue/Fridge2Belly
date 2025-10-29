package cloudflight.integra.backend.model.dtos;

import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserIngredientDto {
    private UUID id;
    private UUID userId;
    private UUID ingredientId;
    private String name;
    private Double quantity;
    private String unit;
    private LocalDate expirationDate;
    private Double calories;
    private Double protein;
    private Double fat;
    private Double carbohydrates;
}
