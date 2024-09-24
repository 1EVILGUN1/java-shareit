package ru.practicum.shareit.item.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.base.BaseRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.*;

@Slf4j
@Repository
public class ItemRepositoryImpl extends BaseRepository<Item> implements ItemRepository {
    private static final String FIND_ALL_ITEMS_QUERY =
            """
                    SELECT * FROM item AS i
                    JOIN user_items AS ui ON i.id = ui.item_id
                    WHERE ui.user_id = ?
                    """;
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM item WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO item (name, description, available) VALUES (?, ?, ?)";
    private static final String INSERT_ITEM_USER_QUERY = "INSERT INTO user_items (user_id, item_id) VALUES (?, ?)";
    private static final String UPDATE_NAME = "UPDATE item SET name = ? WHERE id = ?";
    private static final String UPDATE_DESCRIPTION = "UPDATE item SET description = ? WHERE id = ?";
    private static final String UPDATE_AVAILABLE = "UPDATE item SET available = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM item WHERE id=?";
    private static final String SEARCH_ITEM_BY_NAME = "SELECT * FROM item WHERE UPPER(name) LIKE CONCAT('%',?,'%') OR UPPER(description) LIKE CONCAT('%',?,'%')";
    private static final String FIND_ID_ITEMS_USER = "SELECT item_id FROM user_items WHERE user_id = ?";

    public ItemRepositoryImpl(JdbcTemplate jdbc, RowMapper<Item> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public void save(Item item, long userId) {
        long id = insert(
                INSERT_QUERY,
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
        item.setId(id);
        jdbc.update(INSERT_ITEM_USER_QUERY, userId, item.getId());
    }

    @Override
    public void update(Item item) {
        if (item.getName() != null) {
            jdbc.update(UPDATE_NAME, item.getName(), item.getId());
        }
        if (item.getDescription() != null) {
            jdbc.update(UPDATE_DESCRIPTION, item.getDescription(), item.getId());
        }
        if (item.getAvailable() != null) {
            jdbc.update(UPDATE_AVAILABLE, item.getAvailable(), item.getId());
        }
    }

    @Override
    public Item findById(Long itemId) {
        return findOne(FIND_BY_ID_QUERY, itemId)
                .orElseThrow(() -> new IllegalArgumentException("Предмет с ID: " + itemId + " не найден!"));
    }

    @Override
    public Collection<Item> getItemsUser(long userId) {
        return jdbc.query(FIND_ALL_ITEMS_QUERY, mapper, userId);
    }

    @Override
    public void delete(long itemId) {
        delete(DELETE_QUERY, itemId);
    }

    @Override
    public Collection<Item> searchItemByName(String text) {
        return jdbc.query(SEARCH_ITEM_BY_NAME, mapper, text, text);
    }

    @Override
    public List<Long> getIdItemsUser(long userId) {
        return jdbc.queryForList(FIND_ID_ITEMS_USER, Long.class, userId);
    }
}
