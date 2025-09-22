package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.exception.UserNotFoundException;
import cloudflight.integra.backend.model.dtos.UserDto;
import cloudflight.integra.backend.repository.initial.memory.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserDtoRepoTests {

    private InMemoryUserRepository repository;

    @BeforeEach
    void setup() {
        repository = new InMemoryUserRepository();
    }

    @Test
    void testCreateAndGetAllUsers() {
        UserDto userDto1 = new UserDto(UUID.randomUUID(), "Ana", "ana@email.com");
        UserDto userDto2 = new UserDto(UUID.randomUUID(), "Ion", "ion@email.com");

        repository.create(userDto1);
        repository.create(userDto2);

        List<UserDto> userDtos = repository.getAll();
        assertEquals(2, userDtos.size());
        assertTrue(userDtos.contains(userDto1));
        assertTrue(userDtos.contains(userDto2));
    }

    @Test
    void testGetUserById() {
        UUID id = UUID.randomUUID();
        UserDto userDto = new UserDto(id, "Ana", "ana@email.com");
        repository.create(userDto);

        UserDto found = repository.getUser(id);
        assertEquals(userDto, found);
    }

    @Test
    void testGetUserByIdNotFound() {
        UUID id = UUID.randomUUID();
        assertThrows(UserNotFoundException.class, () -> repository.getUser(id));
    }

    @Test
    void testDeleteUser() {
        UUID id = UUID.randomUUID();
        UserDto userDto = new UserDto(id, "Ana", "ana@email.com");
        repository.create(userDto);

        repository.delete(id);

        assertEquals(0,
                     repository.getAll()
                             .size());
    }

    @Test
    void testDeleteUserNotFound() {
        UUID id = UUID.randomUUID();
        assertThrows(UserNotFoundException.class, () -> repository.delete(id));
    }

    @Test
    void testUpdateUser() {
        UUID id = UUID.randomUUID();
        UserDto userDto = new UserDto(id, "Ana", "ana@email.com");
        repository.create(userDto);

        UserDto updatedUserDto = new UserDto(id, "AnaUpdated", "anaupdated@email.com");
        repository.update(updatedUserDto);

        UserDto found = repository.getUser(id);
        assertEquals("AnaUpdated", found.getUsername());
        assertEquals("anaupdated@email.com", found.getEmail());
    }

    @Test
    void testUpdateUserNotFound() {
        UUID id = UUID.randomUUID();
        UserDto userDto = new UserDto(id, "AnaUpdated", "anaupdated@email.com");

        assertThrows(UserNotFoundException.class, () -> repository.update(userDto));
    }
}
