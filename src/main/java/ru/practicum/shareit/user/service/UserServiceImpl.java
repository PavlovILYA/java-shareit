package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        User storedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> {
                    throw new UserNotFoundException(user.getId());
                });
        updateFields(user, storedUser);
        return userRepository.save(storedUser);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    throw new UserNotFoundException(id);
                });
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
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
