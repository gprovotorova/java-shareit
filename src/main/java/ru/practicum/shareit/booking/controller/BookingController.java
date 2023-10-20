package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.BookingStateBadRequestException;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Slf4j
public class BookingController {

    private final BookingServiceImpl bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @Valid @RequestBody BookingDto bookingDto) {
        log.info("Creating booking {}", bookingDto);
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId,
                                     @PathParam("approved") @NonNull Boolean approved){
        log.info("Update booking {}", bookingId);
        return bookingService.updateBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long bookingId){
        log.info("Get booking {}", bookingId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @RequestParam(value = "state", required = false, defaultValue = "ALL") String state){
        BookingState bookingState;
        try{
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Unknown state: " + state);
        }
        return bookingService.getAllBookingByUserId(userId, bookingState);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam(value = "state", required = false,
                                                  defaultValue = "ALL") String state){
        BookingState bookingState;
        try{
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Unknown state: " + state);
        }
        return bookingService.getAllBookingByOwnerId(userId, bookingState);
    }
}
