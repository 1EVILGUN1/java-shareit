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
    public void save(Item item, long userId) {
        final long id = ++counterId;
        item.setId(id);
        item.setUserId(userId);
        items.put(id, item);
    }

    @Override
    public void update(Item item) {
        Item itemStorage = items.get(item.getId());
        if (item.getName() != null) {
            itemStorage.setName(item.getName());
            items.put(itemStorage.getId(), itemStorage);
        }
        if (item.getDescription() != null) {
            itemStorage.setDescription(item.getDescription());
            items.put(itemStorage.getId(), itemStorage);
        }
        if (item.getAvailable() != null) {
            itemStorage.setAvailable(item.getAvailable());
            items.put(itemStorage.getId(), itemStorage);
        }
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
