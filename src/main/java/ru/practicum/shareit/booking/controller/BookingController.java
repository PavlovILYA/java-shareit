package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
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
        return BookingMapper.toBookingCreateDto(bookingService.saveBooking(booking));
    }

    @PatchMapping("/{bookingId}")
    public Booking approveBooking(@PathVariable("bookingId") Long bookingId,
                                           @RequestParam("approved") Boolean approved,
                                           @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingService.approveBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingCreateDto getBooking(@PathVariable("bookingId") Long bookingId,
                                       @RequestHeader(USER_ID_HEADER) Long userId) {
        return null;
    }

    @GetMapping
    public List<BookingCreateDto> getMyBookingRequests(@RequestParam("state") String state,
                                                       @RequestHeader(USER_ID_HEADER) Long userId) {
        return null;
    }

    @GetMapping("/owner")
    public List<BookingCreateDto> getMyBooking(@RequestParam("state") String state,
                                               @RequestHeader(USER_ID_HEADER) Long userId) {
        return null;
    }

    private void validateBookingDuration(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new BookingValidationException("start time (" + start +
                    ") is after then end time (" + end + ")");
        }
    }
}
