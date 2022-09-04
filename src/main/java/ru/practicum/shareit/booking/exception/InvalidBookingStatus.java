package ru.practicum.shareit.booking.exception;

public class InvalidBookingStatus extends RuntimeException {
    public InvalidBookingStatus() {
        super();
    }

    public InvalidBookingStatus(String message) {
        super(message);
    }
}
