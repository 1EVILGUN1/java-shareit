package ru.practicum.shareit.item.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private long counterId = 0;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item save(Item item) {
        item.setId(++counterId);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void update(Item item) {
        items.put(item.getId(), item);
    }

    @Override
    public Item findById(Long itemId) {
        Item item = items.get(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Предмет с ID: " + itemId + " не найден!");
        }
        return item;
    }

    @Override
    public Collection<Item> getItemsUser(long userId) {
        Collection<Item> itemsUsers = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getUserId() == userId) {
                itemsUsers.add(item);
            }
        }
        return itemsUsers;

    }

    @Override
    public void delete(long itemId) {
        items.remove(itemId);
    }

    @Override
    public Collection<Item> searchItemByName(String text) {
        Collection<Item> searchItem = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getName().toUpperCase().contains(text.toUpperCase())) {
                searchItem.add(item);
            } else if (item.getDescription().toUpperCase().contains(text.toUpperCase())) {
                searchItem.add(item);
            }
        }
        return searchItem;
    }

    @Override
    public List<Long> getIdItemsUser(long userId) {
        List<Long> itemUser = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getUserId() == userId) {
                itemUser.add(item.getId());
            }
        }
        return itemUser;
    }
}
