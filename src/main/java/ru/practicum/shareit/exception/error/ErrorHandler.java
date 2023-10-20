package ru.practicum.shareit.exception.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.ObjectExistException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
<<<<<<< HEAD
import ru.practicum.shareit.exception.ObjectValidationException;
=======
import ru.practicum.shareit.exception.ValidationException;
>>>>>>> 61d3a36fb68671b2bc56a32d663def57fc07f660

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final ObjectNotFoundException e) {
        log.info("404 {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
<<<<<<< HEAD
    public ErrorResponse handleBadRequest(final ObjectValidationException e) {
=======
    public ErrorResponse handleBadRequest(final ValidationException e) {
>>>>>>> 61d3a36fb68671b2bc56a32d663def57fc07f660
        log.debug("400 {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleExistError(final ObjectExistException e) {
        log.debug("409 {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleExistError(final RuntimeException e) {
        log.debug("500 {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }
}

