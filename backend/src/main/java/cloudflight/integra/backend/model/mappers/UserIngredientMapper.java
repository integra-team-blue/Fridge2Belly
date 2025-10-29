package cloudflight.integra.backend.model.mappers;

import cloudflight.integra.backend.model.UserIngredient;
import cloudflight.integra.backend.model.dtos.UserIngredientDto;
import org.springframework.stereotype.Component;

@Component
public class UserIngredientMapper {

    public UserIngredientDto toDto(UserIngredient ui) {
        if (ui == null) return null;

        return UserIngredientDto.builder()
                .id(ui.getId())
                .userId(ui.getUser().getId())
                .ingredientId(ui.getIngredient().getId())
                .name(ui.getIngredient().getName())
                .quantity(ui.getQuantity())
                .unit(ui.getUnit())
                .expirationDate(ui.getExpirationDate())
                .calories(ui.getCalories())
                .protein(ui.getProtein())
                .fat(ui.getFat())
                .carbohydrates(ui.getCarbohydrates())
                .build();
    }

    public UserIngredient toEntity(UserIngredientDto dto) {
        if (dto == null) return null;

        return UserIngredient.builder()
                // user și ingredient se setează în serviciu după ce se obțin entitățile
                .quantity(dto.getQuantity())
                .unit(dto.getUnit())
                .expirationDate(dto.getExpirationDate())
                .calories(dto.getCalories())
                .protein(dto.getProtein())
                .fat(dto.getFat())
                .carbohydrates(dto.getCarbohydrates())
                .build();
    }
}
