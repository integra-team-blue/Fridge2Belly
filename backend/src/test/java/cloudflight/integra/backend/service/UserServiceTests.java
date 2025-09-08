package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exception.UserNotFoundException;
import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.repository.RepositoryUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserServiceTests {

    private RepositoryUser repositoryUser;
    private UserService userService;

    @BeforeEach
    void setup() {
        repositoryUser = Mockito.mock(RepositoryUser.class);
        userService = new UserService(repositoryUser);
    }

    @Test
    void testFindAll() {
        List<User> users = Arrays.asList(
                new User(UUID.randomUUID(), "Ana", "ana@email.com"),
                new User(UUID.randomUUID(), "Ion", "ion@email.com")
        );

        when(repositoryUser.getAll()).thenReturn(users);

        List<User> result = userService.findAll();
        assertEquals(users, result);

        verify(repositoryUser, times(1)).getAll();
    }

    @Test
    void testFindUser() {
        UUID id = UUID.randomUUID();
        User user = new User(id, "Ana", "ana@email.com");

        when(repositoryUser.getUser(id)).thenReturn(user);

        User result = userService.findUser(id);
        assertEquals(user, result);

        verify(repositoryUser, times(1)).getUser(id);
    }

    @Test
    void testFindUserNotFound() {
        UUID id = UUID.randomUUID();

        when(repositoryUser.getUser(id)).thenThrow(new UserNotFoundException("User with id " + id + " not found"));

        assertThrows(UserNotFoundException.class, () -> userService.findUser(id));
        verify(repositoryUser, times(1)).getUser(id);
    }

    @Test
    void testCreateUser() {
        User user = new User(UUID.randomUUID(), "Ana", "ana@email.com");

        userService.createUser(user);

        verify(repositoryUser, times(1)).create(user);
    }

    @Test
    void testCreateUserIDNull() {
        User user = new User(null, "Ana", "ana@email.com");

        userService.createUser(user);

        verify(repositoryUser, times(1)).create(user);
    }

    @Test
    void testDeleteUser() {
        UUID id = UUID.randomUUID();

        userService.deleteUser(id);

        verify(repositoryUser, times(1)).delete(id);
    }

    @Test
    void testDeleteUserNotFound() {
        UUID id = UUID.randomUUID();
        doThrow(new UserNotFoundException("User with id " + id + " not found"))
                .when(repositoryUser).delete(id);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(id));
        verify(repositoryUser, times(1)).delete(id);
    }

    @Test
    void testUpdateUser() {
        UUID id = UUID.randomUUID();
        User user = new User(id, "AnaUpdated", "anaupdated@email.com");

        userService.updateUser(id, user);

        verify(repositoryUser, times(1)).update(user);
    }

    @Test
    void testUpdateUserNotFound() {
        UUID id = UUID.randomUUID();
        User user = new User(id, "AnaUpdated", "anaupdated@email.com");
        doThrow(new UserNotFoundException("User with id " + user.getId() + " not found"))
                .when(repositoryUser).update(user);

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(id, user));
        verify(repositoryUser, times(1)).update(user);
    }
}
