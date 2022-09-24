package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.BookingValidationException;
import ru.practicum.shareit.booking.exception.UnavailableItemException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.common.TestObjectMaker.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    private BookingService bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;

    private User booker;
    private Item item;
    private Booking bookingWithoutId;
    private Booking booking;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, userRepository);
        booker = makeUser(1L, "Maria", "maria@ya.ru");
        User owner = makeUser(2L, "Oleg", "oleg@ya.ru");
        item = makeItem(1L, "item", "description", true, owner, null, null);
        bookingWithoutId = makeBooking(null, LocalDateTime.of(2022, 10, 10, 10, 10, 10),
                LocalDateTime.of(2022, 10, 11, 10, 10, 10), BookingStatus.WAITING, item, booker);
        booking = makeBooking(1L, LocalDateTime.of(2022, 10, 10, 10, 10, 10),
                LocalDateTime.of(2022, 10, 11, 10, 10, 10), BookingStatus.WAITING, item, booker);
    }

    @Test
    public void checkSaveBooking() {
        when(bookingRepository.save(bookingWithoutId)).thenReturn(booking);

        Booking savedBooking = bookingService.saveBooking(bookingWithoutId);
        assertEquals(booking, savedBooking);

        verify(bookingRepository).save(bookingWithoutId);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    public void checkSaveBooking_unavailableItemException() {
        item.setAvailable(false);

        final var thrown = assertThrows(UnavailableItemException.class,
                () -> bookingService.saveBooking(bookingWithoutId));
        assertEquals("Item " + 1L + " is unavailable now", thrown.getMessage());

        verifyNoInteractions(bookingRepository);
    }

    @Test
    public void checkApproveBooking() {
        item.setOwner(booker);
        bookingWithoutId.setId(1L);
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(bookingWithoutId.getId()))
                .thenReturn(Optional.of(bookingWithoutId));
        when(bookingRepository.save(booking)).thenReturn(booking);

        Booking bookingFromDb = bookingService.approveBooking(bookingWithoutId.getId(), true, booker.getId());
        assertEquals(booking, bookingFromDb);

        verify(bookingRepository).findById(bookingWithoutId.getId());
        verify(bookingRepository).save(booking);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    public void checkApproveBooking_accessException() {
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        final var thrown = assertThrows(ItemNotFoundException.class,
                () -> bookingService.approveBooking(booking.getId(), true, booker.getId()));
        assertEquals("Item " + booking.getItem().getId() +
                " from booking " + booking.getId() + " doesn't belong you", thrown.getMessage());

        verify(bookingRepository).findById(booking.getId());
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    public void checkApproveBooking_alreadyApprovedException() {
        item.setOwner(booker);
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        final var thrown = assertThrows(BookingValidationException.class,
                () -> bookingService.approveBooking(booking.getId(), true, booker.getId()));
        assertEquals("Booking " + booking.getId() +
                " is already " + BookingStatus.APPROVED, thrown.getMessage());

        verify(bookingRepository).findById(booking.getId());
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    public void checkGetBookingById() {
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        Booking bookingFromDb = bookingService.getBookingById(booking.getId(), booker.getId());
        assertEquals(booking, bookingFromDb);

        verify(bookingRepository).findById(booking.getId());
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    public void checkGetBookingById_noAccessException() {
        Long alienId = 3L;
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        final var thrown = assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingById(booking.getId(), alienId));
        assertEquals("No access to booking " + booking.getId(), thrown.getMessage());

        verify(bookingRepository).findById(booking.getId());
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    public void checkGetBookingById_notFoundException() {
        when(bookingRepository.findById(any())).thenReturn(Optional.empty());

        final var thrown = assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingById(1L, 1L));
        assertEquals("Booking " + 1L + " not found", thrown.getMessage());

        verify(bookingRepository).findById(any());
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    public void checkGetBookingRequestsByUserId() {
        // IT
    }

    @Test
    public void checkGetBookingsByOwnerId() {
        // IT
    }

    @Test
    public void checkGetLastBookingByItem() {
        // IT
    }

    @Test
    public void checkGetNextBookingByItem() {
        // IT
    }
}