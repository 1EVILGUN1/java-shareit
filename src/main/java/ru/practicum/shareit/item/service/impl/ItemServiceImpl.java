package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public ItemDto save(long userId, ItemDto itemDto) {
        try {
            userService.findById(userId);
        } catch (NotFoundException ex) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден!");
        }
        Item item = ItemMapper.mapToItem(itemDto);
        item.setUserId(userId);
        return ItemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        userService.findById(userId);
        checkItem(itemId);
        if (!userService.findById(userId).getItems().contains(itemId)) {
            throw new ValidationException("Пользователь с ID: " + userId + " не владеет вещью с ID: " + itemId);
        }
        Item itemUpdate = ItemMapper.mapToItem(itemDto);
        itemUpdate.setId(itemId);
        Item item = itemRepository.findById(itemId);
        if (itemUpdate.getName() != null) item.setName(itemUpdate.getName());
        if (itemUpdate.getDescription() != null) item.setDescription(itemUpdate.getDescription());
        if (itemUpdate.getAvailable() != null) item.setAvailable(itemUpdate.getAvailable());
        itemRepository.update(item);
        Item updatedItem = itemRepository.findById(itemId);
        return ItemMapper.mapToItemDto(updatedItem);
    }

    @Override
    public ItemDto findById(long itemId) {
        checkItem(itemId);
        return ItemMapper.mapToItemDto(itemRepository.findById(itemId));
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        userService.findById(userId);
        List<Item> items = itemRepository.getItemsUser(userId).stream().toList();
        User user = UserMapper.mapToUser(userService.findById(userId));
        return ItemMapper.mapToListItemDto(items).stream().toList();
    }

    @Override
    public ItemDto delete(long userId, long itemId) {
        userService.findById(userId);
        checkItem(itemId);
        Item deletedItem = itemRepository.findById(itemId);
        itemRepository.delete(itemId);
        userService.findById(userId).getItems().remove(itemId);
        return ItemMapper.mapToItemDto(deletedItem);
    }

    @Override
    public Collection<ItemDto> searchItemByName(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        Collection<Item> items = itemRepository.searchItemByName(text);
        Collection<Item> availableItems = items.stream().filter(Item::getAvailable).collect(Collectors.toList());
        return ItemMapper.mapToListItemDto(availableItems);
    }

    @Override
    public List<Long> getIdItemsUser(long userId) {
        return itemRepository.getIdItemsUser(userId);
    }

    private void checkItem(long itemId) {
        try {
            itemRepository.findById(itemId);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("Предмет с ID: " + itemId + " не найден!");
        }
    }
}
