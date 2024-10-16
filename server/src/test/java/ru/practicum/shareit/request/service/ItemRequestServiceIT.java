package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
public class ItemRequestServiceIT {

    @Autowired
    private ItemRequestServiceImpl itemRequestService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    void addNewRequest_whenUserExists_thenRequestIsSaved() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setName("User Name");
        user = userService.save(user);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Need a new item");
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequest savedRequest = itemRequestService.addNewRequest(user.getId(), itemRequest);

        assertEquals(itemRequest.getDescription(), savedRequest.getDescription());
        assertEquals(user.getId(), savedRequest.getRequestor().getId());
    }

    @Test
    void addNewRequest_whenUserDoesNotExist_thenNotFoundExceptionThrown() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Need a new item");
        itemRequest.setCreated(LocalDateTime.now());

        assertThrows(NotFoundException.class, () -> itemRequestService.addNewRequest(999L, itemRequest));
    }

    @Test
    void getRequestsOfRequestor_whenUserExists_thenRequestsReturned() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setName("User Name");
        user = userService.save(user);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Need a new item");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);

        itemRequestRepository.save(itemRequest);

        List<ItemRequest> requests = itemRequestService.getRequestsOfRequestor(user.getId());

        assertEquals(1, requests.size());
        assertEquals(itemRequest.getDescription(), requests.get(0).getDescription());
    }

    @Test
    void getRequestsOfRequestor_whenUserDoesNotExist_thenNotFoundExceptionThrown() {
        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestsOfRequestor(999L));
    }

    @Test
    void getAllRequestsOfOtherUsers_whenUserExists_thenRequestsReturned() {
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@example.com");
        user1.setName("User One");
        user1 = userService.save(user1);

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@example.com");
        user2.setName("User Two");
        user2 = userService.save(user2);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Need a new item");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user2);

        itemRequestRepository.save(itemRequest);

        List<ItemRequest> requests = itemRequestService.getAllRequestsOfOtherUsers(user1.getId());

        assertEquals(1, requests.size());
        assertEquals(itemRequest.getDescription(), requests.get(0).getDescription());
    }

    @Test
    void getAllRequestsOfOtherUsers_whenUserDoesNotExist_thenNotFoundExceptionThrown() {
        assertThrows(NotFoundException.class, () -> itemRequestService.getAllRequestsOfOtherUsers(999L));
    }

    @Test
    void getRequestById_whenRequestExists_thenRequestReturned() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setName("User Name");
        user = userService.save(user);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Need a new item");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);

        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);

        ItemRequest foundRequest = itemRequestService.getRequestById(user.getId(), savedRequest.getId());

        assertEquals(savedRequest.getId(), foundRequest.getId());
        assertEquals(savedRequest.getDescription(), foundRequest.getDescription());
    }

    @Test
    void getRequestById_whenRequestNotFound_thenNotFoundExceptionThrown() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setName("User Name");
        User savedUser = userService.save(user);

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(savedUser.getId(), 999L));
    }
}
