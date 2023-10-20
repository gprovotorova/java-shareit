package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ObjectExistException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Component
public class ValidateService {
    @Autowired
    UserRepository userRepository;

    public void userEmailValidation(UserDto userDto) {
        final User user = UserMapper.toUser(userDto);
        List<User> users = userRepository.getAllUsers();
        for (User value : users) {
            if (user.getEmail().equals(value.getEmail())) {
                throw new ObjectExistException("This email is already in use.");
            }
        }
    }
}