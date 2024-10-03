package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;


import java.util.Collection;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ResponseEntity<ItemDto> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @Valid @RequestBody ItemCreateDto item) {
        log.info("Получен запрос POST на добавление предмета пользователем с ID: {}", userId);
        ItemDto createdItem = service.save(userId, item);
        log.info("Предмет с ID: {} успешно добавлен пользователем с ID: {}, \n {}", createdItem.getId(), userId, createdItem);
        return new ResponseEntity<>(createdItem, HttpStatus.OK);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestBody ItemUpdateDto item,
                                          @PathVariable(value = "itemId") long itemId) {
        log.info("Получен запрос PATCH на обновление данных предмета пользователем с ID: {}", userId);
        ItemDto updatedItem = service.update(userId, itemId, item);
        log.info("Данные предмета с ID: {} успешно обновлены пользователем с ID: {}, \n {}", itemId, userId, updatedItem);
        return new ResponseEntity<>(updatedItem, HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @PathVariable(value = "itemId") long itemId) {
        log.info("Получен запрос GET на вывод предмета с ID: {} пользователя с ID: {}", itemId, userId);
        ItemDto itemDto = service.findById(itemId, userId);
        log.info("Вывод предмета с ID: {} пользователя с ID: {}, \n {}", itemDto.getId(), userId, itemDto);
        return new ResponseEntity<>(itemDto, HttpStatus.OK);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Collection<ItemDto>> getItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос GET на вывод всех предметов пользователя с ID: {}", userId);
        Collection<ItemDto> itemDtos = service.getItems(userId);
        log.info("Вывод всех предметов пользователя с ID: {} \n {}", userId, itemDtos);
        return new ResponseEntity<>(itemDtos, HttpStatus.OK);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<ItemDto> deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PathVariable(value = "itemId") long itemId) {
        log.info("Получен запрос DELETE на удаление предмета пользователя с ID: {} " +
                 "пользователя с ID: {}", itemId, userId);
        ItemDto deletedItem = service.delete(userId, itemId);
        log.info("Предмет c ID: {} пользователя с ID: {} успешно удален! \n {}", deletedItem.getId(), userId, deletedItem);
        return new ResponseEntity<>(deletedItem, HttpStatus.OK);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Collection<ItemDto>> searchItemByText(@RequestParam Optional<String> text,
                                                                @RequestHeader("X-Sharer-User-Id") Optional<Long> userId) {
        if (text.isPresent() && userId.isPresent()) {
            log.info("Получен запрос GET на получение предметов по результатам поиска: {} и ID пользователя {}", text, userId);
            Collection<ItemDto> itemDtos = service.searchItemByName(text.get(), userId.get());
            log.info("Вывод предметов вывод предметов связанных с {}, \n {}", text, itemDtos);
            return new ResponseEntity<>(itemDtos, HttpStatus.OK);
        }
        throw new IllegalArgumentException("Ошибка! Запрос на поиск вещи не выполнен");
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable(value = "itemId") long itemId,
                                                 @Valid @RequestBody CommentCreateDto commentCreateDto) {
        log.info("Получен запрос POST на добавление комментария вещи с ID: {} пользователем с ID: {}", itemId, userId);
        CommentDto savedComment = service.addComment(commentCreateDto, itemId, userId);
        log.info("Комментарий с ID: {} успешно добавлен!, \n {}", savedComment.getId(), savedComment);
        return new ResponseEntity<>(savedComment, HttpStatus.OK);
    }
}
