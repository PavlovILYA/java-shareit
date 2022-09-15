package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface RequestService {
    ItemRequest saveRequest(ItemRequest itemRequest);

    List<ItemRequest> getAllByRequester(User requester);

    ItemRequest getRequestById(Long requestId);
}
