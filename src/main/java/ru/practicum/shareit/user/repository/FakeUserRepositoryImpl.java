package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.EmailAlreadyExistsException;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class FakeUserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long nextId;

    @Override
    public User save(User user) {
        checkUniqueEmail(user.getEmail());
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        User storedUser = users.get(user.getId());
        if (user.getEmail() != null) {
            checkUniqueEmail(user.getEmail());
            storedUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            storedUser.setName(user.getName());
        }
        return storedUser;
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    @Override
    public User get(Long id) {
        return users.get(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    private Long generateId() {
        return ++nextId;
    }

    private void checkUniqueEmail(String email) {
        Set<String> existEmails = users.values().stream()
                .map(User::getEmail)
                .collect(Collectors.toSet());
        if (existEmails.contains(email)) {
            throw new EmailAlreadyExistsException("Email '" + email + "' already exists");
        }
    }
}
