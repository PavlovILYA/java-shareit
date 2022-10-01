package ru.practicum.shareit.request.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.common.LocalDateAdapter;
import ru.practicum.shareit.common.LocalDateTimeAdapter;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
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

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RequestService requestService;
    @MockBean
    private UserService userService;

    private User requester;
    private ItemRequest request;
    ItemRequestResponseDto requestResponseDto;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    @BeforeEach
    void setUp() {
        requester = makeUser(1L, "Denis", "denis@ya.ru");
        request = makeItemRequest(1L, "description",
                LocalDateTime.of(2022, 10, 10, 10, 10, 10),requester, null);
        requestResponseDto = makeItemRequestResponseDto(1L, "description",
                LocalDateTime.of(2022, 10, 10, 10, 10, 10), requester, null);
    }

    @Test
    public void checkSaveRequest() throws Exception {
        when(userService.getUser(requester.getId())).thenReturn(requester);
        when(requestService.saveRequest(any())).thenReturn(request);

        mockMvc.perform(post("/requests")
                        .content(gson.toJson(new ItemRequestCreateDto("description")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, requester.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(requestResponseDto)));

        verify(userService).getUser(requester.getId());
        verify(requestService).saveRequest(any());
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(requestService);
    }

    @Test
    public void checkGetMyRequests() throws Exception {
        when(userService.getUser(requester.getId())).thenReturn(requester);
        when(requestService.getAllByRequester(requester)).thenReturn(List.of(request));

        mockMvc.perform(get("/requests")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, requester.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(List.of(requestResponseDto))));

        verify(userService).getUser(requester.getId());
        verify(requestService).getAllByRequester(requester);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(requestService);
    }

//    @Test
//    public void checkGetAlienRequests() throws Exception {
//        User otherUser = makeUser(2L, "Anna", "anna@ya.ru");
//        request.setRequester(otherUser);
//        when(userService.getUser(requester.getId())).thenReturn(requester);
//        when(requestService.getAllAlien(requester, 0, 5)).thenReturn(List.of(request));
//
//        requestResponseDto.setRequester(new UserDto(otherUser.getId(), otherUser.getName(), otherUser.getEmail()));
//        mockMvc.perform(get("/requests/all")
//                        .accept(MediaType.APPLICATION_JSON)
//                        .header(USER_ID_HEADER, requester.getId()))
//                .andExpect(status().isOk())
//                .andExpect(content().json(gson.toJson(List.of(requestResponseDto))));
//
//        verify(userService).getUser(requester.getId());
//        verify(requestService).getAllAlien(requester, 0, 5);
//        verifyNoMoreInteractions(userService);
//        verifyNoMoreInteractions(requestService);
//    }

    @Test
    public void checkGetRequestById() throws Exception {
        when(userService.getUser(requester.getId())).thenReturn(requester);
        when(requestService.getRequestById(requester.getId())).thenReturn(request);

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, requester.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(requestResponseDto)));

        verify(userService).getUser(requester.getId());
        verify(requestService).getRequestById(requester.getId());
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(requestService);
    }
}