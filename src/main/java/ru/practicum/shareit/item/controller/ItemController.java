package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                          @Valid @RequestBody ItemDto item) {
        log.info("Получен запрос POST на добавление предмета пользователем с ID: {}", userId);
        ItemDto itemDto = itemService.save(userId, item);
        log.info("Предмет с ID: {} успешно добавлен пользователем с ID: {}", itemDto.getId(), userId);
        return itemDto;
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestBody ItemDto item,
                                          @PathVariable(value = "itemId") long itemId) {
        log.info("Получен запрос PATCH на обновление данных предмета пользователем с ID: {}", userId);
        ItemDto itemDto = itemService.update(userId, itemId, item);
        log.info("Данные предмета с ID: {} успешно обновлены пользователем с ID: {}", itemId, userId);
        return new ResponseEntity<>(itemDto, HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @PathVariable(value = "itemId") long itemId) {
        log.info("Получен запрос GET на вывод предмета с ID: {} пользователя с ID: {}", itemId, userId);
        ItemDto itemDto = itemService.findById(itemId, userId);
        log.info("Вывод предмета с ID: {} пользователя с ID: {}", itemDto.getId(), userId);
        return new ResponseEntity<>(itemDto, HttpStatus.OK);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос GET на вывод всех предметов пользователя с ID: {}", userId);
        List<ItemDto> items = itemService.getItems(userId);
        log.info("Вывод всех предметов пользователя с ID: {}, {}", userId, items);
        return items;
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<ItemDto> deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PathVariable(value = "itemId") long itemId) {
        log.info("Получен запрос DELETE на удаление предмета пользователя с ID: {} " +
                 "пользователя с ID: {}", itemId, userId);
        ItemDto itemDto = itemService.delete(userId, itemId);
        log.info("Предмет c ID: {} пользователя с ID: {} успешно удален!", itemDto.getId(), userId);
        return new ResponseEntity<>(itemDto, HttpStatus.OK);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemDto> searchItemByText(@RequestParam Optional<String> text,
                                                @RequestHeader("X-Sharer-User-Id") Optional<Long> userId) {
        if (text.isPresent() && userId.isPresent()) {
            log.info("Получен запрос GET на получение предметов по результатам поиска: {}", text);
            Collection<ItemDto> itemByText = itemService.searchItemByName(text.get(), userId.get());
            log.info("Вывод предметов по поиску {}", itemByText);
            return itemByText;
        }
        throw new IllegalArgumentException("Ошибка!");
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> saveComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @PathVariable(value = "itemId") long itemId,
                                                  @Valid @RequestBody CommentDto commentDto) {
        log.info("Получен запрос POST на добовление комментария вещи с ID: {} пользователем с ID: {}", itemId, userId);
        CommentDto saveCommentDto = itemService.saveComment(commentDto, itemId, userId);
        log.info("Комментарий с ID: {} успешно добавлен! \n {}", commentDto.getId(), saveCommentDto);
        return new ResponseEntity<>(saveCommentDto, HttpStatus.OK);
    }
}
