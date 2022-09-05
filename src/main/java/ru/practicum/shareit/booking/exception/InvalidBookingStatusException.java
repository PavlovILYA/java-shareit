package ru.practicum.shareit.booking.exception;

public class InvalidBookingStatusException extends RuntimeException {
    public InvalidBookingStatusException() {
        super();
    }

    public InvalidBookingStatusException(String message) {
        super(message);
    }
}
