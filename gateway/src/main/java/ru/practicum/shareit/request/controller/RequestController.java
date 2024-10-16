package ru.practicum.shareit.request.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.RequestClient;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

@Controller
@Slf4j
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@Positive @RequestHeader("X-Sharer-User-Id") long requestorId,
                                                 @Validated @RequestBody NewItemRequestDto dto) {
        log.info("POST Transfer request item user requestirId={}", requestorId);
        return requestClient.addItemRequest(requestorId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsOfRequestor(@Positive
                                                         @RequestHeader("X-Sharer-User-Id") long requestorId) {
        log.info("GET Request to search request user with requestirId={}", requestorId);
        return requestClient.getRequestsOfRequestor(requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestsOfOtherUsers(@Positive @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("GET Request to search stranger requests users with userId={}", userId);
        return requestClient.getAllRequestsOfOtherUsers(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                                                 @Positive @PathVariable("requestId") long requestId) {
        log.info("GET Request to search request with requestId={} user with userId={}", requestId, userId);
        return requestClient.getRequestById(userId, requestId);
    }
}
