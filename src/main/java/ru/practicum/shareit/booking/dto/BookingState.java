package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.exception.InvalidBookingStatusException;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState fromString(String stringStatus) {
        switch (stringStatus) {
            case "ALL":
                return ALL;
            case "CURRENT":
                return CURRENT;
            case "PAST":
                return PAST;
            case "FUTURE":
                return FUTURE;
            case "WAITING":
                return WAITING;
            case "REJECTED":
                return REJECTED;
            default:
                throw new InvalidBookingStatusException("Unknown state: " + stringStatus);
        }
    }
}
