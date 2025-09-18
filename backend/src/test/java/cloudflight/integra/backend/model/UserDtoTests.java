package cloudflight.integra.backend.model;

import cloudflight.integra.backend.model.dtos.UserDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserDtoTests {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testUserGettersSetters() {
        UUID id = UUID.randomUUID();
        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setUsername("Ana");
        userDto.setEmail("ana@email.com");

        assertEquals(id, userDto.getId());
        assertEquals("Ana", userDto.getUsername());
        assertEquals("ana@email.com", userDto.getEmail());
    }

    @Test
    void testUserAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        UserDto userDto = new UserDto(id, "Ion", "ion@email.com");

        assertEquals(id, userDto.getId());
        assertEquals("Ion", userDto.getUsername());
        assertEquals("ion@email.com", userDto.getEmail());
    }

    @Test
    void testUserValidationSuccess() {
        UserDto userDto = new UserDto(UUID.randomUUID(), "Ana", "ana@email.com");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertEquals(0, violations.size());
    }

    @Test
    void testUserValidationFail_UsernameBlank() {
        UserDto userDto = new UserDto(UUID.randomUUID(), "", "ana@email.com");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    void testUserValidationFail_EmailInvalid() {
        UserDto userDto = new UserDto(UUID.randomUUID(), "Ana", "not-an-email");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

}
