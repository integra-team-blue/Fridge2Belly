package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.model.dtos.UserDto;
import cloudflight.integra.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    @GetMapping("/users/{id}")
    public UserDto findUser(@PathVariable UUID id) {
        return userService.findUser(id);
    }

    @PutMapping("/users/{id}")
    public void updateUser(@PathVariable UUID id, @Valid @RequestBody UserDto userDto) {
        userService.updateUser(id, userDto);
    }

    @PostMapping("/users")
    public ResponseEntity<UserDto> addUser(@Valid @RequestBody UserDto userDto) {
        userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userDto);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/users/check-username")
    public Map<String, Boolean> checkUsername(@RequestParam String username) {
        boolean taken = userService.existsByUsername(username);
        return Map.of("taken", taken);
    }

    @GetMapping("/users/check-email")
    public Map<String, Boolean> checkEmail(@RequestParam String email) {
        boolean taken = userService.existsByEmail(email);
        return Map.of("taken", taken);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest body) {
        if (body == null || body.getEmail() == null || body.getEmail()
                .isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .build();
        }
        return userService.findByEmail(body.getEmail())
                .map(userDto -> ResponseEntity.ok(new AuthResponse("temp-token", userDto)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .build());
    }

    static class LoginRequest {
        private String email;

        public String getEmail() { return email; }

        public void setEmail(String email) { this.email = email; }
    }

    static class AuthResponse {
        private String token;
        private UserDto user;

        public AuthResponse(String token, UserDto user) {
            this.token = token;
            this.user = user;
        }

        public String getToken() { return token; }

        public UserDto getUser() { return user; }

        public void setToken(String token) { this.token = token; }

        public void setUser(UserDto user) { this.user = user; }
    }
}
