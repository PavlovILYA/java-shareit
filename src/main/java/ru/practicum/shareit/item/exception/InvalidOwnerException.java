package ru.practicum.shareit.item.exception;

public class InvalidOwnerException extends RuntimeException {
    public InvalidOwnerException() {
        super();
    }

    public InvalidOwnerException(String message) {
        super(message);
    }
}
