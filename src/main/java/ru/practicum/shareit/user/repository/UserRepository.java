package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserRepository {
    Collection<User> getAll();

    User findById(Long id);

    User save(User user);

    void update(Long userId, User user);

    void delete(Long id);
}
