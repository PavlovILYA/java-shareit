package ru.practicum.shareit.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.validation.group.CreateValidationGroup;
import ru.practicum.shareit.CustomValidationException;
import ru.practicum.shareit.validation.group.UpdateValidationGroup;
import ru.practicum.shareit.user.dto.UserDto;

import static ru.practicum.shareit.Constants.USER_API_PREFIX;

@Slf4j
@RestController
@RequestMapping(USER_API_PREFIX)
@RequiredArgsConstructor
@Tag(name = "User-контроллер", description = "Позволяет управлять пользователями")
public class UserController {
    private final UserClient userClient;

    @Operation(summary = "Создание пользователя")
    @PostMapping
    public ResponseEntity<Object> saveUser(@Validated({CreateValidationGroup.class}) @RequestBody UserDto userDto) {
        log.debug("Creating user: {}", userDto);
        return userClient.saveUser(userDto);
    }

    @Operation(summary = "Обновление пользователя")
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable("userId") Long userId,
                                             @Validated({UpdateValidationGroup.class}) @RequestBody UserDto userDto) {
        validate(userDto);
        userDto.setId(userId);
        log.debug("Updating user {}: {}", userId, userDto);
        return userClient.updateUser(userId, userDto);
    }

    @Operation(summary = "Удаление пользователя")
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Long userId) {
        log.debug("Deleting user {}", userId);
        userClient.deleteUser(userId);
    }

    @Operation(summary = "Получение пользователя")
    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable("userId") Long userId) {
        log.debug("Get user {}", userId);
        return userClient.getUser(userId);
    }

    @Operation(summary = "Получение всех пользователей")
    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.debug("Get all users");
        return userClient.getAll();
    }

    private void validate(UserDto userDto) {
        if (userDto.getName() != null && userDto.getName().isBlank()) {
            throw new CustomValidationException("Invalid field 'name' for UserDto");
        }
    }
}