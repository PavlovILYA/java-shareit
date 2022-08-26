package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User save(User user);

    User update(User user);

    void delete(Long id);

    User get(Long id);

    List<User> getAll();
}
