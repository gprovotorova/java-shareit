package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.ObjectValidationException;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @Valid @RequestBody BookingRequestDto bookingDto) {
        log.info("Creating booking {}", bookingDto);
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getEnd().isEqual(bookingDto.getStart())) {
            throw new ObjectValidationException("Incorrect booking dates.");
        }
        return bookingClient.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long bookingId,
                                         @RequestParam(value = "approved") Boolean approved) {
        log.info("Update booking {}", bookingId);
        return bookingClient.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long bookingId) {
        log.info("Get booking {}", bookingId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity getAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @RequestParam(value = "state", required = false,
                                               defaultValue = "ALL") String state,
                                       @RequestParam(defaultValue = "0") Integer from,
                                       @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get all bookings by user={}", userId);
        return bookingClient.getAllBookingByUserId("", userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam(value = "state", required = false,
                                                defaultValue = "ALL") String state,
                                        @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get all bookings by owner={}", userId);
        return bookingClient.getAllBookingByOwnerId("/owner", userId, state, from, size);
    }
}