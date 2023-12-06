package ru.practicum.shareit.exception;

public class StatusBookingException extends RuntimeException {
    public StatusBookingException(String message) {
        super(message);
    }
    public StatusBookingException(String message, Throwable cause) {
        super(message, cause);
    }

    public StatusBookingException(Throwable cause) {
        super(cause);
    }

}
