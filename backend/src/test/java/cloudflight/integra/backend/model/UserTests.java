package cloudflight.integra.backend.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserTests {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testUserGettersSetters() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        user.setUsername("Ana");
        user.setEmail("ana@email.com");

        assertEquals(id, user.getId());
        assertEquals("Ana", user.getUsername());
        assertEquals("ana@email.com", user.getEmail());
    }

    @Test
    void testUserAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        User user = new User(id, "Ion", "ion@email.com");

        assertEquals(id, user.getId());
        assertEquals("Ion", user.getUsername());
        assertEquals("ion@email.com", user.getEmail());
    }

    @Test
    void testUserValidationSuccess() {
        User user = new User(UUID.randomUUID(), "Ana", "ana@email.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size());
    }

    @Test
    void testUserValidationFail_UsernameBlank() {
        User user = new User(UUID.randomUUID(), "", "ana@email.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    void testUserValidationFail_EmailInvalid() {
        User user = new User(UUID.randomUUID(), "Ana", "not-an-email");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

}
