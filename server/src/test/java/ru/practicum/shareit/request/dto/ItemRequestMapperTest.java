package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestMapperTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemRequestMapper itemRequestMapper;

    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("Test request description");
    }

    @Test
    void mapToItemRequest_whenNewItemRequestDtoIsProvided_thenItemRequestIsReturned() {
        NewItemRequestDto newItemRequestDto = new NewItemRequestDto();
        newItemRequestDto.setDescription("Test request description");

        ItemRequest actualItemRequest = ItemRequestMapper.mapToItemRequest(newItemRequestDto);

        assertEquals(newItemRequestDto.getDescription(), actualItemRequest.getDescription());
        assertEquals(LocalDateTime.now().getDayOfYear(), actualItemRequest.getCreated().getDayOfYear());
    }

    @Test
    void mapToItemRequestDto_whenItemRequestIsProvidedWithItems_thenItemRequestDtoIsReturned() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        User owner = new User(1L, "name", "email@email.ru");
        item.setOwner(owner);

        when(itemService.getItemsByRequestId(itemRequest.getId())).thenReturn(List.of(item));

        ItemRequestDto itemRequestDto = ItemRequestMapper.mapToItemRequestDto(itemRequest, itemService);

        assertEquals(itemRequest.getId(), itemRequestDto.getId());
        assertEquals(itemRequest.getCreated(), itemRequestDto.getCreated());
        assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());
        assertEquals(1, itemRequestDto.getItems().size());
    }

    @Test
    void mapToItemRequestDto_whenItemRequestIsProvidedWithoutItems_thenItemRequestDtoIsReturnedWithEmptyItems() {
        when(itemService.getItemsByRequestId(itemRequest.getId())).thenReturn(Collections.emptyList());

        ItemRequestDto itemRequestDto = ItemRequestMapper.mapToItemRequestDto(itemRequest, itemService);

        assertEquals(itemRequest.getId(), itemRequestDto.getId());
        assertEquals(itemRequest.getCreated(), itemRequestDto.getCreated());
        assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());
        assertEquals(0, itemRequestDto.getItems().size());
    }
}
