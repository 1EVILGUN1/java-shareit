package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemRequestResponseDto;
import ru.practicum.shareit.item.model.Item;

public class ItemRequestResponseMapper {

    public static ItemRequestResponseDto mapToItemRequestResponse(Item item) {
        ItemRequestResponseDto itemRequestResponseDto = new ItemRequestResponseDto();
        itemRequestResponseDto.setItemId(item.getId());
        itemRequestResponseDto.setOwnerId(item.getOwner().getId());
        itemRequestResponseDto.setName(item.getName());
        return itemRequestResponseDto;
    }
}
