package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exception.UserNotFoundException;
import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.model.dtos.UserDto;
import cloudflight.integra.backend.model.mappers.UserMapper;
import cloudflight.integra.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDtoServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private UserDto testUserDto;
    private User testUser;
    private UUID testId;

    @BeforeEach
    void setup() {
        testId = UUID.randomUUID();
        testUserDto = new UserDto(testId, "Ana", "ana@email.com");
        testUser = User.builder()
                .id(testId)
                .username("Ana")
                .email("ana@email.com")
                .build();
    }

    @Test
    void testFindAll() {
        List<User> users = Arrays.asList(testUser);
        List<UserDto> expectedDtos = Arrays.asList(testUserDto);

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        List<UserDto> result = userService.findAll();

        assertEquals(1, result.size());
        assertEquals("Ana",
                     result.get(0)
                             .getUsername());
        verify(userRepository).findAll();
        verify(userMapper).toDto(testUser);
    }

    @Test
    void testFindUser() {
        when(userRepository.findById(testId)).thenReturn(Optional.of(testUser));
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        UserDto result = userService.findUser(testId);

        assertEquals(testUserDto.getId(), result.getId());
        assertEquals("Ana", result.getUsername());
        verify(userRepository).findById(testId);
        verify(userMapper).toDto(testUser);
    }

    @Test
    void testFindUserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findUser(id));
        verify(userRepository).findById(id);
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void testCreateUser() {
        UserDto inputDto = new UserDto(testId, "Ana", "ana@email.com");
        UserDto expectedResult = new UserDto(testId, "Ana", "ana@email.com");

        when(userMapper.toEntity(any(UserDto.class))).thenReturn(testUser);
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(expectedResult);

        UserDto result = userService.createUser(inputDto);

        assertEquals(expectedResult.getId(), result.getId());
        assertEquals("Ana", result.getUsername());
        verify(userMapper).toEntity(any(UserDto.class));
        verify(userRepository).save(testUser);
        verify(userMapper).toDto(testUser);
    }

    @Test
    void testCreateUserIDNull() {
        UserDto inputDto = new UserDto(null, "Ana", "ana@email.com");
        UserDto expectedResult = new UserDto(testId, "Ana", "ana@email.com");

        when(userMapper.toEntity(any(UserDto.class))).thenReturn(testUser);
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(expectedResult);

        UserDto result = userService.createUser(inputDto);

        assertEquals(expectedResult.getId(), result.getId());
        assertEquals("Ana", result.getUsername());
        verify(userMapper).toEntity(any(UserDto.class));
        verify(userRepository).save(testUser);
        verify(userMapper).toDto(testUser);
    }

    @Test
    void testDeleteUser() {
        UUID id = UUID.randomUUID();
        when(userRepository.existsById(id)).thenReturn(true);
        doNothing().when(userRepository)
                .deleteById(id);

        userService.deleteUser(id);

        verify(userRepository).existsById(id);
        verify(userRepository).deleteById(id);
    }

    @Test
    void testDeleteUserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.existsById(id)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(id));
        verify(userRepository).existsById(id);
        verify(userRepository, never()).deleteById(id);
    }

    @Test
    void testUpdateUser() {
        UUID id = UUID.randomUUID();
        UserDto inputDto = new UserDto(null, "AnaUpdated", "anaupdated@email.com");

        User existingUser = User.builder()
                .id(id)
                .username("Ana")
                .email("ana@email.com")
                .build();


        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        userService.updateUser(id, inputDto);
        verify(userRepository).findById(id);
        verify(userRepository).save(existingUser);
        verifyNoInteractions(userMapper);
    }

    @Test
    void testUpdateUserNotFound() {
        UUID id = UUID.randomUUID();
        UserDto userDto = new UserDto(id, "AnaUpdated", "anaupdated@email.com");

        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(id, userDto));
        verify(userRepository).findById(id);
        verify(userRepository, never()).save(any());
        verifyNoInteractions(userMapper);
    }

    @Test
    void testExistsByUsername() {
        String username = "ana";
        when(userRepository.existsByUsernameIgnoreCase(username)).thenReturn(true);

        boolean result = userService.existsByUsername(username);

        assertEquals(true, result);
        verify(userRepository).existsByUsernameIgnoreCase(username);
    }

    @Test
    void testExistsByEmail() {
        String email = "ana@email.com";
        when(userRepository.existsByEmailIgnoreCase(email)).thenReturn(true);

        boolean result = userService.existsByEmail(email);

        assertEquals(true, result);
        verify(userRepository).existsByEmailIgnoreCase(email);
    }

    @Test
    void testFindByEmail() {
        String email = "ana@email.com";
        when(userRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(testUser));

        Optional<UserDto> result = userService.findByEmail(email);

        assertEquals(true, result.isPresent());
        assertEquals(testUser.getId(),
                     result.get()
                             .getId());
        assertEquals(testUser.getUsername(),
                     result.get()
                             .getUsername());
        assertEquals(testUser.getEmail(),
                     result.get()
                             .getEmail());
        verify(userRepository).findByEmailIgnoreCase(email);
    }

    @Test
    void testFindByEmailNotFound() {
        String email = "notfound@email.com";
        when(userRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.empty());

        Optional<UserDto> result = userService.findByEmail(email);

        assertEquals(false, result.isPresent());
        verify(userRepository).findByEmailIgnoreCase(email);
    }
}
