package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.BookingValidationException;
import ru.practicum.shareit.booking.exception.UnavailableItemException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;

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
    public Booking approveBooking(Long bookingId, Boolean isApproved, Long ownerId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new BookingNotFoundException(bookingId);
        });
        BookingStatus newStatus = BookingStatus.approve(isApproved);
        checkBookingBeforeApprove(booking, newStatus, ownerId);
        booking.setStatus(newStatus);
        return bookingRepository.save(booking);
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

    private void checkBookingBeforeApprove(Booking booking, BookingStatus newStatus, Long ownerId) {
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ItemNotFoundException("Item " + booking.getItem().getId() +
                    " from booking " + booking.getId() + " doesn't belong you");
        }
        if (newStatus.equals(booking.getStatus())) {
            throw new BookingValidationException("Booking " + booking.getId() +
                    " is already " + newStatus);
        }
    }
}
