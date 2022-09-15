package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

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
        return null;
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAlienRequests(@RequestHeader(USER_ID_HEADER) Long userId,
                                                         @RequestParam("from") Long from,
                                                         @RequestParam("size") Long size) {
        return null;
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getRequestById(@RequestHeader(USER_ID_HEADER) Long userId,
                                                 @PathVariable("requestId") Long requestId) {
        userService.getUser(userId);
        return RequestMapper.toRequestDto(requestService.getRequestById(requestId));
    }
}
