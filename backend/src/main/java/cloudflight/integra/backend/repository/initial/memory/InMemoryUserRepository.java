package cloudflight.integra.backend.repository.initial.memory;

import cloudflight.integra.backend.exception.UserNotFoundException;
import cloudflight.integra.backend.model.dtos.UserDto;
import cloudflight.integra.backend.repository.initial.IUserRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class InMemoryUserRepository implements IUserRepository {

    private final List<UserDto> userDtos = new ArrayList<>();

    @Override
    public List<UserDto> getAll() {
        return new ArrayList<>(userDtos);
    }

    @Override
    public UserDto getUser(UUID id) {
        return userDtos.stream().filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    }

    @Override
    public UserDto create(UserDto userDto) {
        userDtos.add(userDto);
        return userDto;
    }

    @Override
    public void delete(UUID id) {
        boolean removed = userDtos.removeIf(u -> u.getId().equals(id));
        if (!removed) {
            throw new UserNotFoundException("User with id " + id + " not found");
        }
    }

    @Override
    public void update(UserDto userDto) {
        userDtos.stream()
                .filter(u -> u.getId().equals(userDto.getId()))
                .findFirst()
                .map(u -> {
                    u.setUsername(userDto.getUsername());
                    u.setEmail(userDto.getEmail());
                    return u;
                })
                .orElseThrow(() -> new UserNotFoundException("User with id " + userDto.getId() + " not found"));
    }

}
