package ru.practicum.shareit.exception;

public class InvalidPathVariableException extends RuntimeException {
    public InvalidPathVariableException(String message) {
        super(message);
    }

    public InvalidPathVariableException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPathVariableException(Throwable cause) {
        super(cause);
    }
}
