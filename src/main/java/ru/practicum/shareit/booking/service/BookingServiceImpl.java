package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.exception.UnavailableItemException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;

    @Override
    public Booking saveBooking(Booking booking) {
        if (booking.getItem().getAvailable().equals(Boolean.TRUE)) {
            return bookingRepository.save(booking);
        } else {
            throw new UnavailableItemException(booking.getItem().getId());
        }
    }

    @Override
    public Booking approveBooking(Booking booking) {
        return null;
    }

    @Override
    public Booking getBookingById(Long bookingId) {
        return null;
    }

    @Override
    public List<Booking> getBookingRequestsByUserId(Long userId) {
        return null;
    }

    @Override
    public List<Booking> getBookingsByUserId(Long userId) {
        return null;
    }
}
