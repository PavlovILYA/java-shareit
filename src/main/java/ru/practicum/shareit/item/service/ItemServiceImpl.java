package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

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
