package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.model.User;

import java.util.List;
import java.util.UUID;

public interface RepositoryUser {
    List<User> getAll();
    User getUser(UUID id);
    User create(User user);
    void delete(UUID id);
    void update(User user);
}
