package ru.practicum.shareit.common;

import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TestObjectMaker {
    public static User makeUser(Long id, String name, String email) {
        return User.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();
    }

    public static UserDto makeUserDto(Long id, String name, String email) {
        return UserDto.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();
    }

    public static Item makeItem(Long id, String name, String description, Boolean available,
                                User owner, List<Comment> comments, ItemRequest itemRequest) {
        return Item.builder()
                .id(id)
                .name(name)
                .description(description)
                .available(available)
                .owner(owner)
                .comments(comments)
                .itemRequest(itemRequest)
                .build();
    }

    public static ItemDto makeItemDto(Long id, String name, String description,
                                      Boolean available, Long requestId) {
        return ItemDto.builder()
                .id(id)
                .name(name)
                .description(description)
                .available(available)
                .requestId(requestId)
                .build();
    }

    public static ItemRequest makeRequest(Long id, String description, LocalDateTime created,
                                          User requester, List<Item> items) {
        return ItemRequest.builder()
                .id(id)
                .description(description)
                .created(created)
                .requester(requester)
                .items(items)
                .build();
    }

    public static ItemResponseDto makeItemResponseDto(Long id, String name, String description,
                                                      Boolean available, Long requestId,
                                                      BookingResponseDto lastBooking,
                                                      BookingResponseDto nextBooking,
                                                      List<CommentResponseDto> comments) {
        return ItemResponseDto.builder()
                .id(id)
                .name(name)
                .description(description)
                .available(available)
                .lastBooking(lastBooking == null ? null : new ItemResponseDto.BookingDto(lastBooking.getId(),
                        lastBooking.getStart(), lastBooking.getEnd(), lastBooking.getBooker().getId()))
                .nextBooking(nextBooking == null ? null : new ItemResponseDto.BookingDto(nextBooking.getId(),
                        nextBooking.getStart(), nextBooking.getEnd(), nextBooking.getBooker().getId()))
                .comments(comments)
                .requestId(requestId)
                .build();
    }

    public static Booking makeBooking(Long id, LocalDateTime start, LocalDateTime end, BookingStatus status,
                                      Item item, User booker) {
        return Booking.builder()
                .id(id)
                .start(start)
                .end(end)
                .status(status)
                .item(item)
                .booker(booker)
                .build();
    }

    public static BookingResponseDto makeBookingResponseDto(Long id, LocalDateTime start, LocalDateTime end,
                                                            BookingStatus status, Item item, User booker) {
        return BookingResponseDto.builder()
                .id(id)
                .start(start)
                .end(end)
                .item(new BookingResponseDto.ItemDto(item.getId(), item.getName()))
                .booker(new BookingResponseDto.UserDto(booker.getId()))
                .status(status)
                .build();
    }

    public static CommentResponseDto makeCommentResponseDto(Long id, String text, String itemName,
                                                            String authorName, LocalDate created) {
        return CommentResponseDto.builder()
                .id(id)
                .text(text)
                .itemName(itemName)
                .authorName(authorName)
                .created(created)
                .build();
    }

    public static Comment makeComment(Long id, String text, Item item, User author, LocalDate created) {
        return Comment.builder()
                .id(id)
                .text(text)
                .item(item)
                .author(author)
                .created(created)
                .build();
    }
}
