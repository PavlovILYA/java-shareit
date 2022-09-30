package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.common.TestObjectMaker.makeUser;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Captor
    private ArgumentCaptor<User> captor;

    private User user;
    private User userWithoutId;

    @BeforeEach
    public void beforeEach() {
        userService = new UserServiceImpl(userRepository);
        user = makeUser(1L, "Василий", "vasya@ya.ru");
        userWithoutId = makeUser(null, "Василий", "vasya@ya.ru");
    }

    @Test
    public void checkSaveUser() {
        when(userRepository.save(userWithoutId)).thenReturn(user);

        User userFromDb = userService.saveUser(userWithoutId);
        assertNotNull(userFromDb);
        assertEquals(user, userFromDb);

        verify(userRepository).save(userWithoutId);
        verify(userRepository, times(1)).save(userWithoutId);
    }

    @Test
    public void checkUpdateUser_notFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        final var thrown = assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(user));
        assertEquals("User " + user.getId() + " not found", thrown.getMessage());

        verify(userRepository).findById(user.getId());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void checkUpdateUser_updated() {
        User userAfterUpdate = makeUser(1L, "Николай", "nick@ya.ru");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(userAfterUpdate)).thenReturn(userAfterUpdate);

        User userAfterTest = userService.updateUser(userAfterUpdate);
        assertEquals(userAfterUpdate, userAfterTest);

        verify(userRepository).findById(user.getId());
        verify(userRepository).save(captor.capture());
        final var captureUser = captor.getValue();
        assertEquals(userAfterUpdate, captureUser);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void checkDeleteUser() {
        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void checkGetUser_notFound() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        final var thrown = assertThrows(UserNotFoundException.class, () -> userService.getUser(1L));
        assertEquals("User " + 1L + " not found", thrown.getMessage());

        verify(userRepository).findById(1L);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void checkGetUser_found() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        User foundUser = userService.getUser(user.getId());
        assertEquals(user, foundUser);

        verify(userRepository).findById(user.getId());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void checkGetAll() {
        User user2 = makeUser(2L, "Jane", "jane@ya.ru");
        when(userRepository.findAll()).thenReturn(List.of(user, user2));

        List<User> foundUsers = userService.getAll();
        assertEquals(2, foundUsers.size());
        assertEquals(user, foundUsers.get(0));
        assertEquals(user2, foundUsers.get(1));

        verify(userRepository).findAll();
        verifyNoMoreInteractions(userRepository);
    }
}
