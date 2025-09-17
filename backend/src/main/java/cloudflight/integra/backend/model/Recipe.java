package cloudflight.integra.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "recipes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Recipe {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "cooking_time_minutes", nullable = false)
    private int cookingTimeMinutes;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String instructions;

    @ManyToMany
    @JoinTable(
            name = "recipe_dishes",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "dish_id")
    )
    private List<Dish> dishes;
}
