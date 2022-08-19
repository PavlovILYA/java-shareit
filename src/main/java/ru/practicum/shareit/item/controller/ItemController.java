package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.CreateGroup;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto saveItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @Validated({CreateGroup.class}) @RequestBody ItemDto itemDto) {
        return itemService.saveItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable("itemId") Long itemId,
                              @RequestBody ItemDto itemDto) {
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable("itemId") Long itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping
    public List<ItemDto> getMyItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                                @RequestParam(name = "text") String text) {
        return null;
    }
}
