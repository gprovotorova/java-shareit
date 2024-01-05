package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceTest {
    private final ItemRequestService requestService;
    private final UserService userService;

    private final UserDto firstUser = new UserDto(
            1L,
            "Galina",
            "galina@mail.ru");
    private final UserDto secondUser = new UserDto(
            2L,
            "Ivan",
            "ivan@mail.ru");
    private final ItemRequestDto requestDto = new ItemRequestDto(
            3L,
            "I'm looking for a lamp",
            UserMapper.toUser(firstUser),
            LocalDateTime.of(2023, 12, 28, 22, 40, 0),
            null);

    @Test
    void create_shouldCreateRequest() {
        UserDto user = userService.createUser(firstUser);
        ItemRequestDto request = requestService.createRequest(requestDto, user.getId(),
                LocalDateTime.of(2023, 12, 28, 22, 40, 0));

        assertThat(request.getDescription(), equalTo(request.getDescription()));
    }

    @Test
    void create_shouldThrowExceptionIfUserIdIsIncorrect() {
        assertThrows(ObjectNotFoundException.class,
                () -> requestService.createRequest(requestDto, 100L, LocalDateTime.now()));
    }

    @Test
    void getRequestsByOwner_shouldReturnRequests() {
        UserDto user = userService.createUser(firstUser);
        ItemRequestDto itemRequestDto = requestService.createRequest(requestDto, user.getId(),
                LocalDateTime.of(2023, 12, 28, 22, 40, 0));
        List<ItemRequestDto> returnedRequest = requestService.getRequestsByOwner(user.getId());

        assertFalse(returnedRequest.isEmpty());
        assertTrue(returnedRequest.contains(itemRequestDto));
    }

    @Test
    void getRequestsByOwner_shouldThrowExceptionIfUserIdIsIncorrect() {
        assertThrows(ObjectNotFoundException.class,
                () -> requestService.getRequestsByOwner(100L));
    }

    @Test
    void getExistingRequests_shouldReturnExistingRequests() {
        UserDto firstUserDto = userService.createUser(firstUser);
        UserDto secondUserDto = userService.createUser(secondUser);
        ItemRequestDto itemRequestDto = requestService.createRequest(requestDto, firstUserDto.getId(),
                LocalDateTime.of(2023, 12, 28, 22, 40, 0));
        List<ItemRequestDto> returnedRequest = requestService.getAllRequests(secondUserDto.getId(), 0, 10);

        assertFalse(returnedRequest.isEmpty());
        assertTrue(returnedRequest.contains(itemRequestDto));
    }

    @Test
    void getExistingRequests_shouldThrowExceptionIfUserIdIsIncorrect() {
        assertThrows(ObjectNotFoundException.class,
                () -> requestService.getAllRequests(100L, 0, 10));
    }

    @Test
    void getExistingRequests_shouldReturnExistingRequestsIfSizeIsNull() {
        UserDto firstUserDto = userService.createUser(firstUser);
        UserDto secondUserDto = userService.createUser(secondUser);
        ItemRequestDto itemRequestDto = requestService.createRequest(requestDto, firstUserDto.getId(),
                LocalDateTime.of(2023, 12, 28, 22, 40, 0));
        List<ItemRequestDto> returnedRequest = requestService.getAllRequests(secondUserDto.getId(), 0, null);

        assertFalse(returnedRequest.isEmpty());
        assertTrue(returnedRequest.contains(itemRequestDto));
    }

    @Test
    void getRequestById_shouldReturnRequest() {
        UserDto userDto = userService.createUser(firstUser);
        ItemRequestDto itemRequestDto = requestService.createRequest(requestDto, userDto.getId(),
                LocalDateTime.of(2023, 12, 28, 22, 40, 0));
        ItemRequestDto returnedRequest = requestService.getRequestById(userDto.getId(), itemRequestDto.getId());

        assertEquals(itemRequestDto.getDescription(), returnedRequest.getDescription());
        assertEquals(itemRequestDto.getRequester(), returnedRequest.getRequester());
    }

    @Test
    void getRequestById_shouldThrowExceptionIfRequestIdIsIncorrect() {
        assertThrows(ObjectNotFoundException.class,
                () -> requestService.getRequestById(100L, 100L));
    }

    @Test
    void getRequestById_shouldThrowExceptionIfUserIdIsIncorrect() {
        assertThrows(ObjectNotFoundException.class,
                () -> requestService.getRequestById(100L, 100L));
    }
}
