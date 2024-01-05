package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    private final UserDto galina = new UserDto(
            1L,
            "Galina",
            "galina@mail.ru");

    private final List<UserDto> listUserDto = List.of(
            new UserDto(2L, "Anna", "anna@mail.ru"),
            new UserDto(3L, "Kate", "kate@mail.ru"));

    @Test
    @SneakyThrows
    void createUser_shouldCreateUser() {
        when(userService.createUser(any()))
                .thenReturn(galina);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(galina))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(galina.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(galina.getName())))
                .andExpect(jsonPath("$.email", is(galina.getEmail())));
    }

    @Test
    @SneakyThrows
    void getById_shouldFindUserById() {
        when(userService.getUser(any(Long.class)))
                .thenReturn(galina);

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(galina.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(galina.getName())))
                .andExpect(jsonPath("$.email", is(galina.getEmail())));
    }

    @Test
    @SneakyThrows
    void deleteUser_shouldDeleteUserById() {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void updateUser_shouldUpdateUserData() {
        when(userService.updateUser(any(), any()))
                .thenReturn(galina);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(galina))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(galina.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(galina.getName())))
                .andExpect(jsonPath("$.email", is(galina.getEmail())));
    }

    @Test
    @SneakyThrows
    void getAllUsers_shouldReturnListOfUsers() {
        when(userService.getAllUsers())
                .thenReturn(listUserDto);
        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(listUserDto)));
    }
}
