package cloudflight.integra.backend.service;

import cloudflight.integra.backend.model.dtos.UserDto;
import cloudflight.integra.backend.repository.initial.IUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> findAll() {
        return userRepository.getAll();
    }

    public UserDto findUser(UUID id) {
        return userRepository.getUser(id);
    }

    public UserDto createUser(UserDto userDto) {
        if(userDto.getId() == null)
            userDto.setId(UUID.randomUUID());
        return userRepository.create(userDto);
    }

    public void deleteUser(UUID id) {
        userRepository.delete(id);
    }

    public void updateUser(UUID id, UserDto userDto) {
        userDto.setId(id);
        userRepository.update(userDto);
    }
}
