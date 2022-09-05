package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingReturnDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.exception.BookingValidationException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingCreateDto saveBooking(@Valid @RequestBody BookingCreateDto bookingCreateDto,
                                        @RequestHeader(USER_ID_HEADER) Long userId) {
        validateBookingDuration(bookingCreateDto.getStart(), bookingCreateDto.getEnd());
        Item item = itemService.getItem(bookingCreateDto.getItemId());
        User booker = userService.getUser(userId);
        Booking booking = BookingMapper.fromBookingCreateDto(bookingCreateDto, item, booker);
        return BookingMapper.toBookingCreateDto(
                bookingService.saveBooking(booking));
    }

    @PatchMapping("/{bookingId}")
    public BookingReturnDto approveBooking(@PathVariable("bookingId") Long bookingId,
                                           @RequestParam("approved") Boolean approved,
                                           @RequestHeader(USER_ID_HEADER) Long userId) {
        return BookingMapper.toBookingReturnDto(
                bookingService.approveBooking(bookingId, approved, userId));
    }

    @GetMapping("/{bookingId}")
    public BookingReturnDto getBooking(@PathVariable("bookingId") Long bookingId,
                                       @RequestHeader(USER_ID_HEADER) Long userId) {
        return BookingMapper.toBookingReturnDto(
                bookingService.getBookingById(bookingId, userId));
    }

    @GetMapping
    public List<BookingReturnDto> getMyBookingRequests(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                                       @RequestHeader(USER_ID_HEADER) Long userId) {
        BookingState bookingState = BookingState.fromString(state);
        return bookingService.getBookingRequestsByUserId(userId, bookingState).stream()
                .map(BookingMapper::toBookingReturnDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingReturnDto> getMyBookings(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                               @RequestHeader(USER_ID_HEADER) Long userId) {
        BookingState bookingState = BookingState.fromString(state);
        return bookingService.getBookingsByOwnerId(userId, bookingState).stream()
                .map(BookingMapper::toBookingReturnDto)
                .collect(Collectors.toList());
    }

    private void validateBookingDuration(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new BookingValidationException("Start time (" + start +
                    ") is after then end time (" + end + ")");
        }
    }
}
