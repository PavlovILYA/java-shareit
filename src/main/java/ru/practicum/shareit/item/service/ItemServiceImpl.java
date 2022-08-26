package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Item item) {
        return itemRepository.update(item);
    }

    @Override
    public Item getItem(Long itemId) {
        return itemRepository.get(itemId);
    }

    @Override
    public List<Item> getAllByUserId(Long userId) {
        return itemRepository.getAllByUserId(userId);
    }

    @Override
    public List<Item> getAllByTemplate(String template) {
        return itemRepository.getAllByTemplate(template.toLowerCase());
    }
}
