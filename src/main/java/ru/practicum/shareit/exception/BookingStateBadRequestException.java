package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class BookingStateBadRequestException extends RuntimeException {

    public BookingStateBadRequestException(final String message) {
        super(message);
    }

}
