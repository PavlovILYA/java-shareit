package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class ItemRequestResponseDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private UserDto requester;
    private List<ItemResponseDto> items;
}
