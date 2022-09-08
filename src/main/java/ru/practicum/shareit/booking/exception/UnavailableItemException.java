package ru.practicum.shareit.booking.exception;

public class UnavailableItemException extends RuntimeException {
    public UnavailableItemException() {
        super();
    }

    public UnavailableItemException(Long id) {
        super("Item " + id + " is unavailable now");
    }
}
