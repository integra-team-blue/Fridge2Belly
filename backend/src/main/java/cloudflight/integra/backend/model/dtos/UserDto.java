package cloudflight.integra.backend.model.dtos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private UUID id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @Builder.Default
    private List<UUID> ingredientIds = new ArrayList<>();

    public UserDto(UUID id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.ingredientIds = new ArrayList<>();
    }

}
