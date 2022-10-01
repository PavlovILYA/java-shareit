package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.exception.BookingValidationException;
import ru.practicum.shareit.booking.exception.InvalidBookingStatusException;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({MethodArgumentNotValidException.class,
                       CustomValidationException.class,
                       MissingRequestHeaderException.class,
                       BookingValidationException.class,
                       InvalidBookingStatusException.class,
                       ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle400Exception(final Exception e) {
        log.error("Response HTTP {} {}", HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }
}
