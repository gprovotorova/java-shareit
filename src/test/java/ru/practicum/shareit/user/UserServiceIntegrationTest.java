package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@Rollback
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class UserServiceIntegrationTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Test
    public void testGetAllUsers() {
        User galina = new User(
                1L,
                "Galina",
                "galina@mail.ru");

        User anna =  new User(
                2L,
                "Anna",
                "anna@mail.ru");

        userRepository.save(galina);
        userRepository.save(anna);

        List<UserDto> users = userService.getAllUsers();

        assertTrue(users.size() > 0);
        assertEquals(2, users.size());

        assertEquals("Galina", users.get(0).getName());
        assertEquals("galina@mail.ru", users.get(0).getEmail());
        assertEquals("Anna", users.get(1).getName());
        assertEquals("anna@mail.ru", users.get(1).getEmail());
    }
}
