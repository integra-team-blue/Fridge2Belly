package cloudflight.integra.backend.model.mappers;

import cloudflight.integra.backend.model.Ingredient;
import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.model.dtos.UserDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {
    public UserDto toDto(User user) {
        if (user == null) return null;

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                // Adaugă lista de ingredientIds
                .ingredientIds(user.getIngredients() != null
                                       ? user.getIngredients().stream()
                        .map(Ingredient::getId)
                        .toList()
                                       : List.of())
                .build();
    }

    public User toEntity(UserDto dto) {
        if (dto == null) return null;

        return User.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .email(dto.getEmail())
                // aici nu setăm ingredients, se va face în serviciu dacă e nevoie
                .build();
    }
}

