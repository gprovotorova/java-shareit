package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ObjectExistException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserRepositoryTest {
    @Mock
    private UserRepository userRepository;
    private UserService userService;
    private final UserMapper mapper = new UserMapper();

    private UserDto galina = new UserDto(
            1L,
            "Galina",
            "galina@mail.ru");


    @BeforeEach
    void beforeEach() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void getById_shouldReturnUserById() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(mapper.toUser(galina)));
        UserDto thisUser = userService.getUser(1L);
        verify(userRepository, Mockito.times(1)).findById(1L);

        assertThat(galina.getName(), equalTo(thisUser.getName()));
        assertThat(galina.getEmail(), equalTo(thisUser.getEmail()));
    }

    @Test
    void getById_shouldThrowExceptionIfUserIdIsInvalid() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        final ObjectNotFoundException exception = assertThrows(
                ObjectNotFoundException.class, () -> userService.getUser(100L));
        assertEquals("User with id=" + 100 + " not found", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfEmailExists() {
        when(userRepository.save(any()))
                .thenThrow(new ObjectExistException("User with this email already exists."));
        final ObjectExistException exception = assertThrows(
                ObjectExistException.class, () -> userService.createUser(galina));
        assertEquals("User with this email already exists.", exception.getMessage());
    }
}
