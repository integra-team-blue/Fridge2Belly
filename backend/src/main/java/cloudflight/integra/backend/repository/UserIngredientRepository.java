package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.model.UserIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserIngredientRepository extends JpaRepository<UserIngredient, UUID> {

    @Query("SELECT ui FROM UserIngredient ui JOIN FETCH ui.ingredient WHERE ui.user.id = :userId")
    List<UserIngredient> findAllByUserId(@Param("userId") UUID userId);

    @Query("SELECT ui FROM UserIngredient ui JOIN FETCH ui.user JOIN FETCH ui.ingredient WHERE ui.id = :id")
    Optional<UserIngredient> findByIdWithUserAndIngredient(@Param("id") UUID id);

}
