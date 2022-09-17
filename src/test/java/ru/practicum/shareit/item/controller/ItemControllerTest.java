package ru.practicum.shareit.item.controller;

import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.common.LocalDateAdapter;
import ru.practicum.shareit.common.LocalDateTimeAdapter;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.common.TestObjectMaker.*;
import static ru.practicum.shareit.common.TestObjectMaker.makeBookingResponseDto;

@Slf4j
@WebMvcTest(ItemController.class)
class ItemControllerTest {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;
    @MockBean
    private UserService userService;
    @MockBean
    private BookingService bookingService;
    @MockBean
    private RequestService requestService;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    @Test
    public void checkSaveItem_withoutRequest() throws Exception {
        User user = makeUser(2L, "Igor", "igor@ya.ru");
        when(userService.getUser(2L)).thenReturn(user);

        Item itemBeforeSave = makeItem(null, "item1", "description1", true, user, null, null);
        Item itemAfterSave = makeItem(1L, "item1", "description1", true, user, null, null);
        when(itemService.saveItem(itemBeforeSave)).thenReturn(itemAfterSave);

        ItemDto itemDtoBeforeSave = makeItemDto(null, "item1", "description1", true, null);
        ItemDto itemDtoAfterSave = makeItemDto(1L, "item1", "description1", true, null);

        mockMvc.perform(post("/items")
                        .content(gson.toJson(itemDtoBeforeSave))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, 2L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json((gson.toJson(itemDtoAfterSave))));

        verify(userService).getUser(2L);
        verify(itemService).saveItem(itemBeforeSave);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(itemService);
        verifyNoInteractions(requestService);
    }

    @Test
    public void checkSaveItem_withRequest() throws Exception {
        User user = makeUser(2L, "Igor", "igor@ya.ru");
        User requester = makeUser(3L, "Nick", "nick@ya.ru");
        when(userService.getUser(2L)).thenReturn(user);

        ItemRequest itemRequest = makeRequest(3L, "some request", LocalDateTime.of(2022, 9, 11, 10, 10, 10), requester, null);
        when(requestService.getRequestById(3L)).thenReturn(itemRequest);

        Item itemBeforeSave = makeItem(null, "item1", "description1", true, user, null, itemRequest);
        Item itemAfterSave = makeItem(1L, "item1", "description1", true, user, null, itemRequest);
        when(itemService.saveItem(itemBeforeSave)).thenReturn(itemAfterSave);

        ItemDto itemDtoBeforeSave = makeItemDto(null, "item1", "description1", true, 3L);
        ItemDto itemDtoAfterSave = makeItemDto(1L, "item1", "description1", true, 3L);

        mockMvc.perform(post("/items")
                        .content(gson.toJson(itemDtoBeforeSave))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, 2L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json((gson.toJson(itemDtoAfterSave))));

        verify(userService).getUser(2L);
        verify(itemService).saveItem(itemBeforeSave);
        verify(requestService).getRequestById(3L);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(itemService);
        verifyNoMoreInteractions(requestService);
    }

