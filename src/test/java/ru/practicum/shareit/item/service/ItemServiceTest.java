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
import ru.practicum.shareit.item.exception.InvalidOwnerException;
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

    private Item item;
    private Item item2;

    private Item itemWithoutId;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(itemRepository, commentRepository, bookingRepository);
        User user = makeUser(1L, "Dmitry", "dmitry@ya.ru");
        itemWithoutId = makeItem(null, "item1", "description1", true,
                user, null, null);
        item = makeItem(1L, "item1", "description1", true,
                user, null, null);
        item2 = makeItem(1L, "item1", "description1", true,
                user, null, null);
    }

    @Test
    public void checkSaveItem() {
        when(itemRepository.save(itemWithoutId)).thenReturn(item);

        Item savedItem = itemService.saveItem(itemWithoutId);

        assertEquals(item, savedItem);
        verify(itemRepository).save(itemWithoutId);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void checkUpdateItem() {
        item2.setName("updated");
        item2.setDescription("updated");
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(itemRepository.save(item2)).thenReturn(item2);

        Item updatedItem = itemService.updateItem(item2);

        assertEquals(item2, updatedItem);
        verify(itemRepository).findById(item.getId());
        verify(itemRepository).save(item2);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void checkUpdateItem_itemNoFoundException() {
        when(itemRepository.findById(any())).thenReturn(Optional.empty());

        final var thrown = assertThrows(ItemNotFoundException.class, () -> itemService.updateItem(item));
        assertEquals("Item " + item.getId() + " not found", thrown.getMessage());

        verify(itemRepository).findById(item.getId());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void checkUpdateItem_invalidOwnerException() {
        User user2 = makeUser(2L, "Oleg", "oleg@ya.ru");
        item2.setName("updated");
        item2.setOwner(user2);
        when(itemRepository.findById(item2.getId())).thenReturn(Optional.of(item));

        final var thrown = assertThrows(InvalidOwnerException.class, () -> itemService.updateItem(item2));
        assertEquals("Correct owner is " + item.getOwner() +
                " but there is " + item2.getOwner(), thrown.getMessage());
    }

    @Test
    public void checkGetItem() {
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
        Comment commentBeforeSave = makeComment(null, "comment", item, booker, LocalDate.now());
        Comment commentAfterSave = makeComment(1L, "comment", item, booker, LocalDate.now());
        Booking booking = makeBooking(1L, LocalDateTime.of(2022, 10, 10, 12, 12, 12),
                LocalDateTime.of(2022, 10, 10, 12, 12, 12),
                BookingStatus.APPROVED, item, booker);
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
        Comment commentBeforeSave = makeComment(null, "comment", item, booker, LocalDate.now());
        when(bookingRepository.findAllPastByBooker(booker)).thenReturn(Collections.emptyList());

        final var thrown = assertThrows(UnavailableItemException.class,
                () -> itemService.saveComment(commentBeforeSave));
        assertEquals("Item " + item.getId() + " is unavailable now", thrown.getMessage());

        verify(bookingRepository).findAllPastByBooker(booker);
        verifyNoMoreInteractions(bookingRepository);
        verifyNoInteractions(commentRepository);
    }
}