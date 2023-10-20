package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto createUser(UserDto userDto);

<<<<<<< HEAD
    UserDto updateUser(UserDto userDto, long userId);
=======
    UserDto updateUser(UserDto userDto);
>>>>>>> 61d3a36fb68671b2bc56a32d663def57fc07f660

    UserDto getUser(long userId);

    void deleteUser(long userId);
}
