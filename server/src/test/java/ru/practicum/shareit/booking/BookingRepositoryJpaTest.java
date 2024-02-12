package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class BookingRepositoryJpaTest {
    private static final LocalDateTime DATE = LocalDateTime.now();

    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository bookingRepository;
    private User anna = new User();
    private User galina = new User();
    private Item item = new Item();
    private PageRequest page = PageRequest.of(0, 10).withSort(Sort.Direction.DESC, "id");


    @BeforeEach
    void saveInfo() {
        galina.setName("Galina");
        galina.setEmail("galina@mail.ru");
        em.persist(galina);

        anna.setName("Anna");
        anna.setEmail("anna@mail.ru");
        em.persist(anna);

        item.setName("book");
        item.setDescription("very interesting romantic book description");
        item.setAvailable(true);
        item.setOwner(galina);
        item.setRequestId(null);
        em.persist(item);
        em.flush();
    }

    @Test
    void getBookingByStateByUserId_findByBookerIdOrderByStartDesc() {
        Booking booking = new Booking();
        booking.setStart(DATE.minusDays(2));
        booking.setEnd(DATE.plusDays(2));
        booking.setItem(item);
        booking.setBooker(anna);
        booking.setStatus(BookingStatus.APPROVED);
        em.persist(booking);
        em.flush();

        Page<Booking> resultBookings = bookingRepository.findByBookerIdOrderByStartDesc(anna.getId(), page);
        assertEquals(1, resultBookings.getNumberOfElements());
    }

    @Test
    void getBookingByStateByUserId_findCurrentBookingsByBookerId() {
        Booking booking = new Booking();
        booking.setStart(DATE.minusDays(2));
        booking.setEnd(DATE.plusDays(2));
        booking.setItem(item);
        booking.setBooker(anna);
        booking.setStatus(BookingStatus.APPROVED);
        em.persist(booking);
        em.flush();

        Page<Booking> resultBookings = bookingRepository.findCurrentBookingsByBookerId(anna.getId(), DATE, page);
        assertEquals(1, resultBookings.getNumberOfElements());
    }

    @Test
    void getBookingByStateByUserId_findByBookerIdAndEndInPast() {
        Booking booking = new Booking();
        booking.setStart(DATE.minusDays(3));
        booking.setEnd(DATE.minusDays(1));
        booking.setItem(item);
        booking.setBooker(anna);
        booking.setStatus(BookingStatus.APPROVED);
        em.persist(booking);
        em.flush();

        Page<Booking> resultBookings = bookingRepository.findByBookerIdAndEndInPast(anna.getId(), DATE, page);
        assertEquals(1, resultBookings.getNumberOfElements());
    }

    @Test
    void getBookingByStateByUserId_findByBookerIdAndStartInFuture() {
        Booking booking = new Booking();
        booking.setStart(DATE.plusDays(1));
        booking.setEnd(DATE.plusDays(3));
        booking.setItem(item);
        booking.setBooker(anna);
        booking.setStatus(BookingStatus.APPROVED);
        em.persist(booking);
        em.flush();

        Page<Booking> resultBookings = bookingRepository.findByBookerIdAndStartInFuture(anna.getId(), DATE, page);
        assertEquals(1, resultBookings.getNumberOfElements());
    }

    @Test
    void getBookingByStateByUserId_findByBookerIdAndStatusWaiting() {
        Booking booking = new Booking();
        booking.setStart(DATE.minusDays(2));
        booking.setEnd(DATE.plusDays(2));
        booking.setItem(item);
        booking.setBooker(anna);
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);
        em.flush();

        Page<Booking> resultBookings = bookingRepository.findByBookerIdAndStatusContaining(anna.getId(),
                BookingStatus.WAITING, page);
        assertEquals(1, resultBookings.getNumberOfElements());
    }

    @Test
    void getBookingByStateByUserId_findByBookerIdAndStatusRejected() {
        Booking booking = new Booking();
        booking.setStart(DATE.minusDays(2));
        booking.setEnd(DATE.plusDays(2));
        booking.setItem(item);
        booking.setBooker(anna);
        booking.setStatus(BookingStatus.REJECTED);
        em.persist(booking);
        em.flush();

        Page<Booking> resultBookings = bookingRepository.findByBookerIdAndStatusContaining(anna.getId(),
                BookingStatus.REJECTED, page);
        assertEquals(1, resultBookings.getNumberOfElements());
    }


    @Test
    void getBookingByStateByUserId_getAllByBookerIdOrderByStartDesc() {
        Booking booking = new Booking();
        booking.setStart(DATE.minusDays(2));
        booking.setEnd(DATE.plusDays(2));
        booking.setItem(item);
        booking.setBooker(anna);
        booking.setStatus(BookingStatus.REJECTED);
        em.persist(booking);
        em.flush();

        List<Booking> resultBookings = bookingRepository.getAllByBookerIdOrderByStartDesc(anna.getId());
        assertEquals(1, resultBookings.size());
    }

    @Test
    void getBookingByStateByOwnerId_getAllBookingsForOwnersItems() {
        Booking booking = new Booking();
        booking.setStart(DATE.minusDays(2));
        booking.setEnd(DATE.plusDays(2));
        booking.setItem(item);
        booking.setBooker(anna);
        booking.setStatus(BookingStatus.APPROVED);
        em.persist(booking);
        em.flush();

        Page<Booking> resultBookings = bookingRepository.getAllBookingsForOwnersItems(galina.getId(), page);
        assertEquals(1, resultBookings.getNumberOfElements());
    }

    @Test
    void getBookingByStateByOwnerId_getCurrentBookingsForOwnersItemsd() {
        Booking booking = new Booking();
        booking.setStart(DATE.minusDays(2));
        booking.setEnd(DATE.plusDays(2));
        booking.setItem(item);
        booking.setBooker(anna);
        booking.setStatus(BookingStatus.APPROVED);
        em.persist(booking);
        em.flush();

        Page<Booking> resultBookings = bookingRepository.getCurrentBookingsForOwnersItems(galina.getId(), DATE,
                DATE.plusDays(1), page);
        assertEquals(1, resultBookings.getNumberOfElements());
    }

    @Test
    void getBookingByStateByOwnerId_getPastBookingsForOwnersItems() {
        Booking booking = new Booking();
        booking.setStart(DATE.minusDays(3));
        booking.setEnd(DATE.minusDays(1));
        booking.setItem(item);
        booking.setBooker(anna);
        booking.setStatus(BookingStatus.APPROVED);
        em.persist(booking);
        em.flush();

        Page<Booking> resultBookings = bookingRepository.getPastBookingsForOwnersItems(galina.getId(), DATE, page);
        assertEquals(1, resultBookings.getNumberOfElements());
    }

    @Test
    void getBookingByStateByOwnerId_getFutureBookingsForOwnersItems() {
        Booking booking = new Booking();
        booking.setStart(DATE.plusDays(1));
        booking.setEnd(DATE.plusDays(2));
        booking.setItem(item);
        booking.setBooker(anna);
        booking.setStatus(BookingStatus.APPROVED);
        em.persist(booking);
        em.flush();

        Page<Booking> resultBookings = bookingRepository.getFutureBookingsForOwnersItems(galina.getId(), DATE, page);
        assertEquals(1, resultBookings.getNumberOfElements());
    }

    @Test
    void getBookingByStateByOwnerId_getBookingsForOwnersWithStatusWaiting() {
        Booking booking = new Booking();
        booking.setStart(DATE.minusDays(2));
        booking.setEnd(DATE.plusDays(2));
        booking.setItem(item);
        booking.setBooker(anna);
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);
        em.flush();

        Page<Booking> resultBookings = bookingRepository.getBookingsForOwnersWithStatusContaining(galina.getId(),
                BookingStatus.WAITING, page);
        assertEquals(1, resultBookings.getNumberOfElements());
    }

    @Test
    void getBookingByStateByOwnerId_getBookingsForOwnersWithStatusRejected() {
        Booking booking = new Booking();
        booking.setStart(DATE.minusDays(2));
        booking.setEnd(DATE.plusDays(2));
        booking.setItem(item);
        booking.setBooker(anna);
        booking.setStatus(BookingStatus.REJECTED);
        em.persist(booking);
        em.flush();

        Page<Booking> resultBookings = bookingRepository.getBookingsForOwnersWithStatusContaining(galina.getId(),
                BookingStatus.REJECTED, page);
        assertEquals(1, resultBookings.getNumberOfElements());
    }


    @AfterEach
    void deleteData() {
        bookingRepository.deleteAll();
    }
}