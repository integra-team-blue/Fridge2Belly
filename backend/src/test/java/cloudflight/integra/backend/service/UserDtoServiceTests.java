package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exception.UserNotFoundException;
import cloudflight.integra.backend.model.dtos.UserDto;
import cloudflight.integra.backend.repository.initial.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserDtoServiceTests {

    private IUserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setup() {
        userRepository = Mockito.mock(IUserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    void testFindAll() {
        List<UserDto> userDtos = Arrays.asList(
                new UserDto(UUID.randomUUID(), "Ana", "ana@email.com"),
                new UserDto(UUID.randomUUID(), "Ion", "ion@email.com")
        );

        when(userRepository.getAll()).thenReturn(userDtos);

        List<UserDto> result = userService.findAll();
        assertEquals(userDtos, result);

        verify(userRepository, times(1)).getAll();
    }

    @Test
    void testFindUser() {
        UUID id = UUID.randomUUID();
        UserDto userDto = new UserDto(id, "Ana", "ana@email.com");

        when(userRepository.getUser(id)).thenReturn(userDto);

        UserDto result = userService.findUser(id);
        assertEquals(userDto, result);

        verify(userRepository, times(1)).getUser(id);
    }

    @Test
    void testFindUserNotFound() {
        UUID id = UUID.randomUUID();

        when(userRepository.getUser(id)).thenThrow(new UserNotFoundException("User with id " + id + " not found"));

        assertThrows(UserNotFoundException.class, () -> userService.findUser(id));
        verify(userRepository, times(1)).getUser(id);
    }

    @Test
    void testCreateUser() {
        UserDto userDto = new UserDto(UUID.randomUUID(), "Ana", "ana@email.com");

        userService.createUser(userDto);

        verify(userRepository, times(1)).create(userDto);
    }

    @Test
    void testCreateUserIDNull() {
        UserDto userDto = new UserDto(null, "Ana", "ana@email.com");

        userService.createUser(userDto);

        verify(userRepository, times(1)).create(userDto);
    }

    @Test
    void testDeleteUser() {
        UUID id = UUID.randomUUID();

        userService.deleteUser(id);

        verify(userRepository, times(1)).delete(id);
    }

    @Test
    void testDeleteUserNotFound() {
        UUID id = UUID.randomUUID();
        doThrow(new UserNotFoundException("User with id " + id + " not found"))
                .when(userRepository).delete(id);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(id));
        verify(userRepository, times(1)).delete(id);
    }

    @Test
    void testUpdateUser() {
        UUID id = UUID.randomUUID();
        UserDto userDto = new UserDto(id, "AnaUpdated", "anaupdated@email.com");

        userService.updateUser(id, userDto);

        verify(userRepository, times(1)).update(userDto);
    }

    @Test
    void testUpdateUserNotFound() {
        UUID id = UUID.randomUUID();
        UserDto userDto = new UserDto(id, "AnaUpdated", "anaupdated@email.com");
        doThrow(new UserNotFoundException("User with id " + userDto.getId() + " not found"))
                .when(userRepository).update(userDto);

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(id, userDto));
        verify(userRepository, times(1)).update(userDto);
    }
}
