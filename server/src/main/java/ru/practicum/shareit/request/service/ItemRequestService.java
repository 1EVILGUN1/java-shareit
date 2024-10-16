package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequest addNewRequest(Long requestorId, ItemRequest itemRequest);

    List<ItemRequest> getRequestsOfRequestor(Long requestorId);

    List<ItemRequest> getAllRequestsOfOtherUsers(Long userId);

    ItemRequest getRequestById(Long userId, Long requestId);
}
