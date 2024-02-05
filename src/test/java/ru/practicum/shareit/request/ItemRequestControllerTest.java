package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemRequestService itemRequestService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mvc;

    private static final LocalDateTime DATE =
            LocalDateTime.of(2023, 12, 10, 12, 30, 0);

    @SneakyThrows
    @Test
    void createRequest_shouldCreateRequest() {
        User ivan = new User(
                1L,
                "Ivan",
                "ivan@mail.ru");

        ItemRequestDto itemRequestDto = new ItemRequestDto(
                1L,
                "Request",
                ivan,
                DATE,
                new ArrayList<>()
        );

        Mockito.when(itemRequestService.createRequest(any(), any(Long.class), any(LocalDateTime.class)))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requester.id", is(itemRequestDto.getRequester().getId()), Long.class))
                .andExpect(jsonPath("$.requester.name", is(itemRequestDto.getRequester().getName())))
                .andExpect(jsonPath("$.requester.email", is(itemRequestDto.getRequester().getEmail())))
                .andExpect(jsonPath("$.created",
                        is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @SneakyThrows
    @Test
    void getRequestById_shouldReturnListOfRequests() {
        User galina = new User(
                1L,
                "Galina",
                "galina@mail.ru");
        User anna = new User(
                2L,
                "Anna",
                "anna@mail.ru");
        User ivan = new User(
                3L,
                "Ivan",
                "ivan@mail.ru");

        Long userId = galina.getId();
        Long requestId = 1L;

        ItemRequestDto request = new ItemRequestDto(
                requestId,
                "Request",
                ivan,
                DATE,
                Arrays.asList(
                        new ItemDto(
                                1L,
                                "book",
                                "description book",
                                true,
                                galina,
                                null
                        ),
                        new ItemDto(
                                2L,
                                "camera",
                                "camera description",
                                false,
                                anna,
                                3L
                        ))
        );

        Mockito.when(itemRequestService.getRequestById(userId, requestId)).thenReturn(request);

        mvc.perform(
                        get("/requests/{requestId}", requestId)
                                .header("X-Sharer-User-Id", String.valueOf(userId))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(request)));


    }

    @SneakyThrows
    @Test
    void getAllRequests_shouldReturnListOfRequests() {
        Long userId = 1L;
        int from = 0;
        int size = 10;

        User galina = new User(
                1L,
                "Galina",
                "galina@mail.ru");

        List<ItemRequestDto> requests = Arrays.asList(
                new ItemRequestDto(1L, "Request 1", galina, DATE.plusDays(1), new ArrayList<>()),
                new ItemRequestDto(2L, "Request 2", galina, DATE.plusDays(2), new ArrayList<>()),
                new ItemRequestDto(3L, "Request 3", galina, DATE.plusDays(3), new ArrayList<>())
        );

        Mockito.when(itemRequestService.getAllRequests(userId, from, size)).thenReturn(requests);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requests)));

        Mockito.verify(itemRequestService, Mockito.times(1)).getAllRequests(userId, from, size);
        Mockito.verifyNoMoreInteractions(itemRequestService);
    }

    @SneakyThrows
    @Test
    void getRequestsByOwner_shouldReturnRequestsByOwner() {
        User galina = new User(
                1L,
                "Galina",
                "galina@mail.ru");
        User anna = new User(
                2L,
                "Anna",
                "anna@mail.ru");
        List<ItemRequestDto> requests = Arrays.asList(
                new ItemRequestDto(1L, "Request 1", galina, DATE.plusDays(1), new ArrayList<>()),
                new ItemRequestDto(2L, "Request 2", anna, DATE.plusDays(2), new ArrayList<>())
        );

        Mockito.when(itemRequestService.getRequestsByOwner(1L)).thenReturn(requests);

        mvc.perform(
                        get("/requests")
                                .header("X-Sharer-User-Id", String.valueOf(1L))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requests)));

        Mockito.verify(itemRequestService, Mockito.times(1))
                .getRequestsByOwner(1L);
        Mockito.verifyNoMoreInteractions(itemRequestService);
    }
}