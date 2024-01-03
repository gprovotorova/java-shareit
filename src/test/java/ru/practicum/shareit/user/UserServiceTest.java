package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {
    private final UserService userService;
    private final UserDto galina = new UserDto(
            1L,
            "Galina",
            "galina@mail.ru");

    @Test
    void updateUser_shouldThrowExceptionIfUserNotFound() {
        assertThrows(ObjectNotFoundException.class,
                () -> userService.updateUser(new UserDto(100L, "example", "example@mail.ru"),
                        100L));
    }

    @Test
    void update_shouldUpdateUser() {
        UserDto user = userService.createUser(galina);
        user.setName("Galina K.");
        UserDto updatedUser = userService.updateUser(user, user.getId());

        assertEquals(updatedUser.getName(), user.getName());
    }

    @Test
    void update_shouldUpdateIfNameIsNull() {
        UserDto user = userService.createUser(galina);
        user.setName(null);
        user.setEmail("galina.karlova@mail.ru");
        UserDto updatedUser = userService.updateUser(user, user.getId());

        assertEquals(updatedUser.getEmail(), user.getEmail());
    }

    @Test
    void update_shouldUpdateIfEmailIsNull() {
        UserDto user = userService.createUser(galina);
        user.setName("Galina K.");
        user.setEmail(null);
        UserDto updatedUser = userService.updateUser(user, user.getId());

        assertEquals(updatedUser.getName(), user.getName());
    }

    @Test
    void deleteById_shouldDeleteById() {
        UserDto user = userService.createUser(galina);
        userService.deleteUser(user.getId());

        assertTrue(userService.getAllUsers().isEmpty());
    }

    @Test
    void getUsers_shouldReturnListOfUsers() {
        UserDto firstUser = userService.createUser(new UserDto(3L, "Anna", "anna@mail.ru"));
        UserDto secondUser = userService.createUser(new UserDto(5L, "Kate", "kate@mail.ru"));

        assertEquals(2, userService.getAllUsers().size());
        assertEquals(userService.getAllUsers().get(0), firstUser);
        assertEquals(userService.getAllUsers().get(1), secondUser);
    }
}
