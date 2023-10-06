package ru.practicum.shareit.user.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        final User user = UserMapper.toUser(userDto);
        userRepository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        final User newUser = UserMapper.toUser(userDto);

        final User user = userRepository.findById(newUser.getUserId())
                .orElseThrow(() -> new ObjectNotFoundException("User with id=" + newUser.getUserId() + " not found"));

        if (newUser.getName() == null) {
            newUser.setName(user.getName());
        }
        if (newUser.getEmail() == null) {
            newUser.setEmail(user.getEmail());
        }
        userRepository.update(newUser);
        return UserMapper.toUserDto(newUser);
    }

    @Override
    public UserDto getUser(long userId) {
        return UserMapper.toUserDto(userRepository.findById(userId)
                .orElseThrow(() ->
                        new ObjectNotFoundException("User with id=" + userId + " not found")));

    }

    @Override
    public void deleteUser(long userId) {
        userRepository.findById(userId)
                .ifPresent(user -> userRepository.delete(userId));
    }
}
