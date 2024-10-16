package ru.practicum.shareit.item.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> save(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                       @Validated @RequestBody ItemDto dto) {
        log.info("POST Request save item user with userId={}", userId);
        return itemClient.save(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Positive @PathVariable("itemId") Long id,
                                         @RequestBody ItemDto dto) {
        log.info("PATCH Request update item user with userId={}", userId);
        return itemClient.update(userId, id, dto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                           @Positive @PathVariable("itemId") Long id) {
        log.info("GET Request to search item with itemId={} user with userId={}", id, userId);
        return itemClient.findById(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsOfOwner(@Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET Request update all items user with userId={}", userId);
        return itemClient.getItemsOfOwner(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam("text") String text) {
        log.info("GET Request to search items user with userId={}", userId);
        return itemClient.searchItems(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Positive @RequestHeader("X-Sharer-User-Id") Long authorId,
                                             @Positive @PathVariable("itemId") Long itemId,
                                             @Validated @RequestBody CommentDto dto) {
        log.info("GET Request add comment user with userId={} on item with Id={}", authorId, itemId);
        return itemClient.addComment(authorId, itemId, dto);
    }
}
