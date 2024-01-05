package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class RequestControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemRequestService requestService;
    @Autowired
    private MockMvc mvc;

    private final UserDto userDto = new UserDto(
            1L,
            "Galina",
            "galina@mail.ru");

    private final ItemRequestDto requestDto = new ItemRequestDto(
            2L,
            "I'm looking for a camera",
            UserMapper.toUser(userDto),
            LocalDateTime.of(2023, 12, 29, 15, 30, 0),
            null);

    @SneakyThrows
    @Test
    void create_shouldCreateRequest() {
        when(requestService.createRequest(any(), any(Long.class), any(LocalDateTime.class)))
                .thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.requester.id", is(requestDto.getRequester().getId()), Long.class))
                .andExpect(jsonPath("$.requester.name", is(requestDto.getRequester().getName())))
                .andExpect(jsonPath("$.requester.email", is(requestDto.getRequester().getEmail())))
                .andExpect(jsonPath("$.created",
                        is(requestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @SneakyThrows
    @Test
    void getRequestById_shouldReturnListOfRequests() {
        when(requestService.getRequestById(any(Long.class), any(Long.class)))
                .thenReturn(requestDto);

        mvc.perform(get("/requests/1")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.requester.id", is(requestDto.getRequester().getId()), Long.class))
                .andExpect(jsonPath("$.requester.name", is(requestDto.getRequester().getName())))
                .andExpect(jsonPath("$.requester.email", is(requestDto.getRequester().getEmail())))
                .andExpect(jsonPath("$.created",
                        is(requestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @SneakyThrows
    @Test
    void getExistingRequests_shouldReturnListOfRequests() {
        when(requestService.getAllRequests(any(Long.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(requestDto));

        mvc.perform(get("/requests/all")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.[0].requester.id", is(requestDto.getRequester().getId()), Long.class))
                .andExpect(jsonPath("$.[0].requester.name", is(requestDto.getRequester().getName())))
                .andExpect(jsonPath("$.[0].requester.email", is(requestDto.getRequester().getEmail())))
                .andExpect(jsonPath("$.[0].created",
                        is(requestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @SneakyThrows
    @Test
    void getRequestsByOwner_shouldReturnRequestsByOwner() {
        when(requestService.getRequestsByOwner(any(Long.class)))
                .thenReturn(List.of(requestDto));

        mvc.perform(get("/requests")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.[0].requester.id", is(requestDto.getRequester().getId()), Long.class))
                .andExpect(jsonPath("$.[0].requester.name", is(requestDto.getRequester().getName())))
                .andExpect(jsonPath("$.[0].requester.email", is(requestDto.getRequester().getEmail())))
                .andExpect(jsonPath("$.[0].created",
                        is(requestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }
}
