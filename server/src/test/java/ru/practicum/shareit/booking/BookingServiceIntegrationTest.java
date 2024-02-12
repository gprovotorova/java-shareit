package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@Rollback
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class BookingServiceIntegrationTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingServiceImpl bookingService;

    private static final LocalDateTime DATE =
            LocalDateTime.of(2023, 12, 10, 12, 30, 0);

    @Transactional
    @Test
    void getBookingById_shouldReturnBooking() {
        User galina = new User(1L,
                "Galina",
                "galina@mail.ru");
        galina = userRepository.save(galina);
        User anna = new User(2L,
                "Anna",
                "anna@mail.ru");
        anna = userRepository.save(anna);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Book");
        itemDto.setDescription("very interesting romantic book description");
        itemDto.setAvailable(true);
        Item book = ItemMapper.toItem(itemDto, galina);
        book.setRequestId(null);
        book = itemRepository.save(book);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(DATE);
        booking.setEnd(DATE.plusDays(2));
        booking.setItem(book);
        booking.setBooker(anna);
        booking.setStatus(BookingStatus.APPROVED);

        booking = bookingRepository.save(booking);

        BookingDto foundBooking = bookingService.getBookingById(anna.getId(), booking.getId());

        assertNotNull(foundBooking, "The found reservation must not be null.");

        assertEquals(booking.getId(),
                foundBooking.getId(), "The booking ID must match the expected value.");
        assertEquals(booking.getStatus(),
                foundBooking.getStatus(), "The booking status must match the expected value.");

        bookingRepository.delete(booking);
    }
}
