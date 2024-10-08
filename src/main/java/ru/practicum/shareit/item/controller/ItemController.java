package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private final ItemService service;
    private final BookingService bookingService;

    @PostMapping
    public ItemDto save(@RequestHeader("X-Sharer-User-Id") Long userId,
                        @Valid @RequestBody ItemDto dto) {
        log.info("POST Запрос на добавление вещи пользователем с ID {} \n {}", userId, dto);
        ItemDto savedItem = ItemMapper.mapToDto(service.save(userId, dto));
        log.info("Вещь была добавлена {}", savedItem);
        return savedItem;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable("itemId") Long id,
                          @RequestBody ItemDto dto) {
        log.info("PATCH Запрос на обновление вещи ID {}, пользователя ID {} \n {}", id, userId, dto);
        ItemDto updatedItem = ItemMapper.mapToDto(service.update(userId, id, dto));
        log.info("Запрос на обновление вещи выполнен {}", updatedItem);
        return updatedItem;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemDtoById(@PathVariable("itemId") Long id) {
        log.info("GET Запрос на получение вещи по ID {}", id);
        ItemDto dto = ItemMapper.mapToDtoWithComments(service.findItemById(id), service);
        log.info("Запрос на поиск вещи по ID выполнен {}", dto);
        return dto;
    }

    @GetMapping
    public List<ItemOwnerDto> getAllItemsOfOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET Запрос на получение вещей пользователя с ID {}", userId);
        List<ItemOwnerDto> itemOwnerDtoList = service.getAllItemsOfOwner(userId)
                .stream()
                .map(item -> ItemMapper.mapToDtoOwner(item, bookingService, service))
                .toList();
        log.info("Запрос на получение вещей пользователя по ID выполнен {}", itemOwnerDtoList);
        return itemOwnerDtoList;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam("text") String text) {
        log.info("GET Запрос на поиск вещей по строке {}", text);
        List<ItemDto> itemDtoList = service.searchItems(text)
                .stream().map(ItemMapper::mapToDto)
                .toList();
        log.info("Запрос на поиск вещей по строке выполнен {}", itemDtoList);
        return itemDtoList;
    }

    @PostMapping("{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long authorId, @PathVariable("itemId") Long itemId,
                                 @RequestBody CommentDto dto) {
        log.info("POST Запрос на добавление комментария автором с ID {} вещи с ID {} \n {}", authorId, itemId, dto);
        CommentDto savedCommnetDto = CommentMapper.mapToCommentDto(service.addComment(authorId, itemId, dto));
        log.info("Комментарий был добавлен {}", savedCommnetDto);
        return savedCommnetDto;
    }
}
