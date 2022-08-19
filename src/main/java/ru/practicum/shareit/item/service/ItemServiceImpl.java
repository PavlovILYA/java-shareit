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
    public ItemDto saveItem(ItemDto itemDto, Long userId) {
        User owner = userRepository.get(userId);
        Item item = ItemMapper.toItem(itemDto, owner);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        User owner = userRepository.get(userId);
        itemDto.setId(itemId);
        Item item = ItemMapper.toItem(itemDto, owner);
        return ItemMapper.toItemDto(itemRepository.update(item));
    }

    @Override
    public ItemDto getItem(Long itemId) {
        return ItemMapper.toItemDto(itemRepository.get(itemId));
    }

    @Override
    public List<ItemDto> getAllByUserId(Long userId) {
        return itemRepository.getAllByUserId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
