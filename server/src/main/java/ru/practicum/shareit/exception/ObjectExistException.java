package ru.practicum.shareit.exception;

public class ObjectExistException extends RuntimeException {
    public ObjectExistException(String message) {
        super(message);
    }

    public ObjectExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectExistException(Throwable cause) {
        super(cause);
    }
}
