package ru.practicum.shareit.item.controller;

import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
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

    private User user;
    private Item itemWithoutId;
    private Item item;
    private ItemDto itemDtoWithoutId;
    private ItemDto itemDto;
    private ItemResponseDto itemResponseDto;

    @BeforeEach
    void setUp() {
        user = makeUser(2L, "Igor", "igor@ya.ru");
        itemWithoutId = makeItem(null, "item1", "description1", true, user,
                Collections.emptyList(), null);
        item = makeItem(1L, "item1", "description1", true, user,
                Collections.emptyList(), null);
        itemDtoWithoutId = makeItemDto(null, "item1", "description1", true, null);
        itemDto = makeItemDto(1L, "item1", "description1", true, null);
        itemResponseDto = makeItemResponseDto(1L, "item1", "description1", true,
                null, null, null, Collections.emptyList());
    }

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    @Test
    public void checkSaveItem_withoutRequest() throws Exception {
        when(userService.getUser(user.getId())).thenReturn(user);
        when(itemService.saveItem(itemWithoutId)).thenReturn(item);

        mockMvc.perform(post("/items")
                        .content(gson.toJson(itemDtoWithoutId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, user.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json((gson.toJson(itemDto))));

        verify(userService).getUser(user.getId());
        verify(itemService).saveItem(itemWithoutId);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(itemService);
        verifyNoInteractions(requestService);
    }

    @Test
    public void checkSaveItem_withRequest() throws Exception {
        when(userService.getUser(user.getId())).thenReturn(user);

        User requester = makeUser(3L, "Nick", "nick@ya.ru");
        ItemRequest itemRequest = makeRequest(3L, "some request",
                LocalDateTime.of(2022, 9, 11, 10, 10, 10), requester, null);
        when(requestService.getRequestById(3L)).thenReturn(itemRequest);

        itemWithoutId.setItemRequest(itemRequest);
        item.setItemRequest(itemRequest);
        when(itemService.saveItem(itemWithoutId)).thenReturn(item);

        itemDtoWithoutId.setRequestId(itemRequest.getId());
        itemDto.setRequestId(itemRequest.getId());

        mockMvc.perform(post("/items")
                        .content(gson.toJson(itemDtoWithoutId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, user.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json((gson.toJson(itemDto))));

        verify(userService).getUser(user.getId());
        verify(itemService).saveItem(itemWithoutId);
        verify(requestService).getRequestById(3L);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(itemService);
        verifyNoMoreInteractions(requestService);
    }

    @Test
    public void checkUpdateItem() throws Exception {
        when(userService.getUser(user.getId())).thenReturn(user);
        when(itemService.updateItem(item)).thenReturn(item);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(itemDtoWithoutId)))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(itemDto)));

        verify(userService).getUser(user.getId());
        verify(itemService).updateItem(item);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(itemService);
    }

    @Test
    public void checkGetItem() throws Exception {
        when(itemService.getItem(item.getId())).thenReturn(item);

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, 3L))    // 1111!!!!!
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(itemResponseDto)));

        verify(itemService).getItem(item.getId());
        verifyNoMoreInteractions(itemService);
        verifyNoInteractions(bookingService);
    }

    @Test
    public void checkGetItem_withBookings() throws Exception {
        Booking lastBooking = makeBooking(4L, LocalDateTime.of(2022, 9, 11, 10, 10, 10),
                LocalDateTime.of(2022, 9, 11, 20, 10, 10), BookingStatus.APPROVED, item, user);
        Booking nextBooking = makeBooking(4L, LocalDateTime.of(2022, 10, 11, 10, 10, 10),
                LocalDateTime.of(2022, 10, 11, 20, 10, 10), BookingStatus.WAITING, item, user);
        when(itemService.getItem(item.getId())).thenReturn(item);
        when(bookingService.getLastBookingByItem(item)).thenReturn(lastBooking);
        when(bookingService.getNextBookingByItem(item)).thenReturn(nextBooking);

        BookingResponseDto lastBookingDto = makeBookingResponseDto(4L, LocalDateTime.of(2022, 9, 11, 10, 10, 10),
                LocalDateTime.of(2022, 9, 11, 20, 10, 10), BookingStatus.APPROVED, item, user);
        BookingResponseDto nextBookingDto = makeBookingResponseDto(4L, LocalDateTime.of(2022, 10, 11, 10, 10, 10),
                LocalDateTime.of(2022, 10, 11, 20, 10, 10), BookingStatus.WAITING, item, user);
        ItemResponseDto itemDto = makeItemResponseDto(1L, "item1", "description1",
                true, null, lastBookingDto, nextBookingDto, Collections.emptyList());

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, user.getId()))
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
        when(itemService.getAllByUserId(user.getId(), 0, 2)).thenReturn(List.of(item));
        when(bookingService.getLastBookingByItem(any())).thenReturn(null);
        when(bookingService.getNextBookingByItem(any())).thenReturn(null);

        mockMvc.perform(get("/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, user.getId())
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(List.of(itemResponseDto))));

        verify(itemService).getAllByUserId(user.getId(), 0, 2);
        verify(bookingService).getLastBookingByItem(any());
        verify(bookingService).getNextBookingByItem(any());
        verifyNoMoreInteractions(itemService);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    public void checkSearch() throws Exception {
        String template = "item";
        when(itemService.getAllByTemplate(template, 0, 2)).thenReturn(List.of(item));

        mockMvc.perform(get("/items/search")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, user.getId())
                        .param("text", template)
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(List.of(itemDto))));

        verify(itemService).getAllByTemplate(template, 0, 2);
        verifyNoMoreInteractions(itemService);
    }

    @Test
    public void checkSaveComment() throws Exception {
        Comment commentBeforeSave = makeComment(null, "comment", item, user, LocalDate.now());
        Comment commentAfterSave = makeComment(1L, "comment", item, user, LocalDate.now());
        when(userService.getUser(user.getId())).thenReturn(user);
        when(itemService.getItem(item.getId())).thenReturn(item);
        when(itemService.saveComment(commentBeforeSave)).thenReturn(commentAfterSave);

        CommentResponseDto commentResponseDto = makeCommentResponseDto(1L, "comment",
                "item1", "Igor", LocalDate.now());

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(new CommentCreateDto("comment"))))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(commentResponseDto)));

        verify(userService).getUser(user.getId());
        verify(itemService).getItem(item.getId());
        verify(itemService).saveComment(commentBeforeSave);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(itemService);

    }
}