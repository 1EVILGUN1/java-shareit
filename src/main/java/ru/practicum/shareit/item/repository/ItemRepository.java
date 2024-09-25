package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemRepository {
    void save(Item item);

    void update(Item item);

    Item findById(Long itemId);

    Collection<Item> getItemsUser(long userId);

    void delete(long itemId);

    Collection<Item> searchItemByName(String text);

    List<Long> getIdItemsUser(long userId);
}
