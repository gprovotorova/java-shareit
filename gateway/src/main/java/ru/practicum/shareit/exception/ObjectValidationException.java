package ru.practicum.shareit.exception;

public class ObjectValidationException extends RuntimeException {
    public ObjectValidationException(String message) {
        super(message);
    }

    public ObjectValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectValidationException(Throwable cause) {
        super(cause);
    }
}
