package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
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

import static ru.practicum.shareit.Constants.ITEM_API_PREFIX;
import static ru.practicum.shareit.Constants.USER_ID_HEADER;

@Slf4j
@RestController
@RequestMapping(ITEM_API_PREFIX)
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final RequestService requestService;

    @PostMapping
    public ItemDto saveItem(@RequestHeader(USER_ID_HEADER) Long userId,
                            @RequestBody ItemDto itemDto) {
        log.debug("POST {} userId={} body: {}", ITEM_API_PREFIX, userId, itemDto);
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
        log.debug("PATCH {}/{} userId={} body: {}", ITEM_API_PREFIX, itemId, userId, itemDto);
        itemDto.setId(itemId);
        User owner = userService.getUser(userId);
        Item item = ItemMapper.toItem(itemDto, owner, null);
        return ItemMapper.toItemDto(itemService.updateItem(item));
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                   @PathVariable("itemId") Long itemId) {
        log.debug("GET {}/{} userId={}", ITEM_API_PREFIX, itemId, userId);
        Item item = itemService.getItem(itemId);
        return ItemMapper.toItemResponseDto(item,
                item.getOwner().getId().equals(userId) ? bookingService.getLastBookingByItem(item) : null,
                item.getOwner().getId().equals(userId) ? bookingService.getNextBookingByItem(item) : null);
    }

    @GetMapping
    public List<ItemResponseDto> getMyItems(@RequestHeader(USER_ID_HEADER) Long userId,
                                            @RequestParam(name = "from") int from,
                                            @RequestParam(name = "size") int size) {
        log.debug("GET {} userId={} from={} size={}", ITEM_API_PREFIX, userId, from, size);
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
        log.debug("GET {}/search userId={} from={} size={} text={}", ITEM_API_PREFIX, userId, from, size, text);
        return itemService.getAllByTemplate(text, from, size).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto saveComment(@PathVariable("itemId") Long itemId,
                                          @RequestBody CommentCreateDto commentCreateDto,
                                          @RequestHeader(USER_ID_HEADER) Long userId) {
        log.debug("POST {}/{}/comment userId={} body: {}", ITEM_API_PREFIX, itemId, userId, commentCreateDto);
        User author = userService.getUser(userId);
        Item item = itemService.getItem(itemId);
        Comment comment = ItemMapper.toComment(commentCreateDto, author, item);
        return ItemMapper.toCommentResponseDto(itemService.saveComment(comment));
    }
}
