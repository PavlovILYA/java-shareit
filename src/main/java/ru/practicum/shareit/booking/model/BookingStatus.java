package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.booking.exception.InvalidBookingStatusException;

public enum BookingStatus {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED;

    public static BookingStatus fromString(String stringStatus) {
        try {
            return BookingStatus.valueOf(stringStatus);
        } catch (IllegalArgumentException e) {
            throw new InvalidBookingStatusException("Unknown status:  " + stringStatus);
        }
    }

    public static BookingStatus approve(boolean isApproved) {
        return isApproved ? APPROVED : REJECTED;
    }
}
