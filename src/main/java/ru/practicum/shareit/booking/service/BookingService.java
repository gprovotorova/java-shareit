package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingDto bookingDto, Long userId);

    BookingDto updateBooking(Long bookingId, Long userId, Boolean approved);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> getAllBookingByUserId(Long userId, String stateName, Integer from, Integer size);

    List<BookingDto> getAllBookingByOwnerId(Long userId, String stateName, Integer from, Integer size);
}
