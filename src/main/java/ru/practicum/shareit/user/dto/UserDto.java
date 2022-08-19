package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.CreateGroup;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotNull(groups = {CreateGroup.class})
    @NotBlank(groups = {CreateGroup.class})
    private String name;
    @Email(groups = {CreateGroup.class})
    @NotNull(groups = {CreateGroup.class})
    private String email;
}
