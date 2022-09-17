package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.common.TestObjectMaker.makeItemRequest;
import static ru.practicum.shareit.common.TestObjectMaker.makeUser;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {
    private RequestService requestService;
    @Mock
    private RequestRepository requestRepository;

    @BeforeEach
    void setUp() {
        requestService = new RequestServiceImpl(requestRepository);
    }

    @Test
    public void checkSaveRequest() {
        User requester = makeUser(1L, "Olya", "olya@ya.ru");
        ItemRequest itemRequestBeforeSave = makeItemRequest(null, "description", LocalDateTime.of(2022, 10, 10, 10, 10, 10), requester, null);
        ItemRequest itemRequestAfterSave = makeItemRequest(1L, "description", LocalDateTime.of(2022, 10, 10, 10, 10, 10), requester, null);
        when(requestRepository.save(itemRequestBeforeSave)).thenReturn(itemRequestAfterSave);

        ItemRequest savedRequest = requestService.saveRequest(itemRequestBeforeSave);
        assertEquals(itemRequestAfterSave, savedRequest);

        verify(requestRepository).save(itemRequestBeforeSave);
        verifyNoMoreInteractions(requestRepository);
    }

    @Test
    public void checkGetAllByRequester() {
        User requester = makeUser(1L, "Olya", "olya@ya.ru");
        ItemRequest itemRequest = makeItemRequest(1L, "description", LocalDateTime.of(2022, 10, 10, 10, 10, 10), requester, null);
        when(requestRepository.findAllByRequesterOrderByCreatedDesc(requester)).thenReturn(List.of(itemRequest));

        List<ItemRequest> requests = requestService.getAllByRequester(requester);
        assertEquals(1, requests.size());
        assertEquals(itemRequest, requests.get(0));

        verify(requestRepository).findAllByRequesterOrderByCreatedDesc(requester);
        verifyNoMoreInteractions(requestRepository);
    }

    @Test
    public void checkGetAllAlien() {
        // IT
    }

    @Test
    public void checkGetRequestById() {
        User requester = makeUser(1L, "Olya", "olya@ya.ru");
        ItemRequest itemRequest = makeItemRequest(1L, "description", LocalDateTime.of(2022, 10, 10, 10, 10, 10), requester, null);
        when(requestRepository.findById(requester.getId())).thenReturn(Optional.of(itemRequest));

        ItemRequest requestFromDb = requestService.getRequestById(requester.getId());
        assertEquals(itemRequest, requestFromDb);

        verify(requestRepository).findById(requester.getId());
        verifyNoMoreInteractions(requestRepository);
    }

    @Test
    public void checkGetRequestById_requestNotFoundException() {
        when(requestRepository.findById(any())).thenReturn(Optional.empty());

        final var thrown = assertThrows(RequestNotFoundException.class, () -> requestService.getRequestById(1L));
        assertEquals("Item request " + 1L + " not found", thrown.getMessage());

        verify(requestRepository).findById(any());
        verifyNoMoreInteractions(requestRepository);
    }
}