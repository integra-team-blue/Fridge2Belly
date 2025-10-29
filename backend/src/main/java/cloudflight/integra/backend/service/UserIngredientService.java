package cloudflight.integra.backend.service;

import cloudflight.integra.backend.model.Ingredient;
import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.model.UserIngredient;
import cloudflight.integra.backend.model.dtos.IngredientDto;
import cloudflight.integra.backend.repository.IngredientRepository;
import cloudflight.integra.backend.repository.UserIngredientRepository;
import cloudflight.integra.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserIngredientService {

    private final UserIngredientRepository userIngredientRepository;
    private final UserRepository userRepository;
    private final IngredientRepository ingredientRepository;

    public UserIngredientService(UserIngredientRepository userIngredientRepository,
                                 UserRepository userRepository,
                                 IngredientRepository ingredientRepository) {
        this.userIngredientRepository = userIngredientRepository;
        this.userRepository = userRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @Transactional(readOnly = true)
    public List<UserIngredient> getAllForUser(UUID userId) {
        return userIngredientRepository.findAllByUserId(userId);
    }

    @Transactional
    public UserIngredient addIngredientToUser(UUID userId, UUID ingredientId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));

        UserIngredient ui = UserIngredient.builder()
                .user(user)
                .ingredient(ingredient)
                .quantity(ingredient.getQuantity())
                .unit(ingredient.getUnit())
                .expirationDate(ingredient.getExpirationDate())
                .calories(ingredient.getCalories())
                .protein(ingredient.getProtein())
                .fat(ingredient.getFat())
                .carbohydrates(ingredient.getCarbohydrates())
                .build();

        return userIngredientRepository.save(ui);
    }


    @Transactional
    public void removeIngredientFromUser(UUID id) {
        Optional<UserIngredient> uiOpt = userIngredientRepository.findByIdWithUserAndIngredient(id);

        if (uiOpt.isEmpty()) {
            throw new EntityNotFoundException("UserIngredient not found with id: " + id);
        }

        UserIngredient ui = uiOpt.get();

        userIngredientRepository.delete(ui);
    }

    @Transactional
    public UserIngredient updateUserIngredient(UUID id, UserIngredient userIngredientData) {
        Optional<UserIngredient> uiOpt = userIngredientRepository.findByIdWithUserAndIngredient(id);
        if (uiOpt.isEmpty()) {
            throw new EntityNotFoundException("UserIngredient not found with id: " + id);
        }

        UserIngredient ui = uiOpt.get();

        ui.setQuantity(userIngredientData.getQuantity());
        ui.setUnit(userIngredientData.getUnit());
        ui.setExpirationDate(userIngredientData.getExpirationDate());
        ui.setCalories(userIngredientData.getCalories());
        ui.setProtein(userIngredientData.getProtein());
        ui.setFat(userIngredientData.getFat());
        ui.setCarbohydrates(userIngredientData.getCarbohydrates());

        return userIngredientRepository.save(ui);
    }

}
