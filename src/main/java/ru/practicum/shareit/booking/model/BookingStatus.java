package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.booking.exception.InvalidBookingStatus;

public enum BookingStatus {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED;

    static BookingStatus fromString(String stringStatus) {
        switch (stringStatus) {
            case "WAITING":
                return WAITING;
            case "APPROVED":
                return APPROVED;
            case "REJECTED":
                return REJECTED;
            case "CANCELED":
                return CANCELED;
            default:
                throw new InvalidBookingStatus("BookingStatus " + stringStatus + " doesn't exist");
        }
    }
}
