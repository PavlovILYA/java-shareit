package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User saveUser(User user) {
        user = userRepository.save(user);
        log.debug("Saved user: {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        User storedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> {
                    throw new UserNotFoundException(user.getId());
                });
        updateFields(user, storedUser);
        storedUser = userRepository.save(storedUser);
        log.debug("Updated user: {}", storedUser);
        return storedUser;
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        log.debug("User {} was deleted", id);
    }

    @Override
    public User getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    throw new UserNotFoundException(id);
                });
        log.debug("Returned user: {}", user);
        return user;
    }

    @Override
    public List<User> getAll() {
        List<User> users = userRepository.findAll();
        log.debug("All users: {}", users);
        return users;
    }

    private void updateFields(User user, User storedUser) {
        if (user.getName() != null) {
            storedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            storedUser.setEmail(user.getEmail());
        }
    }
}
