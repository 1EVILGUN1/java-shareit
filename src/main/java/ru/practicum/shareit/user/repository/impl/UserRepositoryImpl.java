package ru.practicum.shareit.user.repository.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.base.BaseRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;

@Repository
public class UserRepositoryImpl extends BaseRepository<User> implements UserRepository {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users (name, email) VALUES (?, ?)";
    private static final String UPDATE_NAME = "UPDATE users SET name = ? WHERE id = ?";
    private static final String UPDATE_EMAIL = "UPDATE users SET email = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM users WHERE id=?";


    public UserRepositoryImpl(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<User> getAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public User findById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id " + id + " не найден"));
    }

    @Override
    public User save(User user) {
        long id = insert(
                INSERT_QUERY,
                user.getName(),
                user.getEmail()
        );
        user.setId(id);
        return user;
    }

    @Override
    public void update(Long userId, User user) {
        if (user.getName() != null) {
            jdbc.update(UPDATE_NAME, user.getName(), userId);
        }
        if (user.getEmail() != null) {
            jdbc.update(UPDATE_EMAIL, user.getEmail(), userId);
        }
    }

    @Override
    public void delete(Long id) {
        delete(DELETE_QUERY, id);
    }
}
