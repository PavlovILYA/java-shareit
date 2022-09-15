package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.CreateValidationGroup;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotNull(groups = {CreateValidationGroup.class})
    @NotBlank(groups = {CreateValidationGroup.class})
    private String name;
    @NotNull(groups = {CreateValidationGroup.class})
    @NotBlank(groups = {CreateValidationGroup.class})
    private String description;
    @NotNull(groups = {CreateValidationGroup.class})
    private Boolean available;
    private Long requestId;
}
