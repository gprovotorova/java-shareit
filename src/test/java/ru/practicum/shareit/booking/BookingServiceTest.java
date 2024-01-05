package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ObjectValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    private BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(userRepository, itemRepository, bookingRepository);
    }

    private final User galina = new User(
            1L,
            "Galina",
            "galina@mail.ru");

    private final User anna = new User(
            2L,
            "Anna",
            "anna@mail.ru");

    private final User ivan = new User(
            3L,
            "Ivan",
            "ivan@mail.ru");

    private final Item book = new Item(
            1L,
            "book",
            "very interesting romantic book description",
            true,
            galina,
            null,
            new HashSet<>());

    private final Booking booking = new Booking(
            1L,
            LocalDateTime.of(2023, 12, 10, 12, 30, 0),
            LocalDateTime.of(2023, 12, 28, 14, 10, 0),
            book, anna, BookingStatus.WAITING);


    @Test
    void createBooking_shouldThrowExceptionIfUserIdIsIncorrect() {
        when(userRepository.findById(anyLong()))
                .thenThrow(ObjectNotFoundException.class);

        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.createBooking(
                        new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(),
                                new BookingDto.Item(book.getId(), book.getName()),
                                booking.getItem().getId(),
                                new BookingDto.User(anna.getId(), anna.getName()),
                                BookingStatus.WAITING), 100L));
    }

    @Test
    void createBooking_shouldThrowExceptionIfItemIdIsIncorrect() {
        BookingDto bookingDto = new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(),
                new BookingDto.Item(book.getId(), book.getName()),
                100L,
                new BookingDto.User(anna.getId(), anna.getName()),
                BookingStatus.WAITING);

        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.createBooking(bookingDto, anna.getId()));
    }

    @Test
    void createBooking_shouldThrowExceptionIfAvailableIsFalse() {
        book.setAvailable(false);
        BookingDto bookingDto = new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(),
                new BookingDto.Item(book.getId(), book.getName()),
                booking.getItem().getId(),
                new BookingDto.User(anna.getId(), anna.getName()),
                BookingStatus.WAITING);

        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.createBooking(bookingDto, anna.getId()));
    }

    @Test
    void createBooking_shouldThrowExceptionIfOwnerIsBooking() {
        BookingDto bookingDto = new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(),
                new BookingDto.Item(book.getId(), book.getName()),
                booking.getItem().getId(),
                new BookingDto.User(galina.getId(), galina.getName()),
                BookingStatus.WAITING);

        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.createBooking(bookingDto, galina.getId()));
    }

    @Test
    void createBooking_shouldThrowExceptionIfEndIsBeforeStart() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(anna));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(book));

        assertThrows(ObjectValidationException.class,
                () -> bookingService.createBooking(
                        new BookingDto(booking.getId(),
                                LocalDateTime.of(2022, 12, 10, 12, 30, 0),
                                LocalDateTime.of(2023, 12, 28, 14, 10, 0),
                                new BookingDto.Item(book.getId(), book.getName()),
                                booking.getItem().getId(),
                                new BookingDto.User(anna.getId(), galina.getName()),
                                BookingStatus.WAITING), anna.getId()));
    }

    @Test
    void updateBooking_shouldThrowExceptionIfBookingIdIsIncorrect() {
        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.updateBooking(galina.getId(), 999L, true));
    }

    @Test
    void updateBooking_shouldThrowExceptionIfNotOwnerUpdatingBookingStatus() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.updateBooking(anna.getId(), 1L, true));
    }

    @Test
    void getBookingById_shouldReturnBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDto returnedBooking = bookingService.getBookingById(booking.getId(), anna.getId());

        assertThat(returnedBooking.getStart(), equalTo(booking.getStart()));
        assertThat(returnedBooking.getEnd(), equalTo(booking.getEnd()));
    }

    @Test
    void getBookingById_shouldThrowExceptionIfBookingIdIsIncorrect() {
        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getBookingById(999L, 999L));
    }

    @Test
    void getBookingById_shouldThrowExceptionIfNeitherBookerOrOwnerIsGettingBooking() {
        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getBookingById(1L, ivan.getId()));
    }


    @Test
    void getBookingsByOwner_shouldReturnBookingsIfBookingStateIsCurrent() {
        Page<Booking> pages = new PageImpl<>(List.of(booking));
        when(bookingRepository.findCurrentBookingsByBookerId(any(), any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(galina));

        List<BookingDto> bookings = bookingService.getAllBookingByOwnerId(galina.getId(),
                String.valueOf(BookingState.CURRENT), 0, 10);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByOwner_shouldReturnBookingsIfBookingStateIsPast() {
        booking.setStatus(BookingStatus.APPROVED);
        Page<Booking> pages = new PageImpl<>(List.of(booking));
        when(bookingRepository.findByBookerIdAndEndInPast(any(), any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(galina));

        List<BookingDto> bookings = bookingService.getAllBookingByOwnerId(galina.getId(),
                String.valueOf(BookingState.PAST), 0, 10);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByOwner_shouldReturnBookingsIfBookingStateIsFuture() {
        //Booking thisBooking = new Booking(1L,
        //        LocalDateTime.of(2023, 12, 10, 12, 30, 0),
        //        LocalDateTime.of(2023, 12, 28, 14, 10, 0),
        //        book, anna, BookingStatus.APPROVED);
        booking.setStatus(BookingStatus.APPROVED);
        Page<Booking> pages = new PageImpl<>(List.of(booking));
        when(bookingRepository.findByBookerIdAndStartInFuture(any(), any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(galina));

        List<BookingDto> bookings = bookingService.getAllBookingByOwnerId(galina.getId(),
                String.valueOf(BookingState.FUTURE), 0, 10);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByOwner_shouldReturnBookingsIfBookingStateIsWaiting() {
        Page<Booking> pages = new PageImpl<>(List.of(booking));
        when(bookingRepository.findByBookerIdAndStatusContaining(any(), any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(galina));

        List<BookingDto> bookings = bookingService.getAllBookingByOwnerId(galina.getId(),
                String.valueOf(BookingState.WAITING), 0, 10);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByOwner_shouldReturnBookingsIfBookingStateIsRejected() {
        booking.setStatus(BookingStatus.REJECTED);
        Page<Booking> pages = new PageImpl<>(List.of(booking));
        when(bookingRepository.findByBookerIdAndStatusContaining(any(), any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(galina));

        List<BookingDto> bookings = bookingService.getAllBookingByOwnerId(galina.getId(),
                String.valueOf(BookingState.REJECTED), 0, 10);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getAllBookingByUserId_shouldThrowExceptionIfUseridIsIncorrect() {
        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getAllBookingByUserId(999L, String.valueOf(BookingState.ALL),
                        0, 10));
    }

    @Test
    void getBookingsByUser_shouldReturnBookingIfSizeIsNull() {
        Page<Booking> pages = new PageImpl<>(List.of(booking));
        when(bookingRepository.getAllBookingsForOwnersItems(any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(anna));

        List<BookingDto> bookings = bookingService.getAllBookingByUserId(anna.getId(),
                String.valueOf(BookingState.ALL), 0, null);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByUser_shouldReturnBookingsIfBookingStateIsAll() {
        Page<Booking> pages = new PageImpl<>(List.of(booking));
        when(bookingRepository.getAllBookingsForOwnersItems(any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(anna));

        List<BookingDto> bookings = bookingService.getAllBookingByUserId(anna.getId(),
                String.valueOf(BookingState.ALL), 0, 10);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByUser_shouldReturnBookingsIfBookingStateIsCurrent() {
        booking.setStatus(BookingStatus.APPROVED);
        Page<Booking> pages = new PageImpl<>(List.of(booking));
        when(bookingRepository.findCurrentBookingsByBookerId(any(), any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(anna));

        List<BookingDto> bookings = bookingService.getAllBookingByUserId(anna.getId(),
                String.valueOf(BookingState.CURRENT), 0, 10);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByUser_shouldReturnBookingsIfBookingStateIsPast() {
        booking.setStatus(BookingStatus.APPROVED);
        Page<Booking> pages = new PageImpl<>(List.of(booking));
        when(bookingRepository.findByBookerIdAndEndInPast(any(), any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(anna));

        List<BookingDto> bookings = bookingService.getAllBookingByUserId(anna.getId(),
                String.valueOf(BookingState.PAST), 0, 10);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByUser_shouldReturnBookingsIfBookingStateIsFuture() {
        booking.setStatus(BookingStatus.APPROVED);
        Page<Booking> pages = new PageImpl<>(List.of(booking));
        when(bookingRepository.findByBookerIdAndStartInFuture(any(), any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(anna));

        List<BookingDto> bookings = bookingService.getAllBookingByUserId(anna.getId(),
                String.valueOf(BookingState.FUTURE), 0, 10);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByUser_shouldReturnBookingsIfBookingStateIsWaiting() {
        Page<Booking> pages = new PageImpl<>(List.of(booking));
        when(bookingRepository.findByBookerIdAndStatusContaining(any(), any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(anna));

        List<BookingDto> bookings = bookingService.getAllBookingByUserId(anna.getId(),
                String.valueOf(BookingState.WAITING), 0, 10);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByUser_shouldReturnBookingsIfBookingStateIsRejected() {
        booking.setStatus(BookingStatus.REJECTED);
        Page<Booking> pages = new PageImpl<>(List.of(booking));
        when(bookingRepository.findByBookerIdAndStatusContaining(any(), any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(anna));

        List<BookingDto> bookings = bookingService.getAllBookingByUserId(anna.getId(),
                String.valueOf(BookingState.REJECTED), 0, 10);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByUser_shouldReturnBookingsIfBookingStateIsUnknownState() {
        Page<Booking> pages = new PageImpl<>(List.of(booking));
        when(bookingRepository.findByBookerIdAndStatusContaining(any(), any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(anna));

        List<BookingDto> bookings = bookingService.getAllBookingByUserId(anna.getId(),
                "UNKNOWN STATE", 0, 10);

        assertFalse(bookings.isEmpty());
    }
}
