package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comments.CommentDto;
import ru.practicum.shareit.common.PageMaker;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemService itemService;

    @MockBean
    BookingService bookingService;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mvc;

    private final Long userId = 1L;
    private final Long itemId = 1L;
    private static final int FROM = 0;
    private static final int SIZE = 10;
    private static final String SEARCH_TEXT = "description";

    private final User user = new User(
            1L,
            "Galina",
            "galina@mail.ru");

    private final ItemDto itemDto = new ItemDto(
            1L,
            "Notebook",
            "Notebook description",
            true,
            new User(1L, "Anna", "anna@mail.ru"),
            null
    );

    private final ItemDtoWithBooking itemDtoWithBooking = new ItemDtoWithBooking(
            1L,
            "Notebook",
            "Notebook description",
            true,
            null,
            null,
            new ArrayList<>()
    );

    private final CommentDto comment = new CommentDto(
            1L,
            "Comment to notebook",
            user.getName(),
            LocalDateTime.now().minusDays(30));

    private final List<ItemDto> items = Arrays.asList(
            new ItemDto(
                    1L,
                    "Notebook",
                    "Notebook description",
                    true,
                    new User(1L, "Anna", "anna@mail.ru"),
                    null),
            new ItemDto(
                    2L,
                    "Book",
                    "Book description",
                    false,
                    user,
                    null),
            new ItemDto(
                    3L,
                    "Camera",
                    "Camera description",
                    true,
                    new User(3L, "Ivan", "ivan@mail.ru"),
                    null)
    );

    @SneakyThrows
    @Test
    void createItem_shouldCreateItem() {
        Mockito.when(itemService.createItem(itemDto, userId)).thenReturn(itemDto);

        mvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", String.valueOf(userId))
                                .content(objectMapper.writeValueAsString(itemDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));

        Mockito.verify(itemService, Mockito.times(1)).createItem(itemDto, userId);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @SneakyThrows
    @Test
    void updateItem_shouldUpdateItem() {
        Mockito.when(itemService.updateItem(itemDto, userId, itemId)).thenReturn(itemDto);

        mvc.perform(
                        patch("/items/{itemId}", itemId)
                                .header("X-Sharer-User-Id", String.valueOf(userId))
                                .content(objectMapper.writeValueAsString(itemDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));

        Mockito.verify(itemService, Mockito.times(1)).updateItem(itemDto, userId, itemId);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @SneakyThrows
    @Test
    void getItemById_shouldReturnItemById() {
        Mockito.when(itemService.getItemById(userId, itemId)).thenReturn(itemDtoWithBooking);

        mvc.perform(
                        get("/items/{itemId}", itemId)
                                .header("X-Sharer-User-Id", String.valueOf(userId))
                                .content(objectMapper.writeValueAsString(itemDtoWithBooking))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDtoWithBooking)));

        Mockito.verify(itemService, Mockito.times(1)).getItemById(userId, itemId);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @SneakyThrows
    @Test
    void getItemsByUser_shouldReturnListOfItems() {
        int from = 3;
        int size = 1;

        Pageable page = PageMaker.makePageableWithSort(from, size);

        Mockito.when(itemService.getItemsByUser(userId, page)).thenReturn(new ArrayList<>());

        mvc.perform(
                        get("/items")
                                .header("X-Sharer-User-Id", String.valueOf(userId))
                                .param("from", String.valueOf(from))
                                .param("size", String.valueOf(size))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new ArrayList<>())));

        Mockito.verify(itemService, Mockito.times(1)).getItemsByUser(userId, page);
        Mockito.verifyNoMoreInteractions(itemService);
    }


    @SneakyThrows
    @Test
    void searchItemByQuery_shouldReturnItemsList() {
        Pageable page = PageMaker.makePageableWithSort(FROM, SIZE);

        Mockito.when(itemService.searchItemByQuery(userId, SEARCH_TEXT, page)).thenReturn(items);

        mvc.perform(
                        get("/items/search")
                                .header("X-Sharer-User-Id", String.valueOf(userId))
                                .param("text", SEARCH_TEXT)
                                .param("from", String.valueOf(FROM))
                                .param("size", String.valueOf(SIZE))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));

        Mockito.verify(itemService, Mockito.times(1))
                .searchItemByQuery(userId, SEARCH_TEXT, page);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @SneakyThrows
    @Test
    void addComment_shouldCreateComment() {
        Mockito.when(itemService.addComment(userId, itemId, comment)).thenReturn(comment);

        mvc.perform(
                        post("/items/{itemId}/comment", itemId)
                                .header("X-Sharer-User-Id", String.valueOf(userId))
                                .content(objectMapper.writeValueAsString(comment))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(comment)));

        Mockito.verify(itemService, Mockito.times(1)).addComment(userId, itemId, comment);
        Mockito.verifyNoMoreInteractions(itemService);
    }
}