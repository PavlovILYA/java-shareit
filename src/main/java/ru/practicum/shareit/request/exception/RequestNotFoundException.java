package ru.practicum.shareit.request.exception;

public class RequestNotFoundException extends RuntimeException {
    public RequestNotFoundException(Long id) {
        super("Item request " + id + " not found");
    }
}
