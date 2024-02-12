package ru.practicum.shareit.exception;

public class UnsupportedStatusException extends RuntimeException {
    public UnsupportedStatusException(String message) {
        super(message);
    }

    public UnsupportedStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedStatusException(Throwable cause) {
        super(cause);
    }
}
