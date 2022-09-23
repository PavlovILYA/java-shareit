package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private static final String FROM_DEFAULT = "0";
    private static final String SIZE_DEFAULT = "5";

    private final RequestService requestService;
    private final UserService userService;

    @PostMapping
    public ItemRequestResponseDto saveRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                              @Valid @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        User register = userService.getUser(userId);
        ItemRequest itemRequest = RequestMapper.toRequest(itemRequestCreateDto, register);
        return RequestMapper.toRequestDto(requestService.saveRequest(itemRequest));
    }

    @GetMapping
    public List<ItemRequestResponseDto> getMyRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        User requester = userService.getUser(userId);
        return requestService.getAllByRequester(requester).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAlienRequests(@RequestHeader(USER_ID_HEADER) Long userId,
                                                         @PositiveOrZero
                                                         @RequestParam(name = "from", defaultValue = FROM_DEFAULT)
                                                         int from,
                                                         @Positive
                                                         @RequestParam(name = "size", defaultValue = SIZE_DEFAULT)
                                                         int size) {
        User requester = userService.getUser(userId);
        return requestService.getAllAlien(requester, from, size).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getRequestById(@RequestHeader(USER_ID_HEADER) Long userId,
                                                 @PathVariable("requestId") Long requestId) {
        userService.getUser(userId);
        return RequestMapper.toRequestDto(requestService.getRequestById(requestId));
    }
}
