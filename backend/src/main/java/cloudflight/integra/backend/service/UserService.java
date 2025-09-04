package cloudflight.integra.backend.service;

import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.repository.RepositoryUser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final RepositoryUser repositoryUser;

    public UserService(RepositoryUser repositoryUser) {
        this.repositoryUser = repositoryUser;
    }

    public List<User> findAll() {
        return repositoryUser.getAll();
    }

    public User findUser(UUID id) {
        return repositoryUser.getUser(id);
    }

    public void createUser(User user) {
        if(user.getId() == null)
            user.setId(UUID.randomUUID());
        repositoryUser.create(user);
    }

    public void deleteUser(UUID id) {
        repositoryUser.delete(id);
    }

    public void updateUser(UUID id, User user) {
        user.setId(id);
        repositoryUser.update(user);
    }
}
