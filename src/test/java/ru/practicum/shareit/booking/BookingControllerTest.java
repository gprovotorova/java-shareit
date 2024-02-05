package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private static final Long USER_ID = 1L;
    private static final Long BOOKING_ID = 1L;
    private static final boolean APPROVED = true;
    private static final String STATE_ALL = "ALL";
    private static final int FROM = 0;
    private static final int SIZE = 10;
    private static final LocalDateTime DATE =
            LocalDateTime.of(2023, 12, 10, 10, 50, 0);

    private final ItemDto itemDto = new ItemDto(
            1L,
            "Book",
            "Description",
            true,
            new User(1L, "Anna", "anna@mail.ru"),
            null);

    private final BookingDto bookingDto = new BookingDto(
            1L,
            LocalDateTime.of(2023, 12, 10, 10, 50, 0),
            LocalDateTime.of(2023, 12, 29, 12, 10, 0),
            new BookingDto.Item(itemDto.getId(), itemDto.getName()),
            1L,
            new BookingDto.User(1L, "Galina"),
            BookingStatus.WAITING);

    private final List<BookingDto> bookings = Arrays.asList(
            new BookingDto(
                    1L,
                    DATE,
                    DATE.plusDays(2),
                    new BookingDto.Item(1L, "Book"),
                    1L,
                    new BookingDto.User(1L, "Galina"),
                    BookingStatus.WAITING
            ),
            new BookingDto(
                    2L,
                    DATE.plusDays(2),
                    DATE.plusDays(5),
                    new BookingDto.Item(2L, "Camera"),
                    2L,
                    new BookingDto.User(2L, "Anna"),
                    BookingStatus.REJECTED
            ),
            new BookingDto(
                    3L,
                    DATE.plusDays(5),
                    DATE.plusDays(7),
                    new BookingDto.Item(3L, "Table"),
                    3L,
                    new BookingDto.User(3L, "Ivan"),
                    BookingStatus.APPROVED
            )
    );

    @SneakyThrows
    @Test
    void createBooking_shouldCreateBooking() {
        Mockito.when(bookingService.createBooking(bookingDto, USER_ID)).thenReturn(bookingDto);

        mvc.perform(
                        post("/bookings")
                                .header("X-Sharer-User-Id", String.valueOf(USER_ID))
                                .content(objectMapper.writeValueAsString(bookingDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDto)));

        Mockito.verify(bookingService, Mockito.times(1)).createBooking(bookingDto, USER_ID);
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @SneakyThrows
    @Test
    void updateBooking_shouldUpdateBooking() {
        Mockito.when(bookingService.updateBooking(BOOKING_ID, USER_ID, APPROVED)).thenReturn(bookingDto);

        mvc.perform(
                        patch("/bookings/{bookingId}", BOOKING_ID)
                                .header("X-Sharer-User-Id", String.valueOf(USER_ID))
                                .param("approved", String.valueOf(APPROVED))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDto)));

        Mockito.verify(bookingService, Mockito.times(1))
                .updateBooking(BOOKING_ID, USER_ID, APPROVED);
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @SneakyThrows
    @Test
    void getBookingById_shouldReturnBookingById() {
        Mockito.when(bookingService.getBookingById(USER_ID, BOOKING_ID)).thenReturn(bookingDto);

        mvc.perform(
                        get("/bookings/{bookingId}", BOOKING_ID)
                                .header("X-Sharer-User-Id", String.valueOf(USER_ID))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDto)));

        Mockito.verify(bookingService, Mockito.times(1)).getBookingById(USER_ID, BOOKING_ID);
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @SneakyThrows
    @Test
    void getAllBookingByUserId_shouldReturnListOfBookingsByUserId() {
        Mockito.when(bookingService.getAllBookingByUserId(USER_ID, STATE_ALL, FROM, SIZE)).thenReturn(bookings);

        mvc.perform(
                        get("/bookings")
                                .header("X-Sharer-User-Id", String.valueOf(USER_ID))
                                .param("state", STATE_ALL)
                                .param("from", String.valueOf(FROM))
                                .param("size", String.valueOf(SIZE))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)));

        Mockito.verify(bookingService, Mockito.times(1))
                .getAllBookingByUserId(USER_ID, STATE_ALL, FROM, SIZE);
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @SneakyThrows
    @Test
    void getAllBookingByOwnerId_shouldReturnListOfBookingsByOwnerId() {
        Mockito.when(bookingService.getAllBookingByOwnerId(USER_ID, STATE_ALL, FROM, SIZE)).thenReturn(bookings);

        mvc.perform(
                        get("/bookings/owner")
                                .header("X-Sharer-User-Id", String.valueOf(USER_ID))
                                .param("state", STATE_ALL)
                                .param("from", String.valueOf(FROM))
                                .param("size", String.valueOf(SIZE))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)));

        Mockito.verify(bookingService, Mockito.times(1))
                .getAllBookingByOwnerId(USER_ID, STATE_ALL, FROM, SIZE);
        Mockito.verifyNoMoreInteractions(bookingService);
    }
}