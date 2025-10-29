package cloudflight.integra.backend.model.mappers;

import cloudflight.integra.backend.model.Ingredient;
import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.model.dtos.IngredientDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IngredientMapper {
    public IngredientDto toDto(Ingredient ingredient) {
        if (ingredient == null) return null;

        return IngredientDto.builder()
                .id(ingredient.getId())
                .name(ingredient.getName())
                .quantity(ingredient.getQuantity())
                .unit(ingredient.getUnit())
                .expirationDate(ingredient.getExpirationDate())
                .calories(ingredient.getCalories())
                .protein(ingredient.getProtein())
                .fat(ingredient.getFat())
                .carbohydrates(ingredient.getCarbohydrates())
                // Adaugă lista de userIds
                .userIds(ingredient.getUsers() != null
                                 ? ingredient.getUsers().stream()
                        .map(User::getId)
                        .toList()
                                 : List.of())
                .build();
    }

    public Ingredient toEntity(IngredientDto dto) {
        if (dto == null) return null;

        return Ingredient.builder()
                .id(dto.getId())
                .name(dto.getName())
                .quantity(dto.getQuantity())
                .unit(dto.getUnit())
                .expirationDate(dto.getExpirationDate())
                .calories(dto.getCalories())
                .protein(dto.getProtein())
                .fat(dto.getFat())
                .carbohydrates(dto.getCarbohydrates())
                // aici nu setăm users, se va face în serviciu dacă e nevoie
                .build();
    }
}

