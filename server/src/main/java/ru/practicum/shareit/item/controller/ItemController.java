package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService service;
    private final BookingService bookingService;

    @PostMapping
    public Item add(@RequestHeader("X-Sharer-User-Id") Long userId,
                    @RequestBody ItemDto dto) {
        Item savedItem = service.save(userId, dto);
        log.info("Added item: {}", savedItem);
        return savedItem;
    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @PathVariable("itemId") Long id,
                       @RequestBody ItemDto dto) {
        Item updatedItem = service.update(userId, id, dto);
        log.info("Updated item: {}", updatedItem);
        return updatedItem;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemDtoById(@PathVariable("itemId") Long id) {
        ItemDto itemDto = ItemMapper.mapToDtoWithComments(service.getItem(id), service);
        log.info("get Item by id: {}", itemDto);
        return itemDto;
    }

    @GetMapping
    public List<ItemOwnerDto> getAllItemsOfOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemOwnerDto> itemOwnerDtos = service.getAllItemsOfOwner(userId)
                .stream()
                .map(item -> ItemMapper.mapToDtoOwner(item, bookingService, service))
                .toList();
        log.info("get all owner items: {}", itemOwnerDtos);
        return itemOwnerDtos;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam("text") String text) {
        List<ItemDto> itemDtos = service.searchItems(text)
                .stream().map(ItemMapper::mapToDto)
                .toList();
        log.info("search items: {}", itemDtos);
        return itemDtos;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long authorId, @PathVariable("itemId") Long itemId,
                                 @RequestBody CommentDto dto) {
        CommentDto commentDto = CommentMapper.mapToCommentDto(service.addComment(authorId, itemId, dto));
        log.info("Comment added: {}", commentDto);
        return commentDto;
    }
}
