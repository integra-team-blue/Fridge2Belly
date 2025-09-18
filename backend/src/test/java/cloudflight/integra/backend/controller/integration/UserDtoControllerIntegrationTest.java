package cloudflight.integra.backend.controller.integration;

import cloudflight.integra.backend.model.dtos.UserDto;
import cloudflight.integra.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserDtoControllerIntegrationTest {
    @Container
    @ServiceConnection
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:14.6")
        .withDatabaseName("integration-tests-db")
        .withUsername("it")
        .withPassword("it");

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
        UserDto userDto1 = userService.createUser(new UserDto(null, "Ana", "ana@email.com"));
        UserDto userDto2 = userService.createUser(new UserDto(null, "Ion", "ion@email.com"));

        ResponseEntity<UserDto[]> response = restTemplate.getForEntity(baseUrl, UserDto[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        UserDto[] userDtos = response.getBody();
        assertThat(userDtos).hasSize(2);
        assertThat(userDtos[0].getUsername()).isEqualTo("Ana");
        assertThat(userDtos[1].getUsername()).isEqualTo("Ion");

        userService.deleteUser(userDto1.getId());
        userService.deleteUser(userDto2.getId());
    }

    // GET /users/{id}
    @Test
    void testFindUser() {
        UserDto saved = userService.createUser(new UserDto(null, "Ana", "ana@email.com"));

        ResponseEntity<UserDto> response = restTemplate.getForEntity(baseUrl + "/" + saved.getId(), UserDto.class);

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
        UserDto userDto = new UserDto(null, "Ana", "ana@email.com");

        ResponseEntity<UserDto> postResponse = restTemplate.postForEntity(baseUrl, userDto, UserDto.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<UserDto[]> getResponse = restTemplate.getForEntity(baseUrl, UserDto[].class);
        assertThat(getResponse.getBody()).hasSize(1);
        UserDto[] userDtos = getResponse.getBody();

        userService.deleteUser(userDtos[0].getId());
    }

    @Test
    void testAddUserInvalid() {
        UserDto userDto = new UserDto(null, "A", "not-an-email");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, entity, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);


    }

    // PUT /users/{id}
    @Test
    void testUpdateUser() {
        UserDto saved = userService.createUser(new UserDto(null, "Ana", "ana@email.com"));
        UserDto updated = new UserDto(null, "AnaUpdated", "anaupdated@email.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserDto> entity = new HttpEntity<>(updated, headers);

        ResponseEntity<Void> putResponse = restTemplate.exchange(baseUrl + "/" + saved.getId(),
                HttpMethod.PUT, entity, Void.class);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<UserDto> getResponse = restTemplate.getForEntity(baseUrl + "/" + saved.getId(), UserDto.class);
        assertThat(Objects.requireNonNull(getResponse.getBody()).getUsername()).isEqualTo("AnaUpdated");

        userService.deleteUser(saved.getId());

    }

    @Test
    void testUpdateUserNotFound() {
        UUID id = UUID.randomUUID();
        UserDto updated = new UserDto(null, "AnaUpdated", "anaupdated@email.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserDto> entity = new HttpEntity<>(updated, headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/" + id,
                HttpMethod.PUT, entity, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("User with id " + id + " not found");
    }

    // DELETE /users/{id}
    @Test
    void testDeleteUser() {
        UserDto saved = userService.createUser(new UserDto(null, "Ana", "ana@email.com"));

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