    @Test
    public void checkUpdateItem() throws Exception {
        User user = makeUser(2L, "Igor", "igor@ya.ru");
        when(userService.getUser(2L)).thenReturn(user);

        Item itemAfterUpdate = makeItem(1L, "item1", "description1", true, user, null, null);
        when(itemService.updateItem(itemAfterUpdate)).thenReturn(itemAfterUpdate);

        ItemDto itemDtoBeforeSave = makeItemDto(null, "item1", "description1", true, null);
        ItemDto itemDtoAfterSave = makeItemDto(1L, "item1", "description1", true, null);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, 2L)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(itemDtoBeforeSave)))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(itemDtoAfterSave)));

        verify(userService).getUser(2L);
        verify(itemService).updateItem(itemAfterUpdate);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(itemService);
    }

    @Test
    public void checkUpdateItem_validException() throws Exception {
        ItemDto itemDtoBeforeSave = makeItemDto(null, "", "description1", true, null);
        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, 2L)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(itemDtoBeforeSave)))
                .andExpect(status().isBadRequest());

        itemDtoBeforeSave = makeItemDto(null, "item1", "", true, null);
        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, 2L)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(itemDtoBeforeSave)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
        verifyNoInteractions(itemService);
    }

    @Test
    public void checkGetItem() throws Exception {
        User user = makeUser(2L, "Igor", "igor@ya.ru");
        Item item = makeItem(1L, "item1", "description1", true, user, Collections.emptyList(), null);
        when(itemService.getItem(1L)).thenReturn(item);

        ItemResponseDto itemDto = makeItemResponseDto(1L, "item1", "description1", true, null, null, null, Collections.emptyList());

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, 3L))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(itemDto)));

        verify(itemService).getItem(1L);
        verifyNoMoreInteractions(itemService);
        verifyNoInteractions(bookingService);
    }

    @Test
    public void checkGetItem_withBookings() throws Exception {
        User user = makeUser(2L, "Igor", "igor@ya.ru");
        Item item = makeItem(1L, "item1", "description1", true, user, Collections.emptyList(), null);
        Booking lastBooking = makeBooking(4L, LocalDateTime.of(2022, 9, 11, 10, 10, 10), LocalDateTime.of(2022, 9, 11, 20, 10, 10), BookingStatus.APPROVED, item, user);
        Booking nextBooking = makeBooking(4L, LocalDateTime.of(2022, 10, 11, 10, 10, 10), LocalDateTime.of(2022, 10, 11, 20, 10, 10), BookingStatus.WAITING, item, user);
        when(itemService.getItem(1L)).thenReturn(item);
        when(bookingService.getLastBookingByItem(item)).thenReturn(lastBooking);
        when(bookingService.getNextBookingByItem(item)).thenReturn(nextBooking);

        BookingResponseDto lastBookingDto = makeBookingResponseDto(4L, LocalDateTime.of(2022, 9, 11, 10, 10, 10), LocalDateTime.of(2022, 9, 11, 20, 10, 10), BookingStatus.APPROVED, item, user);
        BookingResponseDto nextBookingDto = makeBookingResponseDto(4L, LocalDateTime.of(2022, 10, 11, 10, 10, 10), LocalDateTime.of(2022, 10, 11, 20, 10, 10), BookingStatus.WAITING, item, user);
        ItemResponseDto itemDto = makeItemResponseDto(1L, "item1", "description1", true, null, lastBookingDto, nextBookingDto, Collections.emptyList());

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, 2L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(itemDto)));

        verify(itemService).getItem(1L);
        verify(bookingService).getLastBookingByItem(item);
        verify(bookingService).getNextBookingByItem(item);
        verifyNoMoreInteractions(itemService);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    public void checkGetMyItems() throws Exception {
        User user = makeUser(1L, "Igor", "igor@ya.ru");
        Item item1 = makeItem(1L, "item1", "description1", true, user, Collections.emptyList(), null);
        Item item2 = makeItem(2L, "item2", "description2", false, user, Collections.emptyList(), null);
        when(itemService.getAllByUserId(1L, 0, 2)).thenReturn(List.of(item1, item2));
        when(bookingService.getLastBookingByItem(any())).thenReturn(null);
        when(bookingService.getNextBookingByItem(any())).thenReturn(null);

        ItemResponseDto itemResponseDto1 = makeItemResponseDto(1L, "item1", "description1", true, null, null, null, Collections.emptyList());
        ItemResponseDto itemResponseDto2 = makeItemResponseDto(2L, "item2", "description2", false, null, null, null, Collections.emptyList());
        mockMvc.perform(get("/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, 1L)
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(List.of(itemResponseDto1, itemResponseDto2))));

        verify(itemService).getAllByUserId(1L, 0, 2);
        verify(bookingService, times(2)).getLastBookingByItem(any());
        verify(bookingService, times(2)).getNextBookingByItem(any());
        verifyNoMoreInteractions(itemService);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    public void checkSearch_emptyList() throws Exception {
        mockMvc.perform(get("/items/search")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, 2L)
                        .param("text", "")
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(Collections.emptyList())));

        verifyNoInteractions(itemService);
        verifyNoInteractions(bookingService);
    }

    @Test
    public void checkSearch() throws Exception {
        User user = makeUser(1L, "Igor", "igor@ya.ru");
        Item item1 = makeItem(1L, "item1", "description1", true, user, Collections.emptyList(), null);
        Item item2 = makeItem(2L, "item2", "description2", false, user, Collections.emptyList(), null);
        String template = "item";
        when(itemService.getAllByTemplate(template, 0, 2)).thenReturn(List.of(item1, item2));

        ItemDto itemDtoBeforeSave = makeItemDto(1L, "item1", "description1", true, null);
        ItemDto itemDtoAfterSave = makeItemDto(2L, "item2", "description2", false, null);
        mockMvc.perform(get("/items/search")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, 1L)
                        .param("text", template)
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(List.of(itemDtoBeforeSave, itemDtoAfterSave))));

        verify(itemService).getAllByTemplate(template, 0, 2);
        verifyNoMoreInteractions(itemService);
    }

    @Test
    public void checkSaveComment() throws Exception {
        User user = makeUser(1L, "Igor", "igor@ya.ru");
        Item item = makeItem(1L, "item1", "description1", true, user, Collections.emptyList(), null);
        Comment commentBeforeSave = makeComment(null, "comment", item, user, LocalDate.now());
        Comment commentAfterSave = makeComment(1L, "comment", item, user, LocalDate.now());
        when(userService.getUser(1L)).thenReturn(user);
        when(itemService.getItem(1L)).thenReturn(item);
        when(itemService.saveComment(commentBeforeSave)).thenReturn(commentAfterSave);

        CommentResponseDto commentResponseDto = makeCommentResponseDto(1L, "comment", "item1", "Igor", LocalDate.now());

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(new CommentCreateDto("comment"))))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(commentResponseDto)));

        verify(userService).getUser(1L);
        verify(itemService).getItem(1L);
        verify(itemService).saveComment(commentBeforeSave);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(itemService);

    }
}