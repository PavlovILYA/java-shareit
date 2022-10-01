package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.BookingValidationException;
import ru.practicum.shareit.booking.exception.InvalidBookingStatusException;
import ru.practicum.shareit.booking.exception.UnavailableItemException;
import ru.practicum.shareit.item.exception.InvalidOwnerException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({MethodArgumentNotValidException.class,
//                       CustomValidationException.class,
                       MissingRequestHeaderException.class,
                       UnavailableItemException.class,
                       BookingValidationException.class, // !
                       InvalidBookingStatusException.class, // !
                       ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle400Exception(final Exception e) {
        log.error("Response HTTP {} {}", HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler({UserNotFoundException.class,
                       ItemNotFoundException.class,
                       BookingNotFoundException.class,
                       RequestNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle404Exception(final Exception e) {
        log.error("Response HTTP {} {}", HttpStatus.NOT_FOUND.value(), e.getMessage());
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(InvalidOwnerException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handle403Exception(final InvalidOwnerException e) {
        log.error("Response HTTP {} {}", HttpStatus.FORBIDDEN.value(), e.getMessage());
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.FORBIDDEN.value(), e.getMessage());
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handle409Exception(final Exception e) {
        log.error("Response HTTP {} {}", HttpStatus.CONFLICT.value(), e.getMessage());
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.CONFLICT.value(), e.getMessage());
    }
}
