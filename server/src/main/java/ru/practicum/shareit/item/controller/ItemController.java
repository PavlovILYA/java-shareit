package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.CommentCreateDto;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.controller.ItemController.ROOT_PATH;

@Slf4j
@Validated
@RestController
@RequestMapping(ROOT_PATH)
@RequiredArgsConstructor
public class ItemController {
    public static final String ROOT_PATH = "/items";
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final RequestService requestService;

    @PostMapping
    public ItemDto saveItem(@RequestHeader(USER_ID_HEADER) Long userId,
                            @RequestBody ItemDto itemDto) {
        log.debug("POST {} userId={} body: {}", ROOT_PATH, userId, itemDto);
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
                              @RequestBody ItemDto itemDto) {
        log.debug("PATCH {}/{} userId={} body: {}", ROOT_PATH, itemId, userId, itemDto);
        itemDto.setId(itemId);
        User owner = userService.getUser(userId);
        Item item = ItemMapper.toItem(itemDto, owner, null);
        return ItemMapper.toItemDto(itemService.updateItem(item));
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                   @PathVariable("itemId") Long itemId) {
        log.debug("GET {}/{} userId={}", ROOT_PATH, itemId, userId);
        Item item = itemService.getItem(itemId);
        return ItemMapper.toItemResponseDto(item,
                item.getOwner().getId().equals(userId) ? bookingService.getLastBookingByItem(item) : null,
                item.getOwner().getId().equals(userId) ? bookingService.getNextBookingByItem(item) : null);
    }

    @GetMapping
    public List<ItemResponseDto> getMyItems(@RequestHeader(USER_ID_HEADER) Long userId,
                                            @RequestParam(name = "from") int from,
                                            @RequestParam(name = "size") int size) {
        log.debug("GET {} userId={} from={} size={}", ROOT_PATH, userId, from, size);
        return itemService.getAllByUserId(userId, from, size).stream()
                .map(item -> ItemMapper.toItemResponseDto(item,
                        bookingService.getLastBookingByItem(item),
                        bookingService.getNextBookingByItem(item)))
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(USER_ID_HEADER) Long userId,
                                @RequestParam(name = "text") String text,
                                @RequestParam(name = "from") int from,
                                @RequestParam(name = "size") int size) {
        log.debug("GET {}/search userId={} from={} size={} text={}", ROOT_PATH, userId, from, size, text);
        return itemService.getAllByTemplate(text, from, size).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto saveComment(@PathVariable("itemId") Long itemId,
                                          @RequestBody CommentCreateDto commentCreateDto,
                                          @RequestHeader(USER_ID_HEADER) Long userId) {
        log.debug("POST {}/{}/comment userId={} body: {}", ROOT_PATH, itemId, userId, commentCreateDto);
        User author = userService.getUser(userId);
        Item item = itemService.getItem(itemId);
        Comment comment = ItemMapper.toComment(commentCreateDto, author, item);
        return ItemMapper.toCommentResponseDto(itemService.saveComment(comment));
    }
}
