package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.group.CreateValidationGroup;
import ru.practicum.shareit.validation.group.UpdateValidationGroup;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserDto {
    private Long id;
    @NotNull(groups = {CreateValidationGroup.class})
    @NotBlank(groups = {CreateValidationGroup.class})
    private String name;
    @NotNull(groups = {CreateValidationGroup.class})
    @Email(groups = {CreateValidationGroup.class, UpdateValidationGroup.class})
    private String email;
}
