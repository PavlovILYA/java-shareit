package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.CreateValidationGroup;
import ru.practicum.shareit.CustomValidationException;
import ru.practicum.shareit.UpdateValidationGroup;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final RequestService requestService;

    @PostMapping
    public ItemDto saveItem(@RequestHeader(USER_ID_HEADER) Long userId,
                            @Validated({CreateValidationGroup.class}) @RequestBody ItemDto itemDto) {
        User owner = userService.getUser(userId);
        ItemRequest itemRequest = itemDto.getRequestId() == null
                ? null
                : requestService.getRequestById(itemDto.getRequestId());
        Item item = ItemMapper.toItem(itemDto, owner, itemRequest);
        return ItemMapper.toItemDto(itemService.saveItem(item));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(USER_ID_HEADER) Long userId,
                              @PathVariable("itemId") Long itemId,
                              @Validated({UpdateValidationGroup.class}) @RequestBody ItemDto itemDto) {
        validate(itemDto);
        itemDto.setId(itemId);
        User owner = userService.getUser(userId);
        Item item = ItemMapper.toItem(itemDto, owner, null);
        return ItemMapper.toItemDto(itemService.updateItem(item));
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                   @PathVariable("itemId") Long itemId) {
        Item item = itemService.getItem(itemId);
        if (item.getOwner().getId().equals(userId)) {
            return ItemMapper.toItemResponseDto(item,
                    bookingService.getLastBookingByItem(item),
                    bookingService.getNextBookingByItem(item));
        } else {
            return ItemMapper.toItemResponseDto(item,
                    Optional.empty(),
                    Optional.empty());
        }
    }

    @GetMapping
    public List<ItemResponseDto> getMyItems(@RequestHeader(USER_ID_HEADER) Long userId,
                                            @PositiveOrZero
                                            @RequestParam(name = "from", defaultValue = "0") int from,
                                            @Positive
                                            @RequestParam(name = "size", defaultValue = "5") int size) {
        return itemService.getAllByUserId(userId, from, size).stream()
                .map(item -> ItemMapper.toItemResponseDto(item,
                        bookingService.getLastBookingByItem(item),
                        bookingService.getNextBookingByItem(item)))
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(USER_ID_HEADER) Long userId,
                                @RequestParam(name = "text") String text,
                                @PositiveOrZero
                                @RequestParam(name = "from", defaultValue = "0") int from,
                                @Positive
                                @RequestParam(name = "size", defaultValue = "5") int size) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemService.getAllByTemplate(text, from, size).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto saveComment(@PathVariable("itemId") Long itemId,
                                          @Valid @RequestBody CommentCreateDto commentCreateDto,
                                          @RequestHeader(USER_ID_HEADER) Long userId) {
        User author = userService.getUser(userId);
        Item item = itemService.getItem(itemId);
        Comment comment = ItemMapper.toComment(commentCreateDto, author, item);
        return ItemMapper.toCommentResponseDto(itemService.saveComment(comment));
    }

    private void validate(ItemDto itemDto) {
        if (itemDto.getName() != null && itemDto.getName().isBlank()) {
            throw new CustomValidationException("Invalid field 'name' for ItemDto");
        }
        if (itemDto.getDescription() != null && itemDto.getDescription().isBlank()) {
            throw new CustomValidationException("Invalid field 'description' for ItemDto");
        }
    }
}
