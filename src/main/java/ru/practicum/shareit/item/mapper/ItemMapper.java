package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemMapper {

    public static ItemDto mapToDtoWithComments(Item item, ItemService itemService) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        if (item.getAvailable() != null) {
            dto.setAvailable(item.getAvailable());
        }
        List<Comment> comments = itemService.getComments(item.getId());
        dto.setComments(comments
                .stream()
                .map(CommentMapper::mapToCommentDto)
                .toList());
        return dto;
    }

    public static ItemDto mapToDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        if (item.getAvailable() != null) {
            dto.setAvailable(item.getAvailable());
        }
        return dto;
    }

    public static Item mapToItem(ItemDto dto) {
        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        if (dto.isAvailable() != null) {
            item.setAvailable(dto.isAvailable());
        }
        item.setAvailable(dto.isAvailable());
        item.setId(dto.getId());
        return item;
    }

    public static ItemOwnerDto mapToDtoOwner(Item item, BookingService bookingService, ItemService itemService) {
        ItemOwnerDto dto = new ItemOwnerDto();
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        Booking lastBooking = bookingService.findLastBooking(item);
        Booking futureBooking = bookingService.findFutureBooking(item);
        if (lastBooking != null) {
            dto.setLastBooking(BookingMapper.mapToItemBookingDto(lastBooking));
        }
        if (futureBooking != null) {
            dto.setNextBooking(BookingMapper.mapToItemBookingDto(futureBooking));
        }
        List<Comment> comments = itemService.getComments(item.getId());
        dto.setComments(comments
                .stream()
                .map(CommentMapper::mapToCommentDto)
                .toList());
        return dto;
    }
}
