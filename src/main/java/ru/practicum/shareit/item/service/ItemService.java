package ru.practicum.shareit.item.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Transactional(readOnly = true)
public interface ItemService {

    @Transactional
    Item save(Long userId, ItemDto dto);

    @Transactional
    Item update(Long userId, Long itemId, ItemDto dto);

    Item findItemById(Long id);

    List<Item> getAllItemsOfOwner(Long userId);

    List<Item> searchItems(String text);

    @Transactional
    Comment addComment(Long bookerId, Long itemId, CommentDto dto);

    List<Comment> getComments(Long itemId);
}
