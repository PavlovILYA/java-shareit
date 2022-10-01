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
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.Constants.BOOKING_API_PREFIX;
import static ru.practicum.shareit.Constants.USER_ID_HEADER;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = BOOKING_API_PREFIX)
public class BookingController {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    @PostMapping
    public BookingCreateDto saveBooking(@RequestBody BookingCreateDto bookingCreateDto,
                                        @RequestHeader(USER_ID_HEADER) Long userId) {
        log.debug("POST {} userId={} body: {}", BOOKING_API_PREFIX, userId, bookingCreateDto);
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
        log.debug("PATCH {}/{} userId={} approved={}}", BOOKING_API_PREFIX, bookingId, userId, approved);
        return BookingMapper.toBookingResponseDto(
                bookingService.approveBooking(bookingId, approved, userId));
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@PathVariable("bookingId") Long bookingId,
                                         @RequestHeader(USER_ID_HEADER) Long userId) {
        log.debug("GET {}/{} userId={}}", BOOKING_API_PREFIX, bookingId, userId);
        return BookingMapper.toBookingResponseDto(
                bookingService.getBookingById(bookingId, userId));
    }

    @GetMapping
    public List<BookingResponseDto> getMyBookingRequests(@RequestParam(name = "state") String state,
                                                         @RequestHeader(USER_ID_HEADER) Long userId,
                                                         @RequestParam(name = "from") int from,
                                                         @RequestParam(name = "size") int size) {
        log.debug("GET {} userId={} state={} from={} size={}", BOOKING_API_PREFIX, userId, state, from, size);
        BookingState bookingState = BookingState.fromString(state);
        return bookingService.getBookingRequestsByUserId(userId, bookingState, from, size).stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getMyBookings(@RequestParam(name = "state") String state,
                                                  @RequestHeader(USER_ID_HEADER) Long userId,
                                                  @RequestParam(name = "from") int from,
                                                  @RequestParam(name = "size") int size) {
        log.debug("GET {}/owner userId={} state={} from={} size={}", BOOKING_API_PREFIX, userId, state, from, size);
        BookingState bookingState = BookingState.fromString(state);
        return bookingService.getBookingsByOwnerId(userId, bookingState, from, size).stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    private void compareBookerAndItemOwner(User booker, Item item) {
        if (booker.getId().equals(item.getOwner().getId())) {
            throw new BookingNotFoundException("Item " + item.getId() + " is your");
        }
    }
}
