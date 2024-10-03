package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public final class ItemMapper {
    public static ItemDto mapToItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item mapItemCreatedDtoToItem(ItemCreateDto itemUpdatedDto) {
        return Item.builder()
                .name(itemUpdatedDto.getName())
                .description(itemUpdatedDto.getDescription())
                .available(itemUpdatedDto.getAvailable())
                .build();
    }

    public static Item mapItemUpdatedDtoToItem(ItemUpdateDto itemUpdateDto) {
        return Item.builder()
                .id(itemUpdateDto.getId())
                .name(itemUpdateDto.getName())
                .description(itemUpdateDto.getDescription())
                .available(itemUpdateDto.getAvailable())
                .build();
    }

    public static Collection<ItemDto> mapToItemDtoList(Collection<Item> items) {
        return items.stream().map(ItemMapper::mapToItemDto).collect(Collectors.toList());
    }
}
