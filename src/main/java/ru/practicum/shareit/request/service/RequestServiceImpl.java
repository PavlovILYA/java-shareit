package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;

    @Override
    public ItemRequest saveRequest(ItemRequest itemRequest) {
        return requestRepository.save(itemRequest);
    }

    @Override
    public List<ItemRequest> getAllByRequester(User requester) {
        return requestRepository.findAllByRequester(requester);
    }

    @Override
    public List<ItemRequest> getAllAlien(User requester, int from, int size) {
        Pageable pageRequest = PageRequest.of(from / size, size);
        return requestRepository.findAllAlien(requester.getId(), pageRequest).getContent();
    }

    @Override
    public ItemRequest getRequestById(Long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() -> {
            throw new RequestNotFoundException(requestId);
        });
    }
}
