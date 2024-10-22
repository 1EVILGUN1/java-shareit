package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = new User(1L, "User", "user@example.com");
        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Request description");
    }

    @Test
    void addNewRequest_whenUserExists_thenRequestIsSaved() {
        when(userService.findById(user.getId())).thenReturn(user);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemRequest actualRequest = itemRequestService.addNewRequest(user.getId(), itemRequest);

        assertEquals(itemRequest, actualRequest);
        verify(itemRequestRepository, times(1)).save(itemRequest);
    }

    @Test
    void addNewRequest_whenUserNotFound_thenNotFoundExceptionThrown() {
        when(userService.findById(user.getId())).thenThrow(new NotFoundException("User not found"));

        assertThrows(NotFoundException.class, () -> itemRequestService.addNewRequest(user.getId(), itemRequest));
    }

    @Test
    void getRequestsOfRequestor_whenUserExists_thenListOfRequestsReturned() {
        when(userService.findById(user.getId())).thenReturn(user);
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(user.getId())).thenReturn(List.of(itemRequest));

        List<ItemRequest> actualRequests = itemRequestService.getRequestsOfRequestor(user.getId());

        assertEquals(1, actualRequests.size());
        assertEquals(itemRequest, actualRequests.get(0));
        verify(itemRequestRepository, times(1)).findByRequestorIdOrderByCreatedDesc(user.getId());
    }

    @Test
    void getRequestsOfRequestor_whenUserNotFound_thenNotFoundExceptionThrown() {
        when(userService.findById(user.getId())).thenThrow(new NotFoundException("User not found"));

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestsOfRequestor(user.getId()));
    }

    @Test
    void getAllRequestsOfOtherUsers_whenUserExists_thenListOfRequestsReturned() {
        when(userService.findById(user.getId())).thenReturn(user);
        when(itemRequestRepository.findAllExcludedByUserIdDesc(user.getId())).thenReturn(List.of(itemRequest));

        List<ItemRequest> actualRequests = itemRequestService.getAllRequestsOfOtherUsers(user.getId());

        assertEquals(1, actualRequests.size());
        assertEquals(itemRequest, actualRequests.get(0));
        verify(itemRequestRepository, times(1)).findAllExcludedByUserIdDesc(user.getId());
    }

    @Test
    void getAllRequestsOfOtherUsers_whenUserNotFound_thenNotFoundExceptionThrown() {
        when(userService.findById(user.getId())).thenThrow(new NotFoundException("User not found"));

        assertThrows(NotFoundException.class, () -> itemRequestService.getAllRequestsOfOtherUsers(user.getId()));
    }

    @Test
    void getRequestById_whenRequestExists_thenRequestReturned() {
        when(userService.findById(user.getId())).thenReturn(user);
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));

        ItemRequest actualRequest = itemRequestService.getRequestById(user.getId(), itemRequest.getId());

        assertEquals(itemRequest, actualRequest);
        verify(itemRequestRepository, times(1)).findById(itemRequest.getId());
    }

    @Test
    void getRequestById_whenRequestNotFound_thenNotFoundExceptionThrown() {
        when(userService.findById(user.getId())).thenReturn(user);
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemRequestService.getRequestById(user.getId(), itemRequest.getId()));
    }
}
