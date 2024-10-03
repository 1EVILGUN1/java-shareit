package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    ItemDto save(long userId, ItemCreateDto itemCreateDto);

    ItemDto update(long userId, long itemId, ItemUpdateDto itemUpdateDto);

    ItemDto findById(long itemId, long userId);

    Collection<ItemDto> getItems(long userId);

    ItemDto delete(long userId, long itemId);

    Collection<ItemDto> searchItemByName(String text, long userId);

    Item checkItemDto(long id);

    CommentDto addComment(CommentCreateDto commentCreateDto, long itemId, long userId);
}
