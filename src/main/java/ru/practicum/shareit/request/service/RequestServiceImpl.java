package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;

    @Override
    public ItemRequest saveRequest(ItemRequest itemRequest) {
        return requestRepository.save(itemRequest);
    }

    @Override
    public ItemRequest getRequestById(Long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() -> {
            throw new RequestNotFoundException(requestId);
        });
    }
}
