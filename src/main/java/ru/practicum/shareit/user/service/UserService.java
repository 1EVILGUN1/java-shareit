package ru.practicum.shareit.user.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Transactional(readOnly = true)
public interface UserService {

    List<User> findAllUsers();

    @Transactional
    User save(User user);

    @Transactional
    User update(Long id, User updatedUser);

    User findUserById(Long id);

    @Transactional
    User delete(Long id);
}
