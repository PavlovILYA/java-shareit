package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.controller.ItemRequestController.ROOT_PATH;

@Slf4j
@Validated
@RestController
@RequestMapping(path = ROOT_PATH)
@RequiredArgsConstructor
public class ItemRequestController {
    public static final String ROOT_PATH = "/requests";
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final RequestService requestService;
    private final UserService userService;

    @PostMapping
    public ItemRequestResponseDto saveRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                              @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        log.debug("POST {} userId={} body: {}", ROOT_PATH, userId, itemRequestCreateDto);
        User register = userService.getUser(userId);
        ItemRequest itemRequest = RequestMapper.toRequest(itemRequestCreateDto, register);
        return RequestMapper.toRequestDto(requestService.saveRequest(itemRequest));
    }

    @GetMapping
    public List<ItemRequestResponseDto> getMyRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.debug("GET {} userId={}", ROOT_PATH, userId);
        User requester = userService.getUser(userId);
        return requestService.getAllByRequester(requester).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAlienRequests(@RequestHeader(USER_ID_HEADER) Long userId,
                                                         @RequestParam(name = "from") int from,
                                                         @RequestParam(name = "size") int size) {
        log.debug("GET {}/all userId={} from={} size={}", ROOT_PATH, userId, from, size);
        User requester = userService.getUser(userId);
        return requestService.getAllAlien(requester, from, size).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getRequestById(@RequestHeader(USER_ID_HEADER) Long userId,
                                                 @PathVariable("requestId") Long requestId) {
        log.debug("GET {}/{} userId={}", ROOT_PATH, requestId, userId);
        userService.getUser(userId);
        return RequestMapper.toRequestDto(requestService.getRequestById(requestId));
    }
}
