package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.CreateValidationGroup;
import ru.practicum.shareit.CustomValidationException;
import ru.practicum.shareit.UpdateValidationGroup;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private static final String FROM_DEFAULT = "0";
    private static final String SIZE_DEFAULT = "5";

    @PostMapping
    public ResponseEntity<Object> saveItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                           @Validated({CreateValidationGroup.class}) @RequestBody ItemDto itemDto) {
        log.debug("Creating item: userId={}, body: {}", userId, itemDto);
        return itemClient.saveItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @PathVariable("itemId") Long itemId,
                                             @Validated({UpdateValidationGroup.class}) @RequestBody ItemDto itemDto) {
        validate(itemDto);
        log.debug("Updating item {}: userId={}, body: {}", itemId, userId, itemDto);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                          @PathVariable("itemId") Long itemId) {
        log.debug("Get item {}: userId={}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getMyItems(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @PositiveOrZero
                                             @RequestParam(name = "from", defaultValue = FROM_DEFAULT) int from,
                                             @Positive
                                             @RequestParam(name = "size", defaultValue = SIZE_DEFAULT) int size) {
        log.debug("Get items that user {} owns: from={}, size={}", userId, from, size);
        return itemClient.getMyItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(USER_ID_HEADER) Long userId,
                                         @RequestParam(name = "text", defaultValue = "") String text,
                                         @PositiveOrZero
                                         @RequestParam(name = "from", defaultValue = FROM_DEFAULT) int from,
                                         @Positive
                                         @RequestParam(name = "size", defaultValue = SIZE_DEFAULT) int size) {
        log.debug("Get items by template: userId={}, from={}, size={}, text={}", userId, from, size, text);
        if (text == null || text.isBlank()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }
        return itemClient.search(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> saveComment(@PathVariable("itemId") Long itemId,
                                              @Valid @RequestBody CommentCreateDto commentCreateDto,
                                              @RequestHeader(USER_ID_HEADER) Long userId) {
        log.debug("Creating comment: itemId={}, userId={}, body: {}", itemId, userId, commentCreateDto);
        return itemClient.saveComment(itemId, commentCreateDto, userId);
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
