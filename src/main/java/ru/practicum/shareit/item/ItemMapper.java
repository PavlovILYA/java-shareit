package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentReturnDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemReturnDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Collectors;

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
                owner,
                null); // comments
    }

    public static ItemReturnDto toItemReturnDto(Item item,
                                                Optional<Booking> lastBooking,
                                                Optional<Booking> nextBooking) {
        return new ItemReturnDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                getBookingDtoIfExist(lastBooking),
                getBookingDtoIfExist(nextBooking),
                item.getComments().stream()
                        .map(ItemMapper::toCommentReturnDto)
                        .collect(Collectors.toList()));
    }

    public static Comment toComment(CommentCreateDto commentCreateDto, User author, Item item) {
        return new Comment(null,
                commentCreateDto.getText(),
                item,
                author,
                LocalDate.now());
    }

    public static CommentReturnDto toCommentReturnDto(Comment comment) {
        return new CommentReturnDto(comment.getId(),
                comment.getText(),
                comment.getItem().getName(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }

    private static ItemReturnDto.BookingDto getBookingDtoIfExist(Optional<Booking> booking) {
        if (booking.isEmpty()) {
            return null;
        } else {
            return new ItemReturnDto.BookingDto(booking.get().getId(),
                    booking.get().getStart(),
                    booking.get().getEnd(),
                    booking.get().getBooker().getId());
        }
    }
}
