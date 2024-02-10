package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ObjectValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @Transactional
    @Test
    void createUser_shouldCreateUser() {
        UserDto galinaDto = new UserDto(1L, "Galina", "galina@mail.ru");
        User galina = new User(1L, "Galina", "galina@mail.ru");

        when(userRepository.save(Mockito.any(User.class))).thenReturn(galina);

        UserDto createdUser = userService.createUser(galinaDto);

        assertNotNull(createdUser);
        assertEquals(createdUser.getId(), 1);
        assertEquals(galinaDto.getName(), createdUser.getName());
        assertEquals(galinaDto.getEmail(), createdUser.getEmail());

        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Transactional
    @Test
    void createUser_shouldThrowExceptionIfUserEmailIsAlreadyExists() {
        UserDto galinaDto = new UserDto(1L, "Galina", "galina@mail.ru");

        when(userRepository.save(Mockito.any(User.class)))
                .thenThrow(new ObjectValidationException("User with this email already exists."));

        ObjectValidationException exception = assertThrows(ObjectValidationException.class, () ->
                userService.createUser(galinaDto));

        assertEquals("User with this email already exists.", exception.getMessage());

        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Transactional
    @Test
    void updateUser_shouldThrowExceptionIfUserNotFound() {
        assertThrows(ObjectNotFoundException.class,
                () -> userService.updateUser(new UserDto(100L, "Galina K.", "galina.karlova@mail.ru"),
                        100L));
    }

    @Transactional
    @Test
    void updateUser_shouldUpdateIfEmailIsNull() {
        Long userId = 1L;
        UserDto updatedUserDto = new UserDto(1L, "Galina K.", null);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(new User(1L, "Galina", "galina@mail.ru")));

        UserDto replacedUser = userService.updateUser(updatedUserDto, userId);

        assertNotNull(replacedUser);
        assertEquals("Galina K.", updatedUserDto.getName());
        assertEquals("galina@mail.ru", replacedUser.getEmail());

        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Transactional
    @Test
    void updateUser_shouldUpdateIfNameIsNull() {
        Long userId = 1L;
        UserDto updatedUserDto = new UserDto(1L, null, "galina.karlova@mail.ru");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(new User(1L, "Galina", "galina@mail.ru")));

        UserDto updatedUser = userService.updateUser(updatedUserDto, userId);

        assertNotNull(updatedUser);
        assertEquals("Galina", updatedUser.getName());
        assertEquals("galina.karlova@mail.ru", updatedUserDto.getEmail());

        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Transactional
    @Test
    void updatedUser_shouldUpdateUser() {
        Long userId = 1L;
        UserDto galinaDto = new UserDto(1L, "Galina K.", "galina.karlova@mail.ru");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(new User(1L, "Galina", "galina@mail.ru")));

        UserDto updatedUserDto = userService.updateUser(galinaDto, userId);

        assertEquals("Galina K.", updatedUserDto.getName());
        assertEquals("galina.karlova@mail.ru", updatedUserDto.getEmail());

        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }

    @Transactional
    @Test
    void deleteById_shouldDeleteById() {
        User galina = new User(1L, "Galina", "galina@mail.ru");
        userService.deleteUser(galina.getId());

        assertTrue(userService.getAllUsers().isEmpty());
    }

    @Transactional
    @Test
    void getUser_shouldReturnUser() {
        Long userId = 1L;
        User galina = new User(1L, "Galina", "galina@mail.ru");

        when(userRepository.findById(userId)).thenReturn(Optional.of(galina));

        UserDto result = userService.getUser(userId);

        assertNotNull(result);
        assertEquals(galina.getName(), result.getName());
        assertEquals(galina.getEmail(), result.getEmail());

        Mockito.verify(userRepository).findById(userId);
    }

    @Transactional
    @Test
    void getUser_shouldThrowExceptionIfUserNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () ->
                userService.getUser(userId));

        assertEquals("User with id=" + userId + " not found", exception.getMessage());

        Mockito.verify(userRepository).findById(userId);
    }
}