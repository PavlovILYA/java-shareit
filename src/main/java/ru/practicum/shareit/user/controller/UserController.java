package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.CreateValidationGroup;
import ru.practicum.shareit.CustomValidationException;
import ru.practicum.shareit.UpdateValidationGroup;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto saveUser(@Validated({CreateValidationGroup.class}) @RequestBody UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userService.saveUser(user));
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable("userId") Long userId,
                              @Validated({UpdateValidationGroup.class}) @RequestBody UserDto userDto) {
        validate(userDto);
        userDto.setId(userId);
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userService.updateUser(user));
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable("userId") Long userId) {
        return UserMapper.toUserDto(userService.getUser(userId));
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    private void validate(UserDto userDto) {
        if (userDto.getName() != null && userDto.getName().isBlank()) {
            throw new CustomValidationException("Invalid field 'name' for UserDto");
        }
    }
}
