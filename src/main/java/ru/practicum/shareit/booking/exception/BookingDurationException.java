package ru.practicum.shareit.booking.exception;

public class BookingDurationException extends RuntimeException {
    public BookingDurationException() {
        super();
    }

    public BookingDurationException(String message) {
        super(message);
    }
}
