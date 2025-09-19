package cloudflight.integra.backend.service;

import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.model.dtos.UserDto;
import cloudflight.integra.backend.model.mappers.UserMapper;
import cloudflight.integra.backend.repository.UserRepository;
import cloudflight.integra.backend.repository.initial.IUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    public UserService(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public List<UserDto> findAll() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public UserDto findUser(UUID id) {
        return mapper.toDto(repository.getReferenceById(id));
    }

    @Transactional
    public UserDto createUser(UserDto userDto) {
        return mapper.toDto(repository.save(mapper.toEntity(userDto)));
    }

    @Transactional
    public void deleteUser(UUID id) {
        repository.deleteById(id);
    }

    @Transactional
    public UserDto updateUser(UUID id, UserDto userDto) {
        User user = mapper.toEntity(userDto);
        user.setId(id);
        return mapper.toDto(repository.save(user));
    }
}
