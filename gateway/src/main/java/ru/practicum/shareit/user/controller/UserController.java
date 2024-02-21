package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

@RestController
@RequestMapping("/users")
@Slf4j
@AllArgsConstructor
@Validated
public class UserController {

    private final UserClient userClient;

    @GetMapping("/{userId}")
    public ResponseEntity getUser(@PathVariable long userId) {
        log.info("Get user id={}", userId);
        return userClient.getUser(userId);
    }

    @GetMapping
    public ResponseEntity getAllUsers() {
        log.info("Get all users");
        return userClient.getAllUsers();
    }

    @PostMapping
    public ResponseEntity createUser(@Validated({Create.class}) @RequestBody UserDto userDto) {
        log.info("Creating user {}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity update(@Validated({Update.class}) @PathVariable long userId, @RequestBody UserDto userDto) {
        log.info("Updating user {}", userDto);
        return userClient.updateUser(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("Delete user id={}", userId);
        userClient.deleteUser(userId);
    }
}
