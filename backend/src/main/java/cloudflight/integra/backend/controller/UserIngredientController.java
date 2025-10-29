package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.model.UserIngredient;
import cloudflight.integra.backend.model.dtos.UserIngredientDto;
import cloudflight.integra.backend.model.mappers.UserIngredientMapper;
import cloudflight.integra.backend.service.UserIngredientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/user-ingredients")
public class UserIngredientController {

    private final UserIngredientService userIngredientService;
    private final UserIngredientMapper mapper;

    public UserIngredientController(UserIngredientService userIngredientService,
                                    UserIngredientMapper mapper) {
        this.userIngredientService = userIngredientService;
        this.mapper = mapper;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserIngredientDto>> getAllForUser(@PathVariable UUID userId) {
        List<UserIngredientDto> dtos = userIngredientService.getAllForUser(userId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/user/{userId}/ingredient/{ingredientId}")
    public ResponseEntity<UserIngredientDto> addIngredient(
            @PathVariable UUID userId,
            @PathVariable UUID ingredientId,
            @RequestBody UserIngredientDto dto
    ) {
        UserIngredient saved = userIngredientService.addIngredientToUser(userId, ingredientId);
        return ResponseEntity.ok(mapper.toDto(saved));
    }

    @DeleteMapping("/user/{userId}/ingredient/{ingredientId}")
    public ResponseEntity<Void> removeIngredient(
            @PathVariable UUID userId,
            @PathVariable UUID ingredientId
    ) {
        userIngredientService.removeIngredientFromUser(userId, ingredientId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/user/{userId}/ingredient/{ingredientId}")
    public ResponseEntity<UserIngredientDto> updateIngredient(
            @PathVariable UUID userId,
            @PathVariable UUID ingredientId,
            @RequestBody UserIngredientDto dto
    ) {
        UserIngredient updated = userIngredientService.updateUserIngredient(userId, ingredientId, mapper.toEntity(dto));
        return ResponseEntity.ok(mapper.toDto(updated));
    }
}
