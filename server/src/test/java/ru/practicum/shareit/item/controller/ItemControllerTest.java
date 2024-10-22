package ru.practicum.shareit.item.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private ItemController itemController;

    @Test
    void add_whenInvoked_thenItemReturned() {
        Long userId = 1L;
        ItemDto itemDto = new ItemDto(1L, "Item", "Description",
                true, null, null, null, null);
        Item item = new Item();
        when(itemService.save(userId, itemDto)).thenReturn(item);

        Item result = itemController.add(userId, itemDto);

        assertEquals(item, result);
        verify(itemService, times(1)).save(userId, itemDto);
    }

    @Test
    void updateItem_whenInvoked_thenItemReturned() {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto itemDto = new ItemDto(1L, "Updated Item", "Updated Description",
                true, null, null, null, null);
        Item item = new Item();
        when(itemService.update(userId, itemId, itemDto)).thenReturn(item);

        Item result = itemController.update(userId, itemId, itemDto);

        assertEquals(item, result);
        verify(itemService, times(1)).update(userId, itemId, itemDto);
    }

    @Test
    void getItemDtoById_whenInvoked_ItemDtoReturned() {
        Long itemId = 1L;
        Item item = new Item();
        when(itemService.getItem(itemId)).thenReturn(item);
        when(itemService.getItem(itemId)).thenReturn(item);

        ItemDto result = itemController.getItemDtoById(itemId);

        assertNotNull(result);
        verify(itemService, times(1)).getItem(itemId);
    }

    @Test
    void getAllItemsOfOwner_whenInvoked_ListOfItemOwnerDtoReturned() {
        Long userId = 1L;
        List<Item> items = Collections.singletonList(new Item());
        when(itemService.getAllItemsOfOwner(userId)).thenReturn(items);

        List<ItemOwnerDto> result = itemController.getAllItemsOfOwner(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(itemService, times(1)).getAllItemsOfOwner(userId);
    }

    @Test
    void searchItems_whenInvoked_ListOfItemDtoReturned() {
        String searchText = "search";
        List<Item> items = Collections.singletonList(new Item());
        when(itemService.searchItems(searchText)).thenReturn(items);

        List<ItemDto> result = itemController.searchItems(searchText);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(itemService, times(1)).searchItems(searchText);
    }

    @Test
    void addComment_whenInvoked_CommentDtoReturned() {
        Long authorId = 1L;
        Long itemId = 1L;
        String commentText = "This is a comment";
        User author = new User(1L, "name", "email@email.ru");
        CommentDto commentDto = new CommentDto();
        commentDto.setText(commentText);
        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setId(1L);
        comment.setText(commentText);
        comment.setCreated(LocalDateTime.now());
        CommentDto expectedCommentDto = CommentMapper.mapToCommentDto(comment);
        when(itemService.addComment(authorId, itemId, commentDto)).thenReturn(comment);

        CommentDto result = itemController.addComment(authorId, itemId, commentDto);

        assertEquals(expectedCommentDto, result);
        verify(itemService, times(1)).addComment(authorId, itemId, commentDto);
    }
}