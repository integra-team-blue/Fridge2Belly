package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.model.dtos.UserDto;
import cloudflight.integra.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
