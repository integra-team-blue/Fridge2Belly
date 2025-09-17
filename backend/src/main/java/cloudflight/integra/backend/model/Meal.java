package cloudflight.integra.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "meals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Meal {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false)
    private MealType mealType;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @ManyToMany
    @JoinTable(
            name = "meal_dishes",
            joinColumns = @JoinColumn(name = "meal_id"),
            inverseJoinColumns = @JoinColumn(name = "dish_id")
    )
    private List<Dish> dishes;
}
