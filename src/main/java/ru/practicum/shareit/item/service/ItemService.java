package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item saveItem(Item item);

    Item updateItem(Item item);

    Item getItem(Long itemId);

    List<Item> getAllByUserId(Long userId, int from, int size);

    List<Item> getAllByTemplate(String template, int from, int size);

    Comment saveComment(Comment comment);
}
