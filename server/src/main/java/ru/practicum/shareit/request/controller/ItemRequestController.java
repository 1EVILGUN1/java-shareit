package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService service;
    private final ItemService itemService;

    @PostMapping
    public ItemRequest add(@RequestHeader("X-Sharer-User-Id") Long requestorId, @RequestBody NewItemRequestDto dto) {
        ItemRequest addedRequest = service.addNewRequest(requestorId, ItemRequestMapper.mapToItemRequest(dto));
        log.info("Added request: {}", addedRequest);
        return addedRequest;
    }

    @GetMapping
    public List<ItemRequestDto> getRequestsOfRequestor(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        List<ItemRequestDto> requests = service.getRequestsOfRequestor(requestorId).stream().map(itemRequest ->
                ItemRequestMapper.mapToItemRequestDto(itemRequest, itemService)).toList();
        log.info("get requests of user {}", requests);
        return requests;
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequestsOfOtherUsers(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemRequestDto> requests = service.getAllRequestsOfOtherUsers(userId).stream().map(itemRequest ->
                ItemRequestMapper.mapToItemRequestDto(itemRequest, itemService)).toList();
        log.info("get all requests of other user {}", requests);
        return requests;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable("requestId") Long requestId) {
        ItemRequestDto dto = ItemRequestMapper
                .mapToItemRequestDto(service.getRequestById(userId, requestId), itemService);
        log.info(dto.toString());
        log.info("get request by id {}", dto);
        return dto;
    }
}
