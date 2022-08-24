package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item saveItem(Item item);

    Item updateItem(Item item);

    Item getItem(Long itemId);

    List<Item> getAllByUserId(Long userId);

    List<Item> getAllByTemplate(String template);
}
