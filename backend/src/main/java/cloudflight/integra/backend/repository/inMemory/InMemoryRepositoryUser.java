package cloudflight.integra.backend.repository.inMemory;

import cloudflight.integra.backend.exception.UserNotFoundException;
import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.repository.RepositoryUser;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class InMemoryRepositoryUser implements RepositoryUser {

    private final List<User> users = new ArrayList<>();

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users);
    }

    public User getUser(UUID id) {
        return users.stream().filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    }

    @Override
    public void create(User user) {
        users.add(user);
    }

    @Override
    public void delete(UUID id) {
        boolean removed = users.removeIf(u -> u.getId().equals(id));
        if (!removed) {
            throw new UserNotFoundException("User with id " + id + " not found");
        }
    }

    @Override
    public void update(User user) {
        users.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst()
                .map(u -> {
                    u.setUsername(user.getUsername());
                    u.setEmail(user.getEmail());
                    return u;
                })
                .orElseThrow(() -> new UserNotFoundException("User with id " + user.getId() + " not found"));
    }
}
