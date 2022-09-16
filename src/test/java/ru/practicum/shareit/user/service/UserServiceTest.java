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

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Captor
    ArgumentCaptor<User> captor;

    @BeforeEach
    public void beforeEach() {
        userService = new UserServiceImpl(userRepository);
    }


    @Test
    public void checkSaveUser() {
        User user = makeUserWithId(null, "Василий", "vasya@ya.ru");
        User userFromDb = makeUserWithId(1L, "Василий", "vasya@ya.ru");
        when(userRepository.save(user)).thenReturn(userFromDb);

        userFromDb = userService.saveUser(user);

        assertNotNull(userFromDb);
        assertEquals(1L, userFromDb.getId());
        assertEquals("Василий", userFromDb.getName());
        assertEquals("vasya@ya.ru", userFromDb.getEmail());

        verify(userRepository).save(user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void checkUpdateUser_notFound() {
        User userForUpdate = makeUserWithId(1L, "Василий", "vasya@ya.ru");
        when(userRepository.findById(userForUpdate.getId())).thenReturn(Optional.empty());

        final var thrown = assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(userForUpdate));

        assertEquals("User " + userForUpdate.getId() + " not found", thrown.getMessage());

        verify(userRepository).findById(userForUpdate.getId());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void checkUpdateUser_updated() {
        User userBeforeUpdate = makeUserWithId(1L, "Василий", "vasya@ya.ru");
        User userAfterUpdate = makeUserWithId(1L, "Николай", "nick@ya.ru");
        when(userRepository.findById(userBeforeUpdate.getId())).thenReturn(Optional.of(userBeforeUpdate));
        when(userRepository.save(userAfterUpdate)).thenReturn(userAfterUpdate);

        User userAfterTest = userService.updateUser(userAfterUpdate);

        assertEquals(userAfterUpdate, userAfterTest);

        verify(userRepository).findById(userBeforeUpdate.getId());
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
        User userFromDb = makeUserWithId(1L, "Denis", "den@ya.ru");
        when(userRepository.findById(any())).thenReturn(Optional.of(userFromDb));

        User foundUser = userService.getUser(1L);

        assertEquals(userFromDb, foundUser);

        verify(userRepository).findById(1L);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void checkGetAll() {
        User user1 = makeUserWithId(1L, "Mike", "mike@ya.ru");
        User user2 = makeUserWithId(2L, "Jane", "jane@ya.ru");
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<User> foundUsers = userService.getAll();

        assertEquals(2, foundUsers.size());
        assertEquals(user1, foundUsers.get(0));
        assertEquals(user2, foundUsers.get(1));

        verify(userRepository).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    private User makeUserWithId(Long id, String name, String email) {
        return User.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();
    }
}
