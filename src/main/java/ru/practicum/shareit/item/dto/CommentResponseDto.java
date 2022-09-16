package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class CommentResponseDto {
    private Long id;
    private String text;
    private String itemName;
    private String authorName;
    private LocalDate created;
}
