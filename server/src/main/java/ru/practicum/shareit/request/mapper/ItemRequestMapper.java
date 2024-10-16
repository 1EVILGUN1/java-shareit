package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.mapper.ItemRequestResponseMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class ItemRequestMapper {

    public static ItemRequest mapToItemRequest(NewItemRequestDto dto) {
        LocalDateTime now = LocalDateTime.now();
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setCreated(now);
        itemRequest.setDescription(dto.getDescription());
        return itemRequest;
    }

    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest, ItemService itemService) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setCreated(itemRequest.getCreated());
        itemRequestDto.setDescription(itemRequest.getDescription());
        List<Item> items = itemService.getItemsByRequestId(itemRequest.getId());

        if (items != null && !items.isEmpty()) {
            itemRequestDto.setItems(items.stream().map(ItemRequestResponseMapper::mapToItemRequestResponse).toList());
        } else {
            itemRequestDto.setItems(Collections.emptyList());
        }
        return itemRequestDto;
    }
}
