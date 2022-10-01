package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.controller.UserController.ROOT_PATH;

@Slf4j
@RestController
@RequestMapping(path = ROOT_PATH)
@RequiredArgsConstructor
public class UserController {
    public static final String ROOT_PATH = "/users";

    private final UserService userService;

    @PostMapping
    public UserDto saveUser(@RequestBody UserDto userDto) {
        log.debug("POST {} body: {}", ROOT_PATH, userDto);
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userService.saveUser(user));
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable("userId") Long userId,
                              @RequestBody UserDto userDto) {
        log.debug("PATCH {}/{} body: {}", ROOT_PATH, userId, userDto);
        userDto.setId(userId);
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userService.updateUser(user));
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Long userId) {
        log.debug("DELETE {}/{}", ROOT_PATH, userId);
        userService.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable("userId") Long userId) {
        log.debug("GET {}/{}", ROOT_PATH, userId);
        return UserMapper.toUserDto(userService.getUser(userId));
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.debug("GET {}", ROOT_PATH);
        return userService.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
