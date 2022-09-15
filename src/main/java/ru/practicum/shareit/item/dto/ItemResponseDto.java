package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemResponseDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentResponseDto> comments;
    private Long requestId;

    @Data
    @AllArgsConstructor
    public static class BookingDto {
        private Long id;
        private LocalDateTime start;
        private LocalDateTime end;
        private Long bookerId;
    }
}
