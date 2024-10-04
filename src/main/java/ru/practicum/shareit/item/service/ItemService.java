package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    ItemDto create(long userId, ItemCreatedDto itemCreatedDto);

    ItemDto update(long userId, long itemId, ItemUpdatedDto itemUpdatedDto);

    ItemDto getItemById(long itemId, long userId);

    Collection<ItemDto> getItems(long userId);

    ItemDto remove(long userId, long itemId);

    Collection<ItemDto> searchItemByName(String text, long userId);

    Item checkItemDto(long id);

    CommentDto addComment(CommentCreatedDto commentCreatedDto, long itemId, long userId);
}
