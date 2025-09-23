package cloudflight.integra.backend.service;

import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.model.dtos.UserDto;
import cloudflight.integra.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserDtoServiceTests {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    void testFindAll() {
        List<User> entities = List.of(
                                      new User(UUID.randomUUID(), "Ana", "ana@email.com"),
                                      new User(UUID.randomUUID(), "Ion", "ion@email.com"));
        when(userRepository.findAll()).thenReturn(entities);

        List<UserDto> result = userService.findAll();

        assertEquals(2, result.size());
        assertEquals(entities.get(0)
                .getId(),
                     result.get(0)
                             .getId());
        assertEquals(entities.get(0)
                .getUsername(),
                     result.get(0)
                             .getUsername());
        assertEquals(entities.get(0)
                .getEmail(),
                     result.get(0)
                             .getEmail());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testFindUser() {
        UUID id = UUID.randomUUID();
        User entity = new User(id, "Ana", "ana@email.com");
        when(userRepository.findById(id)).thenReturn(Optional.of(entity));

        UserDto result = userService.findUser(id);

        assertEquals(id, result.getId());
        assertEquals("Ana", result.getUsername());
        assertEquals("ana@email.com", result.getEmail());
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void testFindUserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.findUser(id));
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void testCreateUser() {
        UUID id = UUID.randomUUID();
        UserDto dto = new UserDto(id, "Ana", "ana@email.com");
        User saved = new User(id, "Ana", "ana@email.com");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserDto result = userService.createUser(dto);

        assertEquals(id, result.getId());
        assertEquals("Ana", result.getUsername());
        assertEquals("ana@email.com", result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUserIdNull() {
        UserDto dto = new UserDto(null, "Ana", "ana@email.com");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDto result = userService.createUser(dto);

        assertNotNull(result.getId());
        assertEquals("Ana", result.getUsername());
        assertEquals("ana@email.com", result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testDeleteUser() {
        UUID id = UUID.randomUUID();

        when(userRepository.existsById(id)).thenReturn(true);

        userService.deleteUser(id);

        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    void testUpdateUser() {
        UUID id = UUID.randomUUID();
        User existing = new User(id, "Ana", "ana@email.com");
        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDto dto = new UserDto(id, "AnaUpdated", "anaupdated@email.com");

        userService.updateUser(id, dto);

        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).save(any(User.class));
        assertEquals("AnaUpdated", existing.getUsername());
        assertEquals("anaupdated@email.com", existing.getEmail());
    }

    @Test
    void testUpdateUserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.updateUser(id, new UserDto(id, "X", "x@x.com")));
        verify(userRepository, times(1)).findById(id);
        verify(userRepository, never()).save(any(User.class));
    }
}
