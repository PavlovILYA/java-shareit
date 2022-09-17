package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.exception.UnavailableItemException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.common.TestObjectMaker.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    private ItemService itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(itemRepository, commentRepository, bookingRepository);
    }

    @Test
    public void checkSaveItem() {
        Item itemBeforeSave = makeItem(null, "item1", "description1", true, null, null, null);
        Item itemAfterSave = makeItem(1L, "item1", "description1", true, null, null, null);
        when(itemRepository.save(itemBeforeSave)).thenReturn(itemAfterSave);

        Item savedItem = itemService.saveItem(itemBeforeSave);

        assertEquals(itemAfterSave, savedItem);
        verify(itemRepository).save(itemBeforeSave);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void checkUpdateItem() {
        User user = makeUser(1L, "Dmitry", "dmitry@ya.ru");
        Item itemBeforeUpdate = makeItem(1L, "item before", "description before", true, user, null, null);
        Item itemAfterUpdate = makeItem(1L, "item after", "description after", true, user, null, null);
        when(itemRepository.findById(itemAfterUpdate.getId())).thenReturn(Optional.of(itemBeforeUpdate));
        when(itemRepository.save(itemAfterUpdate)).thenReturn(itemAfterUpdate);

        Item updatedItem = itemService.updateItem(itemAfterUpdate);

        assertEquals(itemAfterUpdate, updatedItem);
        verify(itemRepository).findById(itemAfterUpdate.getId());
        verify(itemRepository).save(itemAfterUpdate);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void checkUpdateItem_itemNoFoundException() {
        Item item = makeItem(1L, "item", "description", true, null, null, null);
        when(itemRepository.findById(any())).thenReturn(Optional.empty());

        final var thrown = assertThrows(ItemNotFoundException.class, () -> itemService.updateItem(item));
        assertEquals("Item " + item.getId() + " not found", thrown.getMessage());

        verify(itemRepository).findById(item.getId());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void checkGetItem() {
        Item item = makeItem(1L, "item", "description", true, null, null, null);
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));

        Item returnedItem = itemService.getItem(item.getId());
        assertEquals(item, returnedItem);

        verify(itemRepository).findById(item.getId());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void checkGetAllByUserId() {
        // IT ??
    }

    @Test
    public void checkGetAllByTemplate() {
        // IT
    }

    @Test
    public void checkSaveComment() {
        User booker = makeUser(1L, "booker", "user@ya.ru");
        Item item = makeItem(1L, "item", "description", true, null, null, null);
        Comment commentBeforeSave = makeComment(null, "comment", item, booker, LocalDate.now());
        Comment commentAfterSave = makeComment(1L, "comment", item, booker, LocalDate.now());
        Booking booking = makeBooking(1L, LocalDateTime.of(2022, 10, 10, 12, 12, 12), LocalDateTime.of(2022, 10, 10, 12, 12, 12), BookingStatus.APPROVED, item, booker);
        when(bookingRepository.findAllPastByBooker(booker)).thenReturn(List.of(booking));
        when(commentRepository.save(commentBeforeSave)).thenReturn(commentAfterSave);

        Comment savedComment = itemService.saveComment(commentBeforeSave);
        assertEquals(commentAfterSave, savedComment);

        verify(bookingRepository).findAllPastByBooker(booker);
        verify(commentRepository).save(commentBeforeSave);
        verifyNoMoreInteractions(bookingRepository);
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    public void checkSaveComment_authorWasNotBooker() {
        User booker = makeUser(1L, "booker", "user@ya.ru");
        Item item = makeItem(1L, "item", "description", true, null, null, null);
        Comment commentBeforeSave = makeComment(null, "comment", item, booker, LocalDate.now());
        when(bookingRepository.findAllPastByBooker(booker)).thenReturn(Collections.emptyList());

        final var thrown = assertThrows(UnavailableItemException.class, () -> itemService.saveComment(commentBeforeSave));
        assertEquals("Item " + item.getId() + " is unavailable now", thrown.getMessage());

        verify(bookingRepository).findAllPastByBooker(booker);
        verifyNoMoreInteractions(bookingRepository);
        verifyNoInteractions(commentRepository);
    }
}