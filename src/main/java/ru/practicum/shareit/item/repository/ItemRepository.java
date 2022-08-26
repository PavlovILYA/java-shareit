package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item save(Item item);

    Item update(Item item);

    Item get(Long id);

    List<Item> getAllByUserId(Long userId);

    List<Item> getAllByTemplate(String template);
}
