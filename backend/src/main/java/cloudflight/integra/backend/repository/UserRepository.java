package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {}
