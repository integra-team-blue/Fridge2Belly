package cloudflight.integra.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "dishes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Dish {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "prepared_at", nullable = false)
    private LocalDateTime preparedAt;

    @Column(nullable = false)
    private double calories;

    @Column(nullable = false)
    private double protein;

    @Column(nullable = false)
    private double fat;

    @Column(nullable = false)
    private double carbohydrates;

    @ManyToMany
    @JoinTable(
            name = "dish_ingredients",
            joinColumns = @JoinColumn(name = "dish_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private List<Ingredient> ingredients;

    @ManyToMany(mappedBy = "dishes")
    private List<Recipe> recipes;
}
