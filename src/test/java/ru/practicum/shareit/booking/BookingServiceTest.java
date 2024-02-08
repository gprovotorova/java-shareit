package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.common.PageMaker;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ObjectValidationException;
import ru.practicum.shareit.exception.StatusBookingException;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private static final String STATE_ALL = "ALL";
    private static final String STATE_CURRENT = "CURRENT";
    private static final String STATE_PAST = "PAST";
    private static final String STATE_FUTURE = "FUTURE";
    private static final String STATE_WAITING = "WAITING";
    private static final String STATE_REJECTED = "REJECTED";
    private static final String UNKNOWN_STATE = "UNKNOWN STATE";
    private static final int FROM = 0;
    private static final int SIZE = 10;
    private static final LocalDateTime DATE = LocalDateTime.now();

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
            DATE.plusDays(1),
            DATE.plusDays(2),
            book,
            anna,
            BookingStatus.APPROVED);

    @Transactional
    @Test
    void createBooking_shouldCreateBooking() {
        BookingDto bookingDto = new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(),
                new BookingDto.Item(this.book.getId(), this.book.getName()),
                booking.getItem().getId(),
                new BookingDto.User(this.anna.getId(), this.anna.getName()),
                BookingStatus.WAITING);

        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.of(book));
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(anna));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto createdBooking = bookingService.createBooking(bookingDto, anna.getId());

        assertNotNull(createdBooking, "Booking should not be null");
        assertEquals(BookingStatus.APPROVED, createdBooking.getStatus(), "Booking status should be APPROVED");

        verify(itemRepository, times(1)).findById(any(Long.class));
        verify(userRepository, times(1)).findById(anna.getId());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Transactional
    @Test
    void createBooking_shouldThrowExceptionIfUserIdIsIncorrect() {
        when(userRepository.findById(anyLong())).thenThrow(ObjectNotFoundException.class);

        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.createBooking(
                        new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(),
                                new BookingDto.Item(book.getId(), book.getName()),
                                booking.getItem().getId(),
                                new BookingDto.User(anna.getId(), anna.getName()),
                                BookingStatus.WAITING), 100L));
    }

    @Transactional
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

    @Transactional
    @Test
    void createBooking_shouldThrowExceptionIfAvailableIsFalse() {
        book.setAvailable(false);
        BookingDto bookingDto = new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(),
                new BookingDto.Item(book.getId(), book.getName()),
                booking.getItem().getId(),
                new BookingDto.User(anna.getId(), anna.getName()),
                BookingStatus.APPROVED);

        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.createBooking(bookingDto, anna.getId()));
    }

    @Transactional
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

    @Transactional
    @Test
    void createBooking_shouldThrowExceptionIfEndIsBeforeStart() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(anna));
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.of(book));

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

    @Transactional
    @Test
    void updateBooking_shouldThrowExceptionIfBookingIdIsIncorrect() {
        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.updateBooking(galina.getId(), 999L, true));
    }

    @Transactional
    @Test
    void updateBooking_shouldThrowExceptionIfBookingStatusIsApproved() {
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(any(Long.class))).thenReturn(Optional.of(booking));
        assertThrows(ObjectValidationException.class,
                () -> bookingService.updateBooking(booking.getId(), galina.getId(), true));
    }

    @Transactional
    @Test
    void updateBooking_shouldThrowExceptionIfBookingStatusIsRejected() {
        booking.setStatus(BookingStatus.REJECTED);

        when(bookingRepository.findById(any(Long.class))).thenReturn(Optional.of(booking));
        assertThrows(ObjectValidationException.class,
                () -> bookingService.updateBooking(booking.getId(), galina.getId(), false));
    }

    @Transactional
    @Test
    void updateBooking_shouldThrowExceptionIfNotOwnerUpdatingBookingStatus() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.updateBooking(booking.getId(), anna.getId(), true));
    }

    @Transactional
    @Test
    void updateBooking_shouldUpdateBookingStatusToApproved() {
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto bookingDto = bookingService.updateBooking(booking.getId(), galina.getId(), true);

        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());

        verify(bookingRepository, times(1)).findById(any(Long.class));
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Transactional
    @Test
    void updateBooking_shouldUpdateBookingStatusToRejected() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto bookingDto = bookingService.updateBooking(booking.getId(), galina.getId(), false);

        assertEquals(BookingStatus.REJECTED, bookingDto.getStatus());

        verify(bookingRepository, times(1)).findById(any(Long.class));
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Transactional
    @Test
    void getBookingById_shouldReturnBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDto returnedBooking = bookingService.getBookingById(booking.getId(), anna.getId());

        assertThat(returnedBooking.getStart(), equalTo(booking.getStart()));
        assertThat(returnedBooking.getEnd(), equalTo(booking.getEnd()));
    }

    @Transactional
    @Test
    void getBookingById_shouldThrowExceptionIfBookingIdIsIncorrect() {
        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getBookingById(999L, 999L));
    }

    @Transactional
    @Test
    void getBookingById_shouldThrowExceptionIfNeitherBookerOrOwnerIsGettingBooking() {
        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getBookingById(1L, ivan.getId()));
    }

    @Transactional
    @Test
    void getAllBookingByOwnerId_shouldThrowExceptionIfUseridIsIncorrect() {
        Pageable page = PageMaker.makePageableWithSort(FROM, SIZE);

        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getAllBookingByOwnerId(999L, STATE_ALL, page));
    }

    @Transactional
    @Test
    void getAllBookingByOwnerId_shouldReturnBookingIfSizeIsNullAndStateIsAll() {
        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(bookingRepository.getAllByItemOwnerIdOrderByStartDesc(any(Long.class))).thenReturn(List.of(booking));
        when(bookingRepository.getAllBookingsForOwnersItems(any(Long.class))).thenReturn(List.of(booking));

        Pageable page = PageMaker.makePageableWithSort(FROM, null);

        List<BookingDto> bookings = bookingService.getAllBookingByOwnerId(galina.getId(), STATE_ALL, page);

        assertNotNull(bookings, "List of bookings should not be null");
        assertFalse(bookings.isEmpty(), "List of bookings should not be empty");

        verify(userRepository, times(1)).existsById(galina.getId());
    }

    @Transactional
    @Test
    void getAllBookingByOwnerId_shouldReturnBookingIfSizeIsNullAndStateIsCurrent() {
        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(bookingRepository.getAllByItemOwnerIdOrderByStartDesc(any(Long.class))).thenReturn(List.of(booking));

        Pageable page = PageMaker.makePageableWithSort(FROM, null);

        List<BookingDto> bookings =
                bookingService.getAllBookingByOwnerId(galina.getId(), STATE_CURRENT, page);

        assertNotNull(bookings, "List of bookings should not be null");
        assertTrue(bookings.isEmpty(), "List of bookings should be empty");

        verify(userRepository, times(1)).existsById(galina.getId());
    }

    @Transactional
    @Test
    void getAllBookingByOwnerId_shouldReturnBookingIfSizeIsNullAndStateIsPast() {
        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(bookingRepository.getAllByItemOwnerIdOrderByStartDesc(any(Long.class))).thenReturn(List.of(booking));

        Pageable page = PageMaker.makePageableWithSort(FROM, null);

        List<BookingDto> bookings =
                bookingService.getAllBookingByOwnerId(galina.getId(), STATE_PAST, page);

        assertNotNull(bookings, "List of bookings should not be null");
        assertTrue(bookings.isEmpty(), "List of bookings should be empty");

        verify(userRepository, times(1)).existsById(galina.getId());
    }

    @Transactional
    @Test
    void getAllBookingByOwnerId_shouldReturnBookingIfSizeIsNullAndStateIsFuture() {
        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(bookingRepository.getAllByItemOwnerIdOrderByStartDesc(any(Long.class))).thenReturn(List.of(booking));

        Pageable page = PageMaker.makePageableWithSort(FROM, null);

        List<BookingDto> bookings =
                bookingService.getAllBookingByOwnerId(galina.getId(), STATE_FUTURE, page);

        assertNotNull(bookings, "List of bookings should not be null");
        assertTrue(bookings.isEmpty(), "List of bookings should be empty");

        verify(userRepository, times(1)).existsById(galina.getId());
    }

    @Transactional
    @Test
    void getAllBookingByOwnerId_shouldReturnBookingIfSizeIsNullAndStateIsWaiting() {
        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(bookingRepository.getAllByItemOwnerIdOrderByStartDesc(any(Long.class))).thenReturn(List.of(booking));

        Pageable page = PageMaker.makePageableWithSort(FROM, null);

        List<BookingDto> bookings =
                bookingService.getAllBookingByOwnerId(galina.getId(), STATE_WAITING, page);

        assertNotNull(bookings, "List of bookings should not be null");
        assertTrue(bookings.isEmpty(), "List of bookings should be empty");

        verify(userRepository, times(1)).existsById(galina.getId());
    }

    @Transactional
    @Test
    void getAllBookingByOwnerId_shouldReturnBookingIfSizeIsNullAndStateIsRejected() {
        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(bookingRepository.getAllByItemOwnerIdOrderByStartDesc(any(Long.class))).thenReturn(List.of(booking));

        Pageable page = PageMaker.makePageableWithSort(FROM, null);

        List<BookingDto> bookings =
                bookingService.getAllBookingByOwnerId(galina.getId(), STATE_REJECTED, page);

        assertNotNull(bookings, "List of bookings should not be null");
        assertTrue(bookings.isEmpty(), "List of bookings should be empty");

        verify(userRepository, times(1)).existsById(galina.getId());
    }

    @Transactional
    @Test
    void getAllBookingByOwnerId_shouldReturnBookingIfSizeIsNullAndUnknownState() {
        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(bookingRepository.getAllByItemOwnerIdOrderByStartDesc(any(Long.class))).thenReturn(List.of(booking));

        Pageable page = PageMaker.makePageableWithSort(FROM, null);

        assertThrows(StatusBookingException.class,
                () -> bookingService.getAllBookingByOwnerId(galina.getId(), UNKNOWN_STATE, page));

        verify(userRepository, times(1)).existsById(galina.getId());
    }


    @Transactional
    @Test
    void getBookingsByOwnerId_shouldReturnBookingsIfBookingStateIsAll() {
        Page<Booking> pages = new PageImpl<>(List.of(booking));

        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(bookingRepository.getAllByItemOwnerIdOrderByStartDesc(any(Long.class))).thenReturn(List.of(booking));
        when(bookingRepository.getAllBookingsForOwnersItems(any(Long.class), any(Pageable.class))).thenReturn(pages);

        Pageable page = PageMaker.makePageableWithSort(FROM, SIZE);

        List<BookingDto> bookings = bookingService.getAllBookingByOwnerId(galina.getId(), STATE_ALL, page);

        assertNotNull(bookings, "List of bookings should not be null");
        assertFalse(bookings.isEmpty(), "List of bookings should not be empty");
        assertEquals(1, bookings.size());

        verify(userRepository, times(1)).existsById(galina.getId());
    }

    @Transactional
    @Test
    void getBookingsByOwnerId_shouldReturnBookingsIfStateIsCurrent() {
        Page<Booking> pages = new PageImpl<>(List.of(booking));

        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(bookingRepository.getAllByItemOwnerIdOrderByStartDesc(any(Long.class))).thenReturn(List.of(booking));
        when(bookingRepository.getCurrentBookingsForOwnersItems(any(Long.class), any(LocalDateTime.class),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(pages);

        Pageable page = PageMaker.makePageableWithSort(FROM, SIZE);

        List<BookingDto> bookings = bookingService.getAllBookingByOwnerId(galina.getId(), STATE_CURRENT, page);

        assertNotNull(bookings, "List of bookings should not be null");
        assertFalse(bookings.isEmpty(), "List of bookings should not be empty");
        assertEquals(1, bookings.size());

        verify(userRepository, times(1)).existsById(galina.getId());
    }

    @Transactional
    @Test
    void getBookingsByOwnerId_shouldReturnBookingsIfBookingStateIsPast() {
        Page<Booking> pages = new PageImpl<>(List.of(booking));

        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(bookingRepository.getAllByItemOwnerIdOrderByStartDesc(any(Long.class))).thenReturn(List.of(booking));
        when(bookingRepository.getPastBookingsForOwnersItems(any(Long.class), any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(pages);

        Pageable page = PageMaker.makePageableWithSort(FROM, SIZE);

        List<BookingDto> bookings = bookingService.getAllBookingByOwnerId(galina.getId(), STATE_PAST, page);

        assertNotNull(bookings, "List of bookings should not be null");
        assertFalse(bookings.isEmpty(), "List of bookings should not be empty");
        assertEquals(1, bookings.size());

        verify(userRepository, times(1)).existsById(galina.getId());
    }

    @Transactional
    @Test
    void getBookingsByOwnerId_shouldReturnBookingsIfBookingStateIsFuture() {
        Page<Booking> pages = new PageImpl<>(List.of(booking));

        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(bookingRepository.getAllByItemOwnerIdOrderByStartDesc(any(Long.class))).thenReturn(List.of(booking));
        when(bookingRepository.getFutureBookingsForOwnersItems(any(Long.class), any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(pages);

        Pageable page = PageMaker.makePageableWithSort(FROM, SIZE);

        List<BookingDto> bookings = bookingService.getAllBookingByOwnerId(galina.getId(), STATE_FUTURE, page);

        assertNotNull(bookings, "List of bookings should not be null");
        assertFalse(bookings.isEmpty(), "List of bookings should not be empty");
        assertEquals(1, bookings.size());

        verify(userRepository, times(1)).existsById(galina.getId());
    }

    @Transactional
    @Test
    void getBookingsByOwnerId_shouldReturnBookingsIfBookingStateIsWaiting() {
        booking.setStatus(BookingStatus.WAITING);

        Page<Booking> pages = new PageImpl<>(List.of(booking));

        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(bookingRepository.getAllByItemOwnerIdOrderByStartDesc(any(Long.class))).thenReturn(List.of(booking));
        when(bookingRepository.getBookingsForOwnersWithStatusContaining(any(Long.class), any(BookingStatus.class),
                any(Pageable.class))).thenReturn(pages);

        Pageable page = PageMaker.makePageableWithSort(FROM, SIZE);

        List<BookingDto> bookings = bookingService.getAllBookingByOwnerId(galina.getId(), STATE_WAITING, page);

        assertNotNull(bookings, "List of bookings should not be null");
        assertFalse(bookings.isEmpty(), "List of bookings should not be empty");
        assertEquals(1, bookings.size());

        verify(userRepository, times(1)).existsById(galina.getId());
    }

    @Transactional
    @Test
    void getBookingsByOwnerId_shouldReturnBookingsIfBookingStateIsRejected() {
        booking.setStatus(BookingStatus.REJECTED);

        Page<Booking> pages = new PageImpl<>(List.of(booking));

        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(bookingRepository.getAllByItemOwnerIdOrderByStartDesc(any(Long.class))).thenReturn(List.of(booking));
        when(bookingRepository.getBookingsForOwnersWithStatusContaining(any(Long.class), any(BookingStatus.class),
                any(Pageable.class))).thenReturn(pages);

        Pageable page = PageMaker.makePageableWithSort(FROM, SIZE);

        List<BookingDto> bookings = bookingService.getAllBookingByOwnerId(galina.getId(), STATE_REJECTED, page);

        assertNotNull(bookings, "List of bookings should not be null");
        assertFalse(bookings.isEmpty(), "List of bookings should not be empty");
        assertEquals(1, bookings.size());

        verify(userRepository, times(1)).existsById(galina.getId());
    }

    @Transactional
    @Test
    void getAllBookingByOwnerId_shouldThrowExceptionBookingStateIsUnknownState() {
        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(bookingRepository.getAllByItemOwnerIdOrderByStartDesc(any(Long.class))).thenReturn(List.of(booking));

        Pageable page = PageMaker.makePageableWithSort(FROM, SIZE);

        assertThrows(StatusBookingException.class,
                () -> bookingService.getAllBookingByOwnerId(galina.getId(), UNKNOWN_STATE, page));

        verify(userRepository, times(1)).existsById(galina.getId());
    }

    @Transactional
    @Test
    void getAllBookingByUserId_shouldThrowExceptionIfUseridIsIncorrect() {
        Pageable page = PageMaker.makePageableWithSort(FROM, SIZE);

        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getAllBookingByUserId(999L, STATE_ALL, page));
    }

    @Transactional
    @Test
    void getAllBookingByUserId_shouldReturnBookingIfSizeIsNullAndStateIsAll() {
        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(bookingRepository.getAllByBookerIdOrderByStartDesc(any(Long.class))).thenReturn(List.of(booking));
        when(bookingRepository.findByBookerIdOrderByStartDesc(any(Long.class))).thenReturn(List.of(booking));

        Pageable page = PageMaker.makePageableWithSort(FROM, null);

        List<BookingDto> bookings = bookingService.getAllBookingByUserId(anna.getId(), STATE_ALL, page);

        assertNotNull(bookings, "List of bookings should not be null");
        assertFalse(bookings.isEmpty(), "List of bookings should not be empty");
        assertEquals(1, bookings.size());

        verify(userRepository, times(1)).existsById(anna.getId());
    }

    @Transactional
    @Test
    void getAllBookingByUserId_shouldReturnBookingIfSizeIsNullAndStateIsCurrent() {
        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(bookingRepository.getAllByBookerIdOrderByStartDesc(any(Long.class))).thenReturn(List.of(booking));

        Pageable page = PageMaker.makePageableWithSort(FROM, null);

        List<BookingDto> bookings = bookingService.getAllBookingByUserId(anna.getId(), STATE_CURRENT, page);

        assertNotNull(bookings, "List of bookings should not be null");
        assertTrue(bookings.isEmpty(), "List of bookings should be empty");

        verify(userRepository, times(1)).existsById(anna.getId());
    }

    @Transactional
    @Test
    void getAllBookingByUserId_shouldReturnBookingIfSizeIsNullAndStateIsPast() {
        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(bookingRepository.getAllByBookerIdOrderByStartDesc(any(Long.class))).thenReturn(List.of(booking));

        Pageable page = PageMaker.makePageableWithSort(FROM, null);

        List<BookingDto> bookings = bookingService.getAllBookingByUserId(anna.getId(), STATE_PAST, page);

        assertNotNull(bookings, "List of bookings should not be null");
        assertTrue(bookings.isEmpty(), "List of bookings should be empty");

        verify(userRepository, times(1)).existsById(anna.getId());
    }

    @Transactional
    @Test
    void getAllBookingByUserId_shouldReturnBookingIfSizeIsNullAndStateIsFuture() {
        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(bookingRepository.getAllByBookerIdOrderByStartDesc(any(Long.class))).thenReturn(List.of(booking));

        Pageable page = PageMaker.makePageableWithSort(FROM, null);

        List<BookingDto> bookings = bookingService.getAllBookingByUserId(anna.getId(), STATE_FUTURE, page);

        assertNotNull(bookings, "List of bookings should not be null");
        assertTrue(bookings.isEmpty(), "List of bookings should be empty");

        verify(userRepository, times(1)).existsById(anna.getId());
    }

    @Transactional
    @Test
    void getAllBookingByUserId_shouldReturnBookingIfSizeIsNullAndStateIsWaiting() {
        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(bookingRepository.getAllByBookerIdOrderByStartDesc(any(Long.class))).thenReturn(List.of(booking));

        Pageable page = PageMaker.makePageableWithSort(FROM, null);

        List<BookingDto> bookings = bookingService.getAllBookingByUserId(anna.getId(), STATE_WAITING, page);

        assertNotNull(bookings, "List of bookings should not be null");
        assertTrue(bookings.isEmpty(), "List of bookings should be empty");

        verify(userRepository, times(1)).existsById(anna.getId());
    }

    @Transactional
    @Test
    void getAllBookingByUserId_shouldReturnBookingIfSizeIsNullAndStateIsRejected() {
        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(bookingRepository.getAllByBookerIdOrderByStartDesc(any(Long.class))).thenReturn(List.of(booking));

        Pageable page = PageMaker.makePageableWithSort(FROM, null);

        List<BookingDto> bookings = bookingService.getAllBookingByUserId(anna.getId(), STATE_REJECTED, page);

        assertNotNull(bookings, "List of bookings should not be null");
        assertTrue(bookings.isEmpty(), "List of bookings should be empty");

        verify(userRepository, times(1)).existsById(anna.getId());
    }

    @Transactional
    @Test
    void getAllBookingByUserId_shouldReturnBookingIfSizeIsNullAndUnknownState() {
        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(bookingRepository.getAllByBookerIdOrderByStartDesc(any(Long.class))).thenReturn(List.of(booking));

        Pageable page = PageMaker.makePageableWithSort(FROM, null);

        assertThrows(StatusBookingException.class,
                () -> bookingService.getAllBookingByUserId(anna.getId(), UNKNOWN_STATE, page));

        verify(userRepository, times(1)).existsById(anna.getId());
    }


    @Transactional
    @Test
    void getAllBookingByUserId_shouldReturnBookingsIfBookingStateIsAll() {
        Page<Booking> pages = new PageImpl<>(List.of(booking));

        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(bookingRepository.getAllByBookerIdOrderByStartDesc(any(Long.class))).thenReturn(List.of(booking));
        when(bookingRepository.findByBookerIdOrderByStartDesc(any(Long.class), any(Pageable.class))).thenReturn(pages);

        Pageable page = PageMaker.makePageableWithSort(FROM, SIZE);

        List<BookingDto> bookings = bookingService.getAllBookingByUserId(anna.getId(), STATE_ALL, page);

        assertNotNull(bookings, "List of bookings should not be null");
        assertFalse(bookings.isEmpty(), "List of bookings should not be empty");
        assertEquals(1, bookings.size());

        verify(userRepository, times(1)).existsById(anna.getId());
    }

    @Transactional
    @Test
    void getAllBookingByUserId_shouldReturnBookingsIfBookingStateIsCurrent() {
        Page<Booking> pages = new PageImpl<>(List.of(booking));

        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(bookingRepository.getAllByBookerIdOrderByStartDesc(any(Long.class))).thenReturn(List.of(booking));
        when(bookingRepository.findCurrentBookingsByBookerId(any(Long.class), any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(pages);

        Pageable page = PageMaker.makePageableWithSort(FROM, SIZE);

        List<BookingDto> bookings = bookingService.getAllBookingByUserId(anna.getId(), STATE_CURRENT, page);

        assertNotNull(bookings, "List of bookings should not be null");
        assertFalse(bookings.isEmpty(), "List of bookings should not be empty");
        assertEquals(1, bookings.size());

        verify(userRepository, times(1)).existsById(anna.getId());
    }

    @Transactional
    @Test
    void getAllBookingByUserId_shouldReturnBookingsIfBookingStateIsPast() {
        Page<Booking> pages = new PageImpl<>(List.of(booking));

        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(bookingRepository.getAllByBookerIdOrderByStartDesc(any(Long.class))).thenReturn(List.of(booking));
        when(bookingRepository.findByBookerIdAndEndInPast(any(Long.class), any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(pages);

        Pageable page = PageMaker.makePageableWithSort(FROM, SIZE);

        List<BookingDto> bookings = bookingService.getAllBookingByUserId(anna.getId(), STATE_PAST, page);

        assertNotNull(bookings, "List of bookings should not be null");
        assertFalse(bookings.isEmpty(), "List of bookings should not be empty");
        assertEquals(1, bookings.size());

        verify(userRepository, times(1)).existsById(anna.getId());
    }

    @Transactional
    @Test
    void getAllBookingByUserId_shouldReturnBookingsIfBookingStateIsFuture() {
        Page<Booking> pages = new PageImpl<>(List.of(booking));

        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(bookingRepository.getAllByBookerIdOrderByStartDesc(any(Long.class))).thenReturn(List.of(booking));
        when(bookingRepository.findByBookerIdAndStartInFuture(any(Long.class), any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(pages);

        Pageable page = PageMaker.makePageableWithSort(FROM, SIZE);

        List<BookingDto> bookings = bookingService.getAllBookingByUserId(anna.getId(), STATE_FUTURE, page);

        assertNotNull(bookings, "List of bookings should not be null");
        assertFalse(bookings.isEmpty(), "List of bookings should not be empty");
        assertEquals(1, bookings.size());

        verify(userRepository, times(1)).existsById(anna.getId());
    }

    @Transactional
    @Test
    void getAllBookingByUserId_shouldReturnBookingsIfBookingStateIsWaiting() {
        booking.setStatus(BookingStatus.WAITING);

        Page<Booking> pages = new PageImpl<>(List.of(booking));

        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(bookingRepository.getAllByBookerIdOrderByStartDesc(any(Long.class))).thenReturn(List.of(booking));
        when(bookingRepository.findByBookerIdAndStatusContaining(any(Long.class), any(BookingStatus.class),
                any(Pageable.class))).thenReturn(pages);

        Pageable page = PageMaker.makePageableWithSort(FROM, SIZE);

        List<BookingDto> bookings = bookingService.getAllBookingByUserId(anna.getId(), STATE_WAITING, page);

        assertNotNull(bookings, "List of bookings should not be null");
        assertFalse(bookings.isEmpty(), "List of bookings should not be empty");
        assertEquals(1, bookings.size());

        verify(userRepository, times(1)).existsById(anna.getId());
    }

    @Transactional
    @Test
    void getAllBookingByUserId_shouldReturnBookingsIfBookingStateIsRejected() {
        booking.setStatus(BookingStatus.REJECTED);

        Page<Booking> pages = new PageImpl<>(List.of(booking));

        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(bookingRepository.getAllByBookerIdOrderByStartDesc(any(Long.class))).thenReturn(List.of(booking));
        when(bookingRepository.findByBookerIdAndStatusContaining(any(Long.class), any(BookingStatus.class),
                any(Pageable.class))).thenReturn(pages);

        Pageable page = PageMaker.makePageableWithSort(FROM, SIZE);

        List<BookingDto> bookings = bookingService.getAllBookingByUserId(anna.getId(), STATE_REJECTED, page);

        assertNotNull(bookings, "List of bookings should not be null");
        assertFalse(bookings.isEmpty(), "List of bookings should not be empty");
        assertEquals(1, bookings.size());

        verify(userRepository, times(1)).existsById(anna.getId());
    }

    @Transactional
    @Test
    void getAllBookingByUserId_shouldThrowExceptionBookingStateIsUnknownState() {
        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(bookingRepository.getAllByBookerIdOrderByStartDesc(any(Long.class))).thenReturn(List.of(booking));

        Pageable page = PageMaker.makePageableWithSort(FROM, SIZE);

        assertThrows(StatusBookingException.class,
                () -> bookingService.getAllBookingByUserId(anna.getId(), UNKNOWN_STATE, page));

        verify(userRepository, times(1)).existsById(anna.getId());
    }
}