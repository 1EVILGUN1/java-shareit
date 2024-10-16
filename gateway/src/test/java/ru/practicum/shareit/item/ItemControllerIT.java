package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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
    ItemClient itemClient;

    @SneakyThrows
    @Test
    void addItem_whenValidRequest_thenReturnStatusIsOk() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item Name");
        itemDto.setDescription("Item Description");
        itemDto.setAvailable(true);

        when(itemClient.save(any(Long.class), any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void addItem_whenNameIsBlank_thenReturnStatusIsBadRequest() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(""); // Пустое имя
        itemDto.setDescription("Item Description");
        itemDto.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void updateItem_whenValidRequest_thenReturnStatusIsOk() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Item Name");
        itemDto.setDescription("Updated Item Description");
        itemDto.setAvailable(true);

        when(itemClient.update(any(Long.class), any(Long.class), any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void getById_whenItemExists_thenReturnStatusIsOk() {
        long userId = 1L;
        long itemId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setId(itemId);
        itemDto.setName("Item Name");
        itemDto.setDescription("Item Description");
        itemDto.setAvailable(true);

        when(itemClient.findById(userId, itemId)).thenReturn(ResponseEntity.ok(itemDto));

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Item Name"))
                .andExpect(jsonPath("$.description").value("Item Description"));
    }

    @SneakyThrows
    @Test
    void getItemsOfOwner_whenInvoked_thenReturnStatusIsOk() {
        long userId = 1L;
        when(itemClient.getItemsOfOwner(userId)).thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void searchItems_whenInvoked_thenReturnStatusIsOk() {
        long userId = 1L;
        String searchText = "Item";
        when(itemClient.searchItems(userId, searchText)).thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", searchText))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void addComment_whenValidRequest_thenReturnStatusIsOk() {
        long userId = 1L;
        long itemId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        when(itemClient.addComment(any(Long.class), any(Long.class), any(CommentDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void addComment_whenTextIsBlank_thenReturnStatusIsBadRequest() {
        long userId = 1L;
        long itemId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setText("");

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
