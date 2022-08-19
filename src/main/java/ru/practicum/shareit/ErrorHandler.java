package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.item.exception.WrongItemOwnerException;
import ru.practicum.shareit.user.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final MethodArgumentNotValidException e) {
        log.info("400 {}", e.getMessage());
        return new ErrorResponse(LocalDateTime.now(), 400, e.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailExistException(final EmailAlreadyExistsException e) {
        log.info("409 {}", e.getMessage());
        return new ErrorResponse(LocalDateTime.now(), 409, e.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(final UserNotFoundException e) {
        log.info("404 {}", e.getMessage());
        return new ErrorResponse(LocalDateTime.now(), 404, e.getMessage());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingRequestHeaderException(final MissingRequestHeaderException e) {
        log.info("400 {}", e.getMessage());
        return new ErrorResponse(LocalDateTime.now(), 400, e.getMessage());
    }

    @ExceptionHandler(WrongItemOwnerException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleWrongItemOwnerException(final WrongItemOwnerException e) {
        log.info("403 {}", e.getMessage());
        return new ErrorResponse(LocalDateTime.now(), 403, e.getMessage());
    }
}
