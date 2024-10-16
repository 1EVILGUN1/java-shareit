package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerIT {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ItemService itemService;

    @MockBean
    BookingService bookingService;

    @SneakyThrows
    @Test
    void add_whenInvoked_ItemReturnedAndStatusIsOk() {
        ItemDto itemDto = new ItemDto(1L, "Test Item Name", "Test Description",
                true, null, null, null, null);
        User owner = new User(1L, "name", "email@email.ru");
        Item item = new Item(1L, "Test Item Name", "Test Description", true, owner, null);
        when(itemService.save(anyLong(), any(ItemDto.class))).thenReturn(item);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Item Name"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @SneakyThrows
    @Test
    void updateItem_whenInvoked_ItemReturnedAndStatusIsOk() {
        ItemDto updatedItemDto = new ItemDto(1L, "Updated Item", "Updated Description",
                false, null, null, null, null);
        User owner = new User(1L, "name", "email@email.ru");
        Item item = new Item(1L, "Updated Item", "Updated Description", false, owner, null);
        when(itemService.update(anyLong(), anyLong(), any(ItemDto.class))).thenReturn(item);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItemDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Item"))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @SneakyThrows
    @Test
    void getItemDtoById_whenItemFound_thenReturnStatusIsOkAndItemDto() {
        ItemDto itemDto = new ItemDto(1L, "Test Item", "Test Description", true,
                null, null, null, null);
        User owner = new User(1L, "name", "email@email.ru");
        Item item = new Item(1L, "Test Item", "Test Description", false, owner, null);
        when(itemService.getItem(1L)).thenReturn(item);

        mockMvc.perform(get("/items/{itemId}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @SneakyThrows
    @Test
    void getAllItemsOfOwner_whenInvoked_ListOfItemsReturnedAndStatusIsOk() {
        User owner = new User(1L, "name", "email@email.ru");
        Item item = new Item(1L, "Test Item", "Test Description", false, owner, null);
        when(itemService.getAllItemsOfOwner(1L)).thenReturn(List.of(item));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Test Item"))
                .andExpect(jsonPath("$[0].description").value("Test Description"));
    }

    @SneakyThrows
    @Test
    void searchItems_whenInvoked_ListOfItemsReturnedAndStatusIsOk() {
        User owner = new User(1L, "name", "email@email.ru");
        Item item = new Item(1L, "Test Item", "Test Description", false, owner, null);
        when(itemService.searchItems("Test")).thenReturn(List.of(item));

        mockMvc.perform(get("/items/search")
                        .param("text", "Test"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Item"))
                .andExpect(jsonPath("$[0].description").value("Test Description"));
    }

    @SneakyThrows
    @Test
    void addComment_whenInvoked_CommentReturnedAndStatusIsOk() {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Great item!");
        commentDto.setAuthorName("User2");
        commentDto.setCreated(LocalDateTime.now());
        User author = new User(2L, "User2", "User2@email.ru");
        User owner = new User(1L, "name", "email@email.ru");
        Item item = new Item(1L, "Updated Item", "Updated Description", false, owner, null);
        Comment comment = new Comment(1L, "Great item!", item, author, LocalDateTime.now());

        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(comment);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Great item!"))
                .andExpect(jsonPath("$.authorName").value("User2"));
    }

    @SneakyThrows
    @Test
    void getItemById_whenItemNotFound_thenReturnStatusIsNotFound() {
        long itemId = 1L;
        when(itemService.getItem(itemId)).thenThrow(new NotFoundException("Item not found"));

        mockMvc.perform(get("/items/{itemId}", itemId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Ошибка с входными параметрами."))
                .andExpect(jsonPath("$.description").value("Item not found"));
    }
}
