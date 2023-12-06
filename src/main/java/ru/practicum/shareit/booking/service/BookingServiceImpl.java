package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
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
@Transactional
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
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
    public List<BookingDto> getAllBookingByUserId(Long userId, String stateName) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id= " + userId + " not found."));
        List<Booking> bookings = bookingRepository.getAllByBookerIdOrderByStartDesc(userId);
        if (bookings.isEmpty()) {
            throw new ObjectNotFoundException("The user " + userId + " has no reserved items");
        }
        LocalDateTime dateTime = LocalDateTime.now();
        switch (stateName) {
            case "ALL":
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findCurrentBookingsByBookerId(userId, dateTime);
                break;
            case "PAST":
                bookings = bookingRepository.findByBookerIdAndEndInPast(userId, dateTime);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBookerIdAndStartInFuture(userId, dateTime);
                break;
            case "WAITING":
                bookings = bookingRepository.findByBookerIdAndStatusContaining(userId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatusContaining(userId, BookingStatus.REJECTED);
                break;
            default:
                throw new StatusBookingException(String.format("Unknown state: %s", stateName));
        }
        return bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());

    }

    @Override
    public List<BookingDto> getAllBookingByOwnerId(Long userId, String stateName) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Owner with id= " + userId + " not found"));
        List<Booking> bookings = bookingRepository.getAllByItemOwnerIdOrderByStartDesc(userId);
        if (bookings.isEmpty()) {
            throw new ObjectNotFoundException("The user " + userId + " has no reserved items");
        }
        LocalDateTime dateTime = LocalDateTime.now();
        switch (stateName) {
            case "ALL":
                bookings = bookingRepository.getAllBookingsForOwnersItems(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.getCurrentBookingsForOwnersItems(userId, dateTime, dateTime);
                break;
            case "PAST":
                bookings = bookingRepository.getPastBookingsForOwnersItems(userId, dateTime);
                break;
            case "FUTURE":
                bookings = bookingRepository.getFutureBookingsForOwnersItems(userId, dateTime);
                break;
            case "WAITING":
                bookings = bookingRepository.getBookingsForOwnersWithStatusContaining(userId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.getBookingsForOwnersWithStatusContaining(userId, BookingStatus.REJECTED);
                break;
            default:
                throw new StatusBookingException(String.format("Unknown state: %s", stateName));
        }
        return bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
