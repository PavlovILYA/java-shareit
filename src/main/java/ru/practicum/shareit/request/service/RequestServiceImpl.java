package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;

    @Override
    public ItemRequest saveRequest(ItemRequest itemRequest) {
        itemRequest = requestRepository.save(itemRequest);
        log.debug("Saved request: {}", itemRequest);
        return itemRequest;
    }

    @Override
    public List<ItemRequest> getAllByRequester(User requester) {
        List<ItemRequest> requests = requestRepository.findAllByRequesterOrderByCreatedDesc(requester);
        log.debug("Requests by userId={}: {}", requester.getId(), requests);
        return requests;
    }

    @Override
    public List<ItemRequest> getAllAlien(User requester, int from, int size) {
        Pageable pageRequest = PageRequest.of(from / size, size);
        List<ItemRequest> requests = requestRepository.findAllAlien(requester.getId(), pageRequest).getContent();
        log.debug("Requests for userId={}: {}", requester.getId(), requests);
        return requests;
    }

    @Override
    public ItemRequest getRequestById(Long requestId) {
        ItemRequest request = requestRepository.findById(requestId).orElseThrow(() -> {
            throw new RequestNotFoundException(requestId);
        });
        log.debug("Returned request: {}", request);
        return request;
    }
}
