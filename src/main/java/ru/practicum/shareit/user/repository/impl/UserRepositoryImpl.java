package ru.practicum.shareit.user.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;


@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private long counterId = 0;
    private final Map<Long, User> users = new HashMap<>();
    private final Set emailUniqSet = new HashSet<>();


    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User findById(Long id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с таким id " + id + " не найден");
        }
        return user;
    }

    @Override
    public User save(User user) {
        final String email = user.getEmail();
        if (emailUniqSet.contains(email)) {
            throw new IllegalArgumentException("Пользователь с Email: " + email + " уже зарегистрирован");
        }
        user.setId(++counterId);
        users.put(user.getId(), user);
        emailUniqSet.add(email);
        return user;
    }

    @Override
    public void update(Long userId, User user) {
        User userStorage = users.get(userId);
        if (user.getName() != null) {
            userStorage.setName(user.getName());
            users.put(userId, userStorage);
        }
        if (user.getEmail() != null) {
            userStorage.setEmail(user.getEmail());
            users.put(userId, userStorage);
        }
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }
}
