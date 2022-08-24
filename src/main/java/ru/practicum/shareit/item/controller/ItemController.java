package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.CreateValidationGroup;
import ru.practicum.shareit.CustomValidationException;
import ru.practicum.shareit.UpdateValidationGroup;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;

    @PostMapping
    public ItemDto saveItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @Validated({CreateValidationGroup.class}) @RequestBody ItemDto itemDto) {
        User owner = userService.getUser(userId);
        Item item = ItemMapper.toItem(itemDto, owner);
        return ItemMapper.toItemDto(itemService.saveItem(item));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable("itemId") Long itemId,
                              @Validated({UpdateValidationGroup.class}) @RequestBody ItemDto itemDto) {
        if (itemDto.getName() != null && itemDto.getName().isBlank()
                || itemDto.getDescription() != null && itemDto.getDescription().isBlank()) {
            throw new CustomValidationException("Wrong itemDto fields during updating");
        }
        itemDto.setId(itemId);
        User owner = userService.getUser(userId);
        Item item = ItemMapper.toItem(itemDto, owner);
        return ItemMapper.toItemDto(itemService.updateItem(item));
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable("itemId") Long itemId) {
        return ItemMapper.toItemDto(itemService.getItem(itemId));
    }

    @GetMapping
    public List<ItemDto> getMyItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllByUserId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                                @RequestParam(name = "text") String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemService.getAllByTemplate(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
