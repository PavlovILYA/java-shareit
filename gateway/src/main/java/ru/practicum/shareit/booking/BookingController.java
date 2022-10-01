package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.exception.BookingValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

import static ru.practicum.shareit.Constants.*;


@Controller
@RequestMapping(path = BOOKING_API_PREFIX)
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> saveBooking(@Valid @RequestBody BookingCreateDto bookingCreateDto,
											  @RequestHeader(USER_ID_HEADER) Long userId) {
		validateBookingDuration(bookingCreateDto.getStart(), bookingCreateDto.getEnd());
		log.debug("Creating booking: userId={}, body: {}", userId, bookingCreateDto);
		return bookingClient.saveBooking(bookingCreateDto, userId);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveBooking(@PathVariable("bookingId") Long bookingId,
												 @RequestParam("approved") Boolean approved,
												 @RequestHeader(USER_ID_HEADER) Long userId) {
		log.debug("Approving booking {}: userId={}, approved={}}", bookingId, userId, approved);
		return bookingClient.approveBooking(bookingId, approved, userId);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@PathVariable("bookingId") Long bookingId,
											 @RequestHeader(USER_ID_HEADER) Long userId) {
		log.debug("Get booking {}: userId={}}", bookingId, userId);
		return bookingClient.getBooking(bookingId, userId);
	}

	@GetMapping
	public ResponseEntity<Object> getMyBookingRequests(@RequestParam(name = "state", defaultValue = STATE_DEFAULT)
													   String state,
													   @RequestHeader(USER_ID_HEADER) Long userId,
													   @PositiveOrZero
													   @RequestParam(name = "from", defaultValue = FROM_DEFAULT)
													   int from,
													   @Positive
													   @RequestParam(name = "size", defaultValue = SIZE_DEFAULT)
													   int size) {
		BookingState bookingState = BookingState.fromString(state);
		log.debug("Get bookings that user {} booked: state={}, from={}, size={}", userId, state, from, size);
		return bookingClient.getMyBookingRequests(bookingState, userId, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getMyBookings(@RequestParam(name = "state", defaultValue = STATE_DEFAULT)
												  String state,
												  @RequestHeader(USER_ID_HEADER) Long userId,
												  @PositiveOrZero
												  @RequestParam(name = "from", defaultValue = FROM_DEFAULT) int from,
												  @Positive
												  @RequestParam(name = "size", defaultValue = SIZE_DEFAULT) int size) {
		BookingState bookingState = BookingState.fromString(state);
		log.debug("Get bookings that user {} owns: state={}, from={}, size={}", userId, state, from, size);
		return bookingClient.getMyBookings(bookingState, userId, from, size);
	}

	private void validateBookingDuration(LocalDateTime start, LocalDateTime end) {
		if (start.isAfter(end)) {
			throw new BookingValidationException("Start time (" + start +
					") is after then end time (" + end + ")");
		}
	}
}