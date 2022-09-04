package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking saveBooking(Booking booking);

    Booking approveBooking(Booking booking);

    Booking getBookingById(Long bookingId);

    List<Booking> getBookingRequestsByUserId(Long userId);

    List<Booking> getBookingsByUserId(Long userId);
}
