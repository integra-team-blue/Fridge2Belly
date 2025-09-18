package cloudflight.integra.backend.repository.initial;

import cloudflight.integra.backend.model.dtos.UserDto;

import java.util.List;
import java.util.UUID;

public interface IUserRepository {
    List<UserDto> getAll();
    UserDto getUser(UUID id);
    UserDto create(UserDto userDto);
    void delete(UUID id);
    void update(UserDto userDto);
}
