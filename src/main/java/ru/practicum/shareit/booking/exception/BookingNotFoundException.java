package ru.practicum.shareit.booking.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException() {
        super();
    }

    public BookingNotFoundException(Long id) {
        super("Booking " + id + " not found");
    }
}
