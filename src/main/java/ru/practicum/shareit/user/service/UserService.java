package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    List<User> findAllUsers();

    User save(User user);

    User update(Long id, User updatedUser);

    User findUserById(Long id);

    User delete(Long id);
}
