package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.exception.UserNotFoundException;
import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.repository.inMemory.InMemoryRepositoryUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserRepoTests {

    private InMemoryRepositoryUser repository;

    @BeforeEach
    void setup() {
        repository = new InMemoryRepositoryUser();
    }

    @Test
    void testCreateAndGetAllUsers() {
        User user1 = new User(UUID.randomUUID(), "Ana", "ana@email.com");
        User user2 = new User(UUID.randomUUID(), "Ion", "ion@email.com");

        repository.create(user1);
        repository.create(user2);

        List<User> users = repository.getAll();
        assertEquals(2, users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
    }

    @Test
    void testGetUserById() {
        UUID id = UUID.randomUUID();
        User user = new User(id, "Ana", "ana@email.com");
        repository.create(user);

        User found = repository.getUser(id);
        assertEquals(user, found);
    }

    @Test
    void testGetUserByIdNotFound() {
        UUID id = UUID.randomUUID();
        assertThrows(UserNotFoundException.class, () -> repository.getUser(id));
    }

    @Test
    void testDeleteUser() {
        UUID id = UUID.randomUUID();
        User user = new User(id, "Ana", "ana@email.com");
        repository.create(user);

        repository.delete(id);

        assertEquals(0, repository.getAll().size());
    }

    @Test
    void testDeleteUserNotFound() {
        UUID id = UUID.randomUUID();
        assertThrows(UserNotFoundException.class, () -> repository.delete(id));
    }

    @Test
    void testUpdateUser() {
        UUID id = UUID.randomUUID();
        User user = new User(id, "Ana", "ana@email.com");
        repository.create(user);

        User updatedUser = new User(id, "AnaUpdated", "anaupdated@email.com");
        repository.update(updatedUser);

        User found = repository.getUser(id);
        assertEquals("AnaUpdated", found.getUsername());
        assertEquals("anaupdated@email.com", found.getEmail());
    }

    @Test
    void testUpdateUserNotFound() {
        UUID id = UUID.randomUUID();
        User user = new User(id, "AnaUpdated", "anaupdated@email.com");

        assertThrows(UserNotFoundException.class, () -> repository.update(user));
    }
}
