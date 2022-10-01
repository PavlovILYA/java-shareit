package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.CreateValidationGroup;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
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
