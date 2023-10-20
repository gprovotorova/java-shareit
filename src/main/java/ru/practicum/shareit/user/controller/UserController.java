package ru.practicum.shareit.user.controller;

<<<<<<< HEAD
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
=======
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
>>>>>>> 61d3a36fb68671b2bc56a32d663def57fc07f660
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;
<<<<<<< HEAD

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@AllArgsConstructor
@Validated
public class UserController {

=======
import ru.practicum.shareit.user.service.ValidateService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserController {

    @Autowired
    private ValidateService validateService;
    @Autowired
>>>>>>> 61d3a36fb68671b2bc56a32d663def57fc07f660
    private final UserServiceImpl userService;

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable long userId) {
        log.info("Get user id={}", userId);
        return userService.getUser(userId);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Get all users = ", userService.getAllUsers().size());
        return userService.getAllUsers();
    }

    @PostMapping
    public UserDto createUser(@Validated({Create.class}) @RequestBody UserDto userDto) {
        log.info("Creating user {}", userDto);
<<<<<<< HEAD
=======
        validateService.userEmailValidation(userDto);
>>>>>>> 61d3a36fb68671b2bc56a32d663def57fc07f660
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@Validated({Update.class}) @PathVariable long userId, @RequestBody UserDto userDto) {
<<<<<<< HEAD
        log.info("Updating user {}", userDto);
        return userService.updateUser(userDto, userId);
=======
        userDto.setId(userId);
        log.info("Updating user {}", userDto);
        return userService.updateUser(userDto);
>>>>>>> 61d3a36fb68671b2bc56a32d663def57fc07f660
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("Delete user id={}", userId);
        userService.deleteUser(userId);
    }
}
