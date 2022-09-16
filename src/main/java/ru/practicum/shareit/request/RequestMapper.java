package ru.practicum.shareit.request;

import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RequestMapper {
    public static ItemRequest toRequest(ItemRequestCreateDto itemRequestCreateDto, User register) {
        return ItemRequest.builder()
                .description(itemRequestCreateDto.getDescription())
                .created(LocalDateTime.now())
                .requester(register)
                .build();
    }

    public static ItemRequestResponseDto toRequestDto(ItemRequest itemRequest) {
        List<ItemResponseDto> items = null;
        if (itemRequest.getItems() != null) {
            items = itemRequest.getItems().stream()
                    .map(item -> ItemMapper.toItemResponseDto(item, Optional.empty(), Optional.empty()))
                    .collect(Collectors.toList());
        }
        return ItemRequestResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requester(UserMapper.toUserDto(itemRequest.getRequester()))
                .items(items)
                .build();
    }
}
