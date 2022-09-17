package ru.practicum.shareit.user.controller;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static ru.practicum.shareit.common.TestObjectMaker.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @Captor
    private ArgumentCaptor<User> captor;

    private final Gson gson = new Gson();

    @Test
    public void checkGetAll() throws Exception {
        List<User> users = List.of(makeUser(1L, "Ivan", "ivan@ya.ru"),
                makeUser(2L, "Ivana", "ivana@ya.ru"));
        when(userService.getAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(users)));

        verify(userService, times(1)).getAll();
    }

    @Test
    public void checkSaveUser() throws Exception {
        User userForSave = makeUser(null, "Tom", "tom@ya.ru");
        User savedUser = makeUser(2L, "Tom", "tom@ya.ru");
        when(userService.saveUser(userForSave)).thenReturn(savedUser);

        UserDto userDtoForSave = makeUserDto(null, "Tom", "tom@ya.ru");
        UserDto savedUserDto = makeUserDto(2L, "Tom", "tom@ya.ru");

        mockMvc.perform(post("/users")
                        .content(gson.toJson(userDtoForSave))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(savedUserDto)));

        verify(userService).saveUser(captor.capture());
        final var value = captor.getValue();
        assertEquals(userForSave, value);

        verifyNoMoreInteractions(userService);
    }

    @Test
    public void checkUpdateUser_validException() throws Exception {
        UserDto userDtoForSave = makeUserDto(2L, "", "tom@ya.ru");
        mockMvc.perform(patch("/users/{userId}", 2L)
                        .content(gson.toJson(userDtoForSave))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    public void checkUpdateUser_updated() throws Exception {
        User userForUpdate = makeUser(2L, "Tom", "tom@ya.ru");
        when(userService.updateUser(userForUpdate)).thenReturn(userForUpdate);

        UserDto userDtoForSave = makeUserDto(2L, "Tom", "tom@ya.ru");

        mockMvc.perform(patch("/users/{userId}", 2L)
                        .content(gson.toJson(userDtoForSave))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(userDtoForSave)));

        verify(userService).updateUser(captor.capture());
        final var value = captor.getValue();
        assertEquals(userForUpdate, value);

        verifyNoMoreInteractions(userService);
    }

    @Test
    public void checkDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/{userId}", 2L))
                .andExpect(status().isOk());

        verify(userService).deleteUser(2L);
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void checkGetUser() throws Exception {
        User user = makeUser(2L, "Tom", "tom@ya.ru");
        when(userService.getUser(2L)).thenReturn(user);

        UserDto userDto = makeUserDto(2L, "Tom", "tom@ya.ru");

        mockMvc.perform(get("/users/{userId}", 2L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(userDto)));

        verify(userService).getUser(2L);

        verifyNoMoreInteractions(userService);
    }
}
