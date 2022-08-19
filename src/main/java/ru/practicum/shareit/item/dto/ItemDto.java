package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.CreateGroup;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotNull(groups = {CreateGroup.class})
    @NotBlank(groups = {CreateGroup.class})
    private String name;
    @NotNull(groups = {CreateGroup.class})
    @NotBlank(groups = {CreateGroup.class})
    private String description;
    @NotNull(groups = {CreateGroup.class})
    private Boolean available;
}
