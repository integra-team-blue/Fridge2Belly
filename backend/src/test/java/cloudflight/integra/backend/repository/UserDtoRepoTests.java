package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserDtoRepoTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User createSampleUser(String username, String email) {
        return User.builder()
                .username(username)
                .email(email)
                .build();
    }

    @Test
    void testSaveAndFindAllUsers() {
        User user1 = createSampleUser("Ana", "ana@email.com");
        User user2 = createSampleUser("Ion", "ion@email.com");

        userRepository.save(user1);
        userRepository.save(user2);

        List<User> users = userRepository.findAll();

        assertEquals(2, users.size());
        assertTrue(users.stream()
                .anyMatch(u -> u.getUsername()
                        .equals("Ana")));
        assertTrue(users.stream()
                .anyMatch(u -> u.getUsername()
                        .equals("Ion")));
    }

    @Test
    void testFindUserById() {
        User user = createSampleUser("Ana", "ana@email.com");
        User savedUser = userRepository.save(user);

        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals("Ana",
                     foundUser.get()
                             .getUsername());
        assertEquals("ana@email.com",
                     foundUser.get()
                             .getEmail());
    }

    @Test
    void testFindUserByIdNotFound() {
        UUID randomId = UUID.randomUUID();
        Optional<User> notFound = userRepository.findById(randomId);

        assertTrue(notFound.isEmpty());
    }

    @Test
    void testDeleteUser() {
        User user = createSampleUser("Ana", "ana@email.com");
        User savedUser = userRepository.save(user);

        assertTrue(userRepository.existsById(savedUser.getId()));

        userRepository.deleteById(savedUser.getId());

        assertFalse(userRepository.existsById(savedUser.getId()));
        assertEquals(0,
                     userRepository.findAll()
                             .size());
    }

    @Test
    void testDeleteUserNotFound() {
        UUID randomId = UUID.randomUUID();

        assertDoesNotThrow(() -> userRepository.deleteById(randomId));
    }

    @Test
    void testUpdateUser() {
        User user = createSampleUser("Ana", "ana@email.com");
        User savedUser = userRepository.save(user);

        savedUser.setUsername("AnaUpdated");
        savedUser.setEmail("anaupdated@email.com");

        User updatedUser = userRepository.save(savedUser);

        assertEquals("AnaUpdated", updatedUser.getUsername());
        assertEquals("anaupdated@email.com", updatedUser.getEmail());
    }

    @Test
    void testExistsById() {
        User user = createSampleUser("TestUser", "test@email.com");
        User savedUser = userRepository.save(user);

        assertTrue(userRepository.existsById(savedUser.getId()));
        assertFalse(userRepository.existsById(UUID.randomUUID()));
    }

    @Test
    void testFindByEmailIgnoreCase() {
        User user = createSampleUser("Ana", "ana@email.com");
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmailIgnoreCase("ANA@EMAIL.COM");

        assertTrue(foundUser.isPresent());
        assertEquals("Ana",
                     foundUser.get()
                             .getUsername());
    }

    @Test
    void testExistsByUsernameIgnoreCase() {
        User user = createSampleUser("Ana", "ana@email.com");
        userRepository.save(user);

        assertTrue(userRepository.existsByUsernameIgnoreCase("ana"));
        assertTrue(userRepository.existsByUsernameIgnoreCase("ANA"));
        assertFalse(userRepository.existsByUsernameIgnoreCase("Ion"));
    }

    @Test
    void testExistsByEmailIgnoreCase() {
        User user = createSampleUser("Ana", "ana@email.com");
        userRepository.save(user);

        assertTrue(userRepository.existsByEmailIgnoreCase("ana@email.com"));
        assertTrue(userRepository.existsByEmailIgnoreCase("ANA@EMAIL.COM"));
        assertFalse(userRepository.existsByEmailIgnoreCase("ion@email.com"));
    }

    @Test
    void testUserConstraints() {
        User user1 = createSampleUser("UniqueUser", "unique@email.com");
        userRepository.save(user1);

        // Test unique username constraint
        User user2 = createSampleUser("UniqueUser", "different@email.com");
        assertThrows(Exception.class, () -> {
            userRepository.save(user2);
            entityManager.flush(); // Force constraint violation
        });

        // Test unique email constraint
        User user3 = createSampleUser("DifferentUser", "unique@email.com");
        assertThrows(Exception.class, () -> {
            userRepository.save(user3);
            entityManager.flush(); // Force constraint violation
        });
    }
}
