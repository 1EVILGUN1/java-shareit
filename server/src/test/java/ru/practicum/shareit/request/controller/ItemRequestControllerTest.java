package ru.practicum.shareit.request.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    ItemRequestService itemRequestService;

    @Mock
    ItemService itemService;

    @InjectMocks
    ItemRequestController itemRequestController;

    @Test
    void add_whenCalled_thenItemRequestReturned() {
        Long requestorId = 1L;
        NewItemRequestDto newItemRequestDto = new NewItemRequestDto();
        ItemRequest itemRequest = new ItemRequest();
        when(itemRequestService.addNewRequest(requestorId, ItemRequestMapper.mapToItemRequest(newItemRequestDto)))
                .thenReturn(itemRequest);

        ItemRequest actualRequest = itemRequestController.add(requestorId, newItemRequestDto);

        assertEquals(itemRequest, actualRequest);
        verify(itemRequestService, times(1)).addNewRequest(requestorId,
                ItemRequestMapper.mapToItemRequest(newItemRequestDto));
    }

    @Test
    void getRequestsOfRequestor_whenCalled_thenListOfRequestsReturned() {
        Long requestorId = 1L;
        List<ItemRequest> itemRequests = List.of(new ItemRequest());
        when(itemRequestService.getRequestsOfRequestor(requestorId)).thenReturn(itemRequests);

        List<ItemRequestDto> actualRequests = itemRequestController.getRequestsOfRequestor(requestorId);

        assertEquals(itemRequests.size(), actualRequests.size());
        verify(itemRequestService, times(1)).getRequestsOfRequestor(requestorId);
    }

    @Test
    void getAllRequestsOfOtherUsers_whenCalled_thenListOfRequestsReturned() {
        Long userId = 1L;
        List<ItemRequest> itemRequests = List.of(new ItemRequest());
        when(itemRequestService.getAllRequestsOfOtherUsers(userId)).thenReturn(itemRequests);

        List<ItemRequestDto> actualRequests = itemRequestController.getAllRequestsOfOtherUsers(userId);

        assertEquals(itemRequests.size(), actualRequests.size());
        verify(itemRequestService, times(1)).getAllRequestsOfOtherUsers(userId);
    }

    @Test
    void getRequestById_whenRequestExists_thenRequestReturned() {
        Long userId = 1L;
        Long requestId = 1L;
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        ItemRequestDto expectedRequestDto = new ItemRequestDto();
        expectedRequestDto.setId(requestId);

        when(itemRequestService.getRequestById(userId, requestId)).thenReturn(itemRequest);
        when(itemService.getItemsByRequestId(requestId)).thenReturn(Collections.emptyList());

        ItemRequestDto actualRequestDto = itemRequestController.getRequestById(userId, requestId);

        assertEquals(expectedRequestDto.getId(), actualRequestDto.getId());
        verify(itemRequestService, times(1)).getRequestById(userId, requestId);
    }

    @Test
    void getRequestById_whenRequestNotFound_thenNotFoundExceptionThrown() {
        Long userId = 1L;
        Long requestId = 1L;
        when(itemRequestService.getRequestById(userId, requestId))
                .thenThrow(new NotFoundException("Request not found"));

        assertThrows(NotFoundException.class, () -> {
            itemRequestController.getRequestById(userId, requestId);
        });
    }
}
