package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.BookingValidationException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private static final String STATE_DEFAULT = "ALL";
    private static final String FROM_DEFAULT = "0";
    private static final String SIZE_DEFAULT = "5";

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    @PostMapping
    public BookingCreateDto saveBooking(@Valid @RequestBody BookingCreateDto bookingCreateDto,
                                        @RequestHeader(USER_ID_HEADER) Long userId) {
        validateBookingDuration(bookingCreateDto.getStart(), bookingCreateDto.getEnd());
        Item item = itemService.getItem(bookingCreateDto.getItemId());
        User booker = userService.getUser(userId);
        compareBookerAndItemOwner(booker, item);
        Booking booking = BookingMapper.toBooking(bookingCreateDto, item, booker);
        return BookingMapper.toBookingCreateDto(
                bookingService.saveBooking(booking));
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@PathVariable("bookingId") Long bookingId,
                                             @RequestParam("approved") Boolean approved,
                                             @RequestHeader(USER_ID_HEADER) Long userId) {
        return BookingMapper.toBookingResponseDto(
                bookingService.approveBooking(bookingId, approved, userId));
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@PathVariable("bookingId") Long bookingId,
                                         @RequestHeader(USER_ID_HEADER) Long userId) {
        return BookingMapper.toBookingResponseDto(
                bookingService.getBookingById(bookingId, userId));
    }

    @GetMapping
    public List<BookingResponseDto> getMyBookingRequests(@RequestParam(name = "state", defaultValue = STATE_DEFAULT)
                                                         String state,
                                                         @RequestHeader(USER_ID_HEADER) Long userId,
                                                         @PositiveOrZero
                                                         @RequestParam(name = "from", defaultValue = FROM_DEFAULT)
                                                         int from,
                                                         @Positive
                                                         @RequestParam(name = "size", defaultValue = SIZE_DEFAULT)
                                                         int size) {
        BookingState bookingState = BookingState.fromString(state);
        return bookingService.getBookingRequestsByUserId(userId, bookingState, from, size).stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getMyBookings(@RequestParam(name = "state", defaultValue = STATE_DEFAULT)
                                                  String state,
                                                  @RequestHeader(USER_ID_HEADER) Long userId,
                                                  @PositiveOrZero
                                                  @RequestParam(name = "from", defaultValue = FROM_DEFAULT) int from,
                                                  @Positive
                                                  @RequestParam(name = "size", defaultValue = SIZE_DEFAULT) int size) {
        BookingState bookingState = BookingState.fromString(state);
        return bookingService.getBookingsByOwnerId(userId, bookingState, from, size).stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    private void validateBookingDuration(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new BookingValidationException("Start time (" + start +
                    ") is after then end time (" + end + ")");
        }
    }

    private void compareBookerAndItemOwner(User booker, Item item) {
        if (booker.getId().equals(item.getOwner().getId())) {
            throw new BookingNotFoundException("Item " + item.getId() + " is your");
        }
    }
}
