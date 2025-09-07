package cloudflight.integra.backend.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Meal {

    private UUID id;

    @NotNull(message = "Meal type is required")
    private MealType mealType;

    @NotNull(message = "Date and time is required")
    private LocalDateTime dateTime;

    @NotNull(message = "Dish IDs list must not be null")
    @NotEmpty(message = "Meal must have at least one dish")
    private List<@NotNull(message = "Dish ID cannot be null") UUID> dishIds;

    public Meal(UUID id, MealType mealType, LocalDateTime dateTime, List<UUID> dishIds) {
        this.id = id;
        this.mealType = mealType;
        this.dateTime = dateTime;
        this.dishIds = dishIds;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public List<UUID> getDishIds() {
        return dishIds;
    }

    public void setDishIds(List<UUID> dishIds) {
        this.dishIds = dishIds;
    }
}
