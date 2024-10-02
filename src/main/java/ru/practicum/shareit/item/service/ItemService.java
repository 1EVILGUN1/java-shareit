package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    ItemDto save(long userId, ItemDto item);

    ItemDto update(long userId, long itemId, ItemDto item);

    ItemDto findById(long itemId, long userId);

    List<ItemDto> getItems(long userId);

    ItemDto delete(long userId, long itemId);

    Collection<ItemDto> searchItemByName(String text, long userId);

    CommentDto saveComment(CommentDto comment, long itemId, long userId);
}
