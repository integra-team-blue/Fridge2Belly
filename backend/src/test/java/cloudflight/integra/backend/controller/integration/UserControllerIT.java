package cloudflight.integra.backend.controller.integration;

import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserService userService;

    private String baseUrl;

    @BeforeEach
    void setup() {
        baseUrl = "http://localhost:" + port + "/users";
    }

    // GET /users
    @Test
    void testFindAll() {
        User user1 = userService.createUser(new User(null, "Ana", "ana@email.com"));
        User user2 = userService.createUser(new User(null, "Ion", "ion@email.com"));

        ResponseEntity<User[]> response = restTemplate.getForEntity(baseUrl, User[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        User[] users = response.getBody();
        assertThat(users).hasSize(2);
        assertThat(users[0].getUsername()).isEqualTo("Ana");
        assertThat(users[1].getUsername()).isEqualTo("Ion");

        userService.deleteUser(user1.getId());
        userService.deleteUser(user2.getId());
    }

    // GET /users/{id}
    @Test
    void testFindUser() {
        User saved = userService.createUser(new User(null, "Ana", "ana@email.com"));

        ResponseEntity<User> response = restTemplate.getForEntity(baseUrl + "/" + saved.getId(), User.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getUsername()).isEqualTo("Ana");

        userService.deleteUser(saved.getId());
    }

    @Test
    void testNotFindUser() {
        UUID id = UUID.randomUUID();

        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/" + id, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("User with id " + id + " not found");
    }

    // POST /users
    @Test
    void testAddUserValid() {
        User user = new User(null, "Ana", "ana@email.com");

        ResponseEntity<User> postResponse = restTemplate.postForEntity(baseUrl, user, User.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<User[]> getResponse = restTemplate.getForEntity(baseUrl, User[].class);
        assertThat(getResponse.getBody()).hasSize(1);
        User[] users = getResponse.getBody();

        userService.deleteUser(users[0].getId());
    }

    @Test
    void testAddUserInvalid() {
        User user = new User(null, "A", "not-an-email");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<User> entity = new HttpEntity<>(user, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, entity, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);


    }

    // PUT /users/{id}
    @Test
    void testUpdateUser() {
        User saved = userService.createUser(new User(null, "Ana", "ana@email.com"));
        User updated = new User(null, "AnaUpdated", "anaupdated@email.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<User> entity = new HttpEntity<>(updated, headers);

        ResponseEntity<Void> putResponse = restTemplate.exchange(baseUrl + "/" + saved.getId(),
                HttpMethod.PUT, entity, Void.class);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<User> getResponse = restTemplate.getForEntity(baseUrl + "/" + saved.getId(), User.class);
        assertThat(Objects.requireNonNull(getResponse.getBody()).getUsername()).isEqualTo("AnaUpdated");

        userService.deleteUser(saved.getId());

    }

    @Test
    void testUpdateUserNotFound() {
        UUID id = UUID.randomUUID();
        User updated = new User(null, "AnaUpdated", "anaupdated@email.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<User> entity = new HttpEntity<>(updated, headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/" + id,
                HttpMethod.PUT, entity, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("User with id " + id + " not found");
    }

    // DELETE /users/{id}
    @Test
    void testDeleteUser() {
        User saved = userService.createUser(new User(null, "Ana", "ana@email.com"));

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(baseUrl + "/" + saved.getId(),
                HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate.getForEntity(baseUrl + "/" + saved.getId(), String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    void testDeleteUserNotFound() {
        UUID id = UUID.randomUUID();

        ResponseEntity<String> deleteResponse = restTemplate.exchange(baseUrl + "/" + id,
                HttpMethod.DELETE, null, String.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(deleteResponse.getBody()).contains("User with id " + id + " not found");
    }
}
