package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.BookingValidationException;
import ru.practicum.shareit.booking.exception.InvalidBookingStatusException;
import ru.practicum.shareit.booking.exception.UnavailableItemException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    public Booking saveBooking(Booking booking) {
        if (!booking.getItem().getAvailable().equals(Boolean.TRUE)) {
            throw new UnavailableItemException(booking.getItem().getId());
        }
        booking = bookingRepository.save(booking);
        log.debug("Saved booking: {}", booking);
        return booking;
    }

    @Override
    public Booking approveBooking(Long bookingId, Boolean isApproved, Long ownerId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new BookingNotFoundException(bookingId);
        });
        BookingStatus newStatus = BookingStatus.approve(isApproved);
        checkBookingBeforeApprove(booking, newStatus, ownerId);
        booking.setStatus(newStatus);
        booking = bookingRepository.save(booking);
        log.debug("Approved booking: {}", booking);
        return booking;
    }

    @Override
    public Booking getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new BookingNotFoundException(bookingId);
        });
        checkAccess(booking, userId);
        log.debug("Returned booking: {}", booking);
        return booking;
    }

    @Override
    public List<Booking> getBookingRequestsByUserId(Long userId, BookingState state, int from, int size) {
        User booker = userRepository.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException(userId);
        });
        Pageable pageRequest = PageRequest.of(from / size, size);
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerOrderByStartDesc(booker, pageRequest).getContent();
                break;
            case CURRENT:
                bookings = bookingRepository.findAllCurrentByBooker(booker, pageRequest).getContent();
                break;
            case PAST:
                bookings = bookingRepository.findAllPastByBooker(booker, pageRequest).getContent();
                break;
            case FUTURE:
                bookings = bookingRepository.findAllFutureByBooker(booker, pageRequest).getContent();
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerAndStatusOrderByStartDesc(booker,
                                BookingStatus.WAITING, pageRequest).getContent();
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerAndStatusOrderByStartDesc(booker,
                                BookingStatus.REJECTED, pageRequest).getContent();
                break;
            default:
                throw new InvalidBookingStatusException();
        }
        log.debug("{} bookings by bookerId={}: {}", state, userId, bookings);
        return bookings;
    }

    @Override
    public List<Booking> getBookingsByOwnerId(Long ownerId, BookingState state, int from, int size) {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> {
            throw new UserNotFoundException(ownerId);
        });
        Pageable pageRequest = PageRequest.of(from / size, size);
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerOrderByStartDesc(owner, pageRequest).getContent();
                break;
            case CURRENT:
                bookings = bookingRepository.findAllCurrentByOwner(owner, pageRequest).getContent();
                break;
            case PAST:
                bookings = bookingRepository.findAllPastByOwner(owner, pageRequest).getContent();
                break;
            case FUTURE:
                bookings = bookingRepository.findAllFutureByOwner(owner, pageRequest).getContent();
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(owner,
                        BookingStatus.WAITING, pageRequest).getContent();
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(owner,
                        BookingStatus.REJECTED, pageRequest).getContent();
                break;
            default:
                throw new InvalidBookingStatusException();
        }
        log.debug("{} bookings by ownerId={}: {}", state, ownerId, bookings);
        return bookings;
    }

    @Override
    public Booking getLastBookingByItem(Item item) {
        Booking booking = bookingRepository.findAllPastOrCurrentByItemDesc(item, PageRequest.of(0, 1)).stream()
                .findFirst().orElse(null);
        log.debug("Last booking by itemId={}: {}", item.getId(), booking);
        return booking;
    }

    @Override
    public Booking getNextBookingByItem(Item item) {
        Booking booking = bookingRepository.findAllFutureByItemAsc(item, PageRequest.of(0, 1)).stream()
                .findFirst().orElse(null);
        log.debug("Next booking by itemId={}: {}", item.getId(), booking);
        return booking;
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

    private void checkAccess(Booking booking, Long userId) {
        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new BookingNotFoundException("No access to booking " + booking.getId());
        }
    }
}
