package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.CreateGroup;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto saveUser(@Validated({CreateGroup.class}) @RequestBody UserDto userDto) {
        return userService.saveUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable("userId") Long userId,
                              @RequestBody UserDto userDto) {
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable("userId") Long userId) {
        return userService.getUser(userId);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll();
    }
}
