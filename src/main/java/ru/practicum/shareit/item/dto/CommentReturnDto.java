package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CommentReturnDto {
    private Long id;
    private String text;
    private String itemName;
    private String authorName;
    private LocalDate created;
}
