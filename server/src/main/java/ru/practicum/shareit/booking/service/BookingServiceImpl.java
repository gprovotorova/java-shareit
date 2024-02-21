package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InvalidPathVariableException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ObjectValidationException;
import ru.practicum.shareit.exception.StatusBookingException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("Booking with id= " + bookingId + " not found"));
        if (booking.getItem().getOwner().getId().equals(userId) || booking.getBooker().getId().equals(userId)) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new ObjectNotFoundException("The user isn't the owner of item");
        }
    }

    @Override
    @Transactional
    public BookingDto createBooking(BookingDto bookingDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id= " + userId + " not found."));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ObjectNotFoundException("Item with id= " + bookingDto.getItemId() +
                        " not found."));
        Booking booking = BookingMapper.toBooking(user, item, bookingDto);
        booking.setStatus(BookingStatus.WAITING);
        if (booking.getBooker().getId().equals(booking.getItem().getOwner().getId())) {
            throw new ObjectNotFoundException("Owner cannot book his item.");
        }
        if (!booking.getItem().getAvailable()) {
            throw new ObjectValidationException("Item is not available.");
        }
        if (booking.getStart().equals(booking.getEnd())) {
            throw new ObjectValidationException("The start of the booking cannot be equal to the end.");
        }
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new ObjectValidationException("The start of the booking cannot be after the end.");
        }
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new ObjectValidationException("The start of the booking cannot be in the past.");
        }
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ObjectValidationException("The end of the booking cannot be in the past.");
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto updateBooking(Long bookingId, Long userId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("Booking with id= " + bookingId + " not found."));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException("The user isn't the owner of item.");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED) && approved) {
            throw new ObjectValidationException("The booking is already approved.");
        }
        if (booking.getStatus().equals(BookingStatus.REJECTED) && !approved) {
            throw new ObjectValidationException("The booking is already rejected.");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllBookingByUserId(Long userId, String stateName, Pageable page) {
        userRepository.existsById(userId);
        List<Booking> bookings = bookingRepository.getAllByBookerIdOrderByStartDesc(userId);
        if (bookings.isEmpty()) {
            throw new ObjectNotFoundException("The user " + userId + " has no reserved items");
        }
        if (page.isUnpaged()) {
            bookings = getBookingByStateByUserId(userId, stateName);
        } else {
            bookings = getBookingByStateByUserId(userId, stateName, page).getContent();
        }
        return bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());

    }

    private Page<Booking> getBookingByStateByUserId(Long userId, String stateName, Pageable page) {
        LocalDateTime dateTime = LocalDateTime.now();
        try {
            switch (stateName) {
                case "ALL":
                    return bookingRepository.findByBookerIdOrderByStartDesc(userId, page);
                case "CURRENT":
                    return bookingRepository.findCurrentBookingsByBookerId(userId, dateTime, page);
                case "PAST":
                    return bookingRepository.findByBookerIdAndEndInPast(userId, dateTime, page);
                case "FUTURE":
                    return bookingRepository.findByBookerIdAndStartInFuture(userId, dateTime, page);
                case "WAITING":
                    return bookingRepository.findByBookerIdAndStatusContaining(userId, BookingStatus.WAITING, page);
                case "REJECTED":
                    return bookingRepository.findByBookerIdAndStatusContaining(userId, BookingStatus.REJECTED, page);
                default:
                    throw new StatusBookingException(String.format("Unknown state: %s", stateName));
            }
        } catch (IllegalArgumentException iae) {
            throw new InvalidPathVariableException("Unknown state: " + stateName);
        }
    }

    private List<Booking> getBookingByStateByUserId(Long userId, String stateName) {
        LocalDateTime dateTime = LocalDateTime.now();
        try {
            switch (stateName) {
                case "ALL":
                    return bookingRepository.findByBookerIdOrderByStartDesc(userId);
                case "CURRENT":
                    return bookingRepository.findCurrentBookingsByBookerId(userId, dateTime);
                case "PAST":
                    return bookingRepository.findByBookerIdAndEndInPast(userId, dateTime);
                case "FUTURE":
                    return bookingRepository.findByBookerIdAndStartInFuture(userId, dateTime);
                case "WAITING":
                    return bookingRepository.findByBookerIdAndStatusContaining(userId, BookingStatus.WAITING);
                case "REJECTED":
                    return bookingRepository.findByBookerIdAndStatusContaining(userId, BookingStatus.REJECTED);
                default:
                    throw new StatusBookingException(String.format("Unknown state: %s", stateName));
            }
        } catch (IllegalArgumentException iae) {
            throw new InvalidPathVariableException("Unknown state: " + stateName);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllBookingByOwnerId(Long userId, String stateName, Pageable page) {
        userRepository.existsById(userId);
        List<Booking> bookings = bookingRepository.getAllByItemOwnerIdOrderByStartDesc(userId);
        if (bookings.isEmpty()) {
            throw new ObjectNotFoundException("The user " + userId + " has no reserved items");
        }
        if (page.isUnpaged()) {
            bookings = getBookingByStateByOwnerId(userId, stateName);
        } else {
            bookings = getBookingByStateByOwnerId(userId, stateName, page).getContent();
        }
        return bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private Page<Booking> getBookingByStateByOwnerId(Long userId, String stateName, Pageable page) {
        LocalDateTime dateTime = LocalDateTime.now();
        try {
            switch (stateName) {
                case "ALL":
                    return bookingRepository.getAllBookingsForOwnersItems(userId, page);
                case "CURRENT":
                    return bookingRepository.getCurrentBookingsForOwnersItems(userId, dateTime, dateTime, page);
                case "PAST":
                    return bookingRepository.getPastBookingsForOwnersItems(userId, dateTime, page);
                case "FUTURE":
                    return bookingRepository.getFutureBookingsForOwnersItems(userId, dateTime, page);
                case "WAITING":
                    return bookingRepository.getBookingsForOwnersWithStatusContaining(userId, BookingStatus.WAITING,
                            page);
                case "REJECTED":
                    return bookingRepository.getBookingsForOwnersWithStatusContaining(userId, BookingStatus.REJECTED,
                            page);
                default:
                    throw new StatusBookingException(String.format("Unknown state: %s", stateName));
            }
        } catch (IllegalArgumentException iae) {
            throw new InvalidPathVariableException("Unknown state: " + stateName);
        }
    }

    private List<Booking> getBookingByStateByOwnerId(Long userId, String stateName) {
        LocalDateTime dateTime = LocalDateTime.now();
        try {
            switch (stateName) {
                case "ALL":
                    return bookingRepository.getAllBookingsForOwnersItems(userId);
                case "CURRENT":
                    return bookingRepository.getCurrentBookingsForOwnersItems(userId, dateTime, dateTime);
                case "PAST":
                    return bookingRepository.getPastBookingsForOwnersItems(userId, dateTime);
                case "FUTURE":
                    return bookingRepository.getFutureBookingsForOwnersItems(userId, dateTime);
                case "WAITING":
                    return bookingRepository.getBookingsForOwnersWithStatusContaining(userId, BookingStatus.WAITING);
                case "REJECTED":
                    return bookingRepository.getBookingsForOwnersWithStatusContaining(userId, BookingStatus.REJECTED);
                default:
                    throw new StatusBookingException(String.format("Unknown state: %s", stateName));
            }
        } catch (IllegalArgumentException iae) {
            throw new InvalidPathVariableException("Unknown state: " + stateName);
        }
    }
}
