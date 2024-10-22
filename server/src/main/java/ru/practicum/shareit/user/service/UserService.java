package ru.practicum.shareit.user.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Transactional(readOnly = true)
public interface UserService {

    List<User> findAllUsers();

    User save(User user);

    User update(Long id, User updatedUser);

    User findById(Long id);

    void delete(Long id);
}
