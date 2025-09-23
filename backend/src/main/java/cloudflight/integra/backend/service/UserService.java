package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exception.UserNotFoundException;
import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.model.dtos.UserDto;
import cloudflight.integra.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(u -> new UserDto(u.getId(), u.getUsername(), u.getEmail()))
                .toList();
    }

    public UserDto findUser(UUID id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        return new UserDto(u.getId(), u.getUsername(), u.getEmail());
    }

    public UserDto createUser(UserDto dto) {
        UUID id = Optional.ofNullable(dto.getId())
                .orElse(UUID.randomUUID());
        User u = new User(id, dto.getUsername(), dto.getEmail());
        u = userRepository.save(u);
        return new UserDto(u.getId(), u.getUsername(), u.getEmail());
    }

    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User with id " + id + " not found");
        }
        userRepository.deleteById(id);
    }

    public void updateUser(UUID id, UserDto dto) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        existing.setUsername(dto.getUsername());
        existing.setEmail(dto.getEmail());
        userRepository.save(existing);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsernameIgnoreCase(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    public Optional<UserDto> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .map(u -> new UserDto(u.getId(), u.getUsername(), u.getEmail()));
    }
}
