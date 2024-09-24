package ru.practicum.shareit.user.repository.mapper;


import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserRowMapper implements RowMapper<User> {
    private final ItemRepository itemRepository;

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));

        List<Long> items = itemRepository.getIdItemsUser(user.getId());
        user.setItems(new HashSet<>(items));
        return user;
    }
}
