package ru.practicum.shareit.user.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
<<<<<<< HEAD
import ru.practicum.shareit.exception.ObjectExistException;
=======
>>>>>>> 61d3a36fb68671b2bc56a32d663def57fc07f660
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
<<<<<<< HEAD
    private UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
=======
    UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers()
>>>>>>> 61d3a36fb68671b2bc56a32d663def57fc07f660
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
<<<<<<< HEAD
        User user = UserMapper.toUser(userDto);
        try{
            userRepository.save(user);
        } catch (ObjectExistException e) {
            throw new ObjectExistException("User with this email already exists.");
        }
=======
        final User user = UserMapper.toUser(userDto);
        userRepository.save(user);
>>>>>>> 61d3a36fb68671b2bc56a32d663def57fc07f660
        return UserMapper.toUserDto(user);
    }

    @Override
<<<<<<< HEAD
    public UserDto updateUser(UserDto userDto, long userId) {
        User updatedUser = UserMapper.toUser(getUser(userId));
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            updatedUser.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            updatedUser.setName(userDto.getName());
        }
        userRepository.save(updatedUser);
        return UserMapper.toUserDto(updatedUser);
=======
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
>>>>>>> 61d3a36fb68671b2bc56a32d663def57fc07f660
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
<<<<<<< HEAD
                .ifPresent(user -> userRepository.deleteById(userId));
=======
                .ifPresent(user -> userRepository.delete(userId));
>>>>>>> 61d3a36fb68671b2bc56a32d663def57fc07f660
    }
}
