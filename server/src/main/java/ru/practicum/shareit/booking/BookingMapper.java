package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static Booking toBooking(BookingCreateDto bookingCreateDto, Item item, User booker) {
        return Booking.builder()
                .id(bookingCreateDto.getId())
                .start(bookingCreateDto.getStart())
                .end(bookingCreateDto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }

    public static BookingCreateDto toBookingCreateDto(Booking booking) {
        return BookingCreateDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .userId(booking.getBooker().getId())
                .build();
    }

    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        Item item = booking.getItem();
        User booker = booking.getBooker();
        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(new BookingResponseDto.ItemDto(item.getId(), item.getName()))
                .booker(new BookingResponseDto.UserDto(booker.getId()))
                .status(booking.getStatus())
                .build();
    }
}
