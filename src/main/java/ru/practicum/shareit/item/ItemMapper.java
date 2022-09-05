package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable());
    }

    public static Item toItem(ItemDto itemDto, User owner) {
        return new Item(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner);
    }

    public static ItemWithBookingsDto toItemWithBookingsDto(Item item,
                                                            Optional<Booking> lastBooking,
                                                            Optional<Booking> nextBooking) {
        return new ItemWithBookingsDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                getBookingDtoIfExist(lastBooking),
                getBookingDtoIfExist(nextBooking));
    }

    private static ItemWithBookingsDto.BookingDto getBookingDtoIfExist(Optional<Booking> booking) {
        if (booking.isEmpty()) {
            return null;
        } else {
            return new ItemWithBookingsDto.BookingDto(booking.get().getId(),
                    booking.get().getStart(),
                    booking.get().getEnd(),
                    booking.get().getBooker().getId());
        }
    }
}
