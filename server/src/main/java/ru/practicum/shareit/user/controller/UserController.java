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

import static ru.practicum.shareit.Constants.USER_API_PREFIX;

@Slf4j
@RestController
@RequestMapping(path = USER_API_PREFIX)
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto saveUser(@RequestBody UserDto userDto) {
        log.debug("POST {} body: {}", USER_API_PREFIX, userDto);
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userService.saveUser(user));
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable("userId") Long userId,
                              @RequestBody UserDto userDto) {
        log.debug("PATCH {}/{} body: {}", USER_API_PREFIX, userId, userDto);
        userDto.setId(userId);
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userService.updateUser(user));
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Long userId) {
        log.debug("DELETE {}/{}", USER_API_PREFIX, userId);
        userService.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable("userId") Long userId) {
        log.debug("GET {}/{}", USER_API_PREFIX, userId);
        return UserMapper.toUserDto(userService.getUser(userId));
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.debug("GET {}", USER_API_PREFIX);
        return userService.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
