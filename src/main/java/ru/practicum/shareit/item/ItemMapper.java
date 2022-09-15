package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getItemRequest() == null ? null : item.getItemRequest().getId())
                .build();
    }

    public static Item toItem(ItemDto itemDto, User owner, ItemRequest itemRequest) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description((itemDto.getDescription()))
                .available(itemDto.getAvailable())
                .owner(owner)
                .itemRequest(itemRequest)
                .build();
    }

    public static ItemResponseDto toItemReturnDto(Item item,
                                                  Optional<Booking> lastBooking,
                                                  Optional<Booking> nextBooking) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(getBookingDtoIfExist(lastBooking))
                .nextBooking(getBookingDtoIfExist(nextBooking))
                .comments(item.getComments().stream()
                        .map(ItemMapper::toCommentReturnDto)
                        .collect(Collectors.toList()))
                .requestId(item.getItemRequest() == null ? null : item.getItemRequest().getId())
                .build();
    }

    public static Comment toComment(CommentCreateDto commentCreateDto, User author, Item item) {
        return new Comment(null,
                commentCreateDto.getText(),
                item,
                author,
                LocalDate.now());
    }

    public static CommentResponseDto toCommentReturnDto(Comment comment) {
        return new CommentResponseDto(comment.getId(),
                comment.getText(),
                comment.getItem().getName(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }

    private static ItemResponseDto.BookingDto getBookingDtoIfExist(Optional<Booking> booking) {
        if (booking.isEmpty()) {
            return null;
        } else {
            return new ItemResponseDto.BookingDto(booking.get().getId(),
                    booking.get().getStart(),
                    booking.get().getEnd(),
                    booking.get().getBooker().getId());
        }
    }
}
