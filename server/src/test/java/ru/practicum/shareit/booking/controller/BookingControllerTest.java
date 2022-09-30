package ru.practicum.shareit.booking.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.LocalDateAdapter;
import ru.practicum.shareit.common.LocalDateTimeAdapter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.common.TestObjectMaker.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;
    @MockBean
    private UserService userService;
    @MockBean
    private ItemService itemService;

    private User booker;
    private Item item;
    private Booking bookingWithoutId;
    private Booking booking;
    private BookingCreateDto bookingCreateDto;
    private BookingResponseDto bookingResponseDto;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    @BeforeEach
    void setUp() {
        booker = makeUser(1L, "Vova", "vova@ya.ru");
        User owner = makeUser(2L, "Petr", "petr@ya.ru");
        item = makeItem(1L, "item", "description", true,
                owner, null, null);
        bookingWithoutId = makeBooking(null, LocalDateTime.of(2022, 10, 10, 10, 10, 10),
                LocalDateTime.of(2022, 10, 11, 10, 10, 10), BookingStatus.WAITING, item, booker);
        booking = makeBooking(1L, LocalDateTime.of(2022, 10, 10, 10, 10, 10),
                LocalDateTime.of(2022, 10, 11, 10, 10, 10), BookingStatus.WAITING, item, booker);
        bookingCreateDto = makeBookingCreateDto(LocalDateTime.of(2022, 10, 10, 10, 10, 10),
                LocalDateTime.of(2022, 10, 11, 10, 10, 10), item.getId(), booker.getId());
        bookingResponseDto = makeBookingResponseDto(1L, LocalDateTime.of(2022, 10, 10, 10, 10, 10),
                LocalDateTime.of(2022, 10, 11, 10, 10, 10), BookingStatus.WAITING, item, booker);
    }

    @Test
    public void checkSaveBooking() throws Exception {
        when(itemService.getItem(item.getId())).thenReturn(item);
        when(userService.getUser(booker.getId())).thenReturn(booker);
        when(bookingService.saveBooking(bookingWithoutId)).thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .content(gson.toJson(bookingCreateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, booker.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(bookingCreateDto)));

        verify(itemService).getItem(item.getId());
        verify(userService).getUser(booker.getId());
        verify(bookingService).saveBooking(bookingWithoutId);
        verifyNoMoreInteractions(itemService);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    public void checkSaveBooking_durationException() throws Exception {
        bookingCreateDto.setEnd(LocalDateTime.of(2021, 10, 11, 10, 10, 10));
        mockMvc.perform(post("/bookings")
                        .content(gson.toJson(bookingCreateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, booker.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemService);
        verifyNoInteractions(userService);
        verifyNoInteractions(bookingService);
    }

    @Test
    public void checkSaveBooking_bookerIsOwnerException() throws Exception {
        item.setOwner(booker);
        when(itemService.getItem(item.getId())).thenReturn(item);
        when(userService.getUser(booker.getId())).thenReturn(booker);

        bookingCreateDto.setUserId(booker.getId());
        mockMvc.perform(post("/bookings")
                        .content(gson.toJson(bookingCreateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, booker.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(itemService).getItem(item.getId());
        verify(userService).getUser(booker.getId());
        verifyNoMoreInteractions(itemService);
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(bookingService);
    }

    @Test
    public void checkApproveBooking() throws Exception {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingService.approveBooking(booking.getId(), true, booker.getId()))
                .thenReturn(booking);

        bookingResponseDto.setStatus(BookingStatus.APPROVED);
        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .param("approved", "true")
                        .header(USER_ID_HEADER, booker.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(bookingResponseDto)));

        verify(bookingService).approveBooking(booking.getId(), true, booker.getId());
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    public void checkGetBooking() throws Exception {
        when(bookingService.getBookingById(booking.getId(), booker.getId()))
                .thenReturn(booking);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(USER_ID_HEADER, booker.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(bookingResponseDto)));

        verify(bookingService).getBookingById(booking.getId(), booker.getId());
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    public void checkGetMyBookingRequests_wrongStateException() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, booker.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .param("state", "WRONG STATE"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
    }

    @Test
    public void checkGetMyBookingRequests() throws Exception {
        when(bookingService.getBookingRequestsByUserId(booker.getId(), BookingState.WAITING, 0, 5))
                .thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings", 1L)
                        .header(USER_ID_HEADER, booker.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .param("state", "WAITING"))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(List.of(bookingResponseDto))));

        verify(bookingService).getBookingRequestsByUserId(booker.getId(), BookingState.WAITING, 0, 5);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    public void checkGetMyBookings() throws Exception {
        Long myId = 2L;
        when(bookingService.getBookingRequestsByUserId(myId, BookingState.WAITING, 0, 5))
                .thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings", 1L)
                        .header(USER_ID_HEADER, myId)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("state", "WAITING"))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(List.of(bookingResponseDto))));

        verify(bookingService).getBookingRequestsByUserId(myId, BookingState.WAITING, 0, 5);
        verifyNoMoreInteractions(bookingService);
    }
}
