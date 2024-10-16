package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item save(Long userId, ItemDto dto);

    Item update(Long userId, Long itemId, ItemDto dto);

    Item getItem(Long id);

    List<Item> getAllItemsOfOwner(Long userId);

    List<Item> searchItems(String text);

    Comment addComment(Long bookerId, Long itemId, CommentDto dto);

    List<Comment> getComments(Long itemId);

    List<Item> getItemsByRequestId(Long requestId);
}
