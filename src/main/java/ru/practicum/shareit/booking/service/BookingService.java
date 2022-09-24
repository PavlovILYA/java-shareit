package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface BookingService {
    Booking saveBooking(Booking booking);

    Booking approveBooking(Long bookingId, Boolean isApproved, Long ownerId);

    Booking getBookingById(Long bookingId, Long userId);

    List<Booking> getBookingRequestsByUserId(Long userId, BookingState state, int from, int size);

    List<Booking> getBookingsByOwnerId(Long ownerId, BookingState state, int from, int size);

    Booking getLastBookingByItem(Item item);

    Booking getNextBookingByItem(Item item);
}
