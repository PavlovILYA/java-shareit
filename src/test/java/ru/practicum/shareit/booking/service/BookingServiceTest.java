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

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, userRepository);
    }

    @Test
    public void checkSaveBooking() {
        User booker = makeUser(1L, "Maria", "maria@ya.ru");
        Item item = makeItem(1L, "item", "description", true, null, null, null);
        Booking bookingBeforeSave = makeBooking(null, LocalDateTime.of(2022, 10, 10, 10, 10, 10), LocalDateTime.of(2022, 10, 11, 10, 10, 10), BookingStatus.WAITING, item, booker);
        Booking bookingAfterSave = makeBooking(1L, LocalDateTime.of(2022, 10, 10, 10, 10, 10), LocalDateTime.of(2022, 10, 11, 10, 10, 10), BookingStatus.WAITING, item, booker);
        when(bookingRepository.save(bookingBeforeSave)).thenReturn(bookingAfterSave);

        Booking savedBooking = bookingService.saveBooking(bookingBeforeSave);
        assertEquals(bookingAfterSave, savedBooking);

        verify(bookingRepository).save(bookingBeforeSave);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    public void checkSaveBooking_unavailableItemException() {
        Item item = makeItem(1L, "item", "description", false, null, null, null);
        Booking bookingBeforeSave = makeBooking(null, LocalDateTime.of(2022, 10, 10, 10, 10, 10), LocalDateTime.of(2022, 10, 11, 10, 10, 10), BookingStatus.WAITING, item, null);

        final var thrown = assertThrows(UnavailableItemException.class, () -> bookingService.saveBooking(bookingBeforeSave));
        assertEquals("Item " + 1L + " is unavailable now", thrown.getMessage());

        verifyNoInteractions(bookingRepository);
    }

    @Test
    public void checkApproveBooking() {
        User booker = makeUser(1L, "Maria", "maria@ya.ru");
        Item item = makeItem(1L, "item", "description", true, booker, null, null);
        Booking booking = makeBooking(1L, LocalDateTime.of(2022, 10, 10, 10, 10, 10), LocalDateTime.of(2022, 10, 11, 10, 10, 10), BookingStatus.WAITING, item, booker);
        Booking bookingAfterApprove = makeBooking(1L, LocalDateTime.of(2022, 10, 10, 10, 10, 10), LocalDateTime.of(2022, 10, 11, 10, 10, 10), BookingStatus.APPROVED, item, booker);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(bookingAfterApprove)).thenReturn(bookingAfterApprove);

        Booking bookingFromDb = bookingService.approveBooking(booking.getId(), true, booker.getId());
        assertEquals(bookingAfterApprove, bookingFromDb);

        verify(bookingRepository).findById(booking.getId());
        verify(bookingRepository).save(bookingAfterApprove);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    public void checkApproveBooking_accessException() {
        User booker = makeUser(1L, "Maria", "maria@ya.ru");
        User owner = makeUser(2L, "Oleg", "oleg@ya.ru");
        Item item = makeItem(1L, "item", "description", true, owner, null, null);
        Booking booking = makeBooking(1L, LocalDateTime.of(2022, 10, 10, 10, 10, 10), LocalDateTime.of(2022, 10, 11, 10, 10, 10), BookingStatus.WAITING, item, booker);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        final var thrown = assertThrows(ItemNotFoundException.class, () -> bookingService.approveBooking(booking.getId(), true, booker.getId()));
        assertEquals("Item " + booking.getItem().getId() +
                " from booking " + booking.getId() + " doesn't belong you", thrown.getMessage());

        verify(bookingRepository).findById(booking.getId());
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    public void checkApproveBooking_alreadyApprovedException() {
        User booker = makeUser(1L, "Maria", "maria@ya.ru");
        Item item = makeItem(1L, "item", "description", true, booker, null, null);
        Booking booking = makeBooking(1L, LocalDateTime.of(2022, 10, 10, 10, 10, 10), LocalDateTime.of(2022, 10, 11, 10, 10, 10), BookingStatus.APPROVED, item, booker);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        final var thrown = assertThrows(BookingValidationException.class, () -> bookingService.approveBooking(booking.getId(), true, booker.getId()));
        assertEquals("Booking " + booking.getId() +
                " is already " + BookingStatus.APPROVED, thrown.getMessage());

        verify(bookingRepository).findById(booking.getId());
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    public void checkGetBookingById() {
        User booker = makeUser(1L, "Maria", "maria@ya.ru");
        Item item = makeItem(1L, "item", "description", true, null, null, null);
        Booking booking = makeBooking(1L, LocalDateTime.of(2022, 10, 10, 10, 10, 10), LocalDateTime.of(2022, 10, 11, 10, 10, 10), BookingStatus.WAITING, item, booker);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        Booking bookingFromDb = bookingService.getBookingById(booking.getId(), booker.getId());
        assertEquals(booking, bookingFromDb);

        verify(bookingRepository).findById(booking.getId());
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    public void checkGetBookingById_noAccessException() {
        Long alienId = 3L;
        User booker = makeUser(1L, "Maria", "maria@ya.ru");
        User owner = makeUser(2L, "Anna", "anna@ya.ru");
        Item item = makeItem(1L, "item", "description", true, owner, null, null);
        Booking booking = makeBooking(1L, LocalDateTime.of(2022, 10, 10, 10, 10, 10), LocalDateTime.of(2022, 10, 11, 10, 10, 10), BookingStatus.WAITING, item, booker);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        final var thrown = assertThrows(BookingNotFoundException.class, () -> bookingService.getBookingById(booking.getId(), alienId));
        assertEquals("No access to booking " + booking.getId(), thrown.getMessage());

        verify(bookingRepository).findById(booking.getId());
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    public void checkGetBookingById_notFoundException() {
        when(bookingRepository.findById(any())).thenReturn(Optional.empty());

        final var thrown = assertThrows(BookingNotFoundException.class, () -> bookingService.getBookingById(1L, 1L));
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