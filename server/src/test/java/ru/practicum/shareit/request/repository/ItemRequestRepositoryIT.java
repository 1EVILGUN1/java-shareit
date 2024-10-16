package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class ItemRequestRepositoryIT {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private User otherUser;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;

    @BeforeEach
    void setUp() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
        user = userRepository.save(new User(1L, "User", "user@example.com"));
        otherUser = userRepository.save(new User(2L, "Other User", "other@example.com"));

        itemRequest1 = itemRequestRepository.save(new ItemRequest(null, "Request 1", user,
                LocalDateTime.now()));
        itemRequest2 = itemRequestRepository.save(new ItemRequest(null, "Request 2", otherUser,
                LocalDateTime.now()));
    }

    @Test
    void findByRequestorIdOrderByCreatedDesc_whenRequestsExist_thenRequestsReturned() {
        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(user.getId());

        assertEquals(1, requests.size());
        assertEquals(itemRequest1.getDescription(), requests.get(0).getDescription());
    }

    @Test
    void findAllExcludedByUserIdDesc_whenRequestsExist_thenRequestsReturned() {
        List<ItemRequest> requests = itemRequestRepository.findAllExcludedByUserIdDesc(user.getId());

        assertEquals(1, requests.size());
        assertEquals(itemRequest2.getDescription(), requests.get(0).getDescription());
    }

    @Test
    void findAllExcludedByUserIdDesc_whenNoRequestsExistForUser_thenEmptyListReturned() {
        List<ItemRequest> requests = itemRequestRepository.findAllExcludedByUserIdDesc(otherUser.getId());

        assertEquals(1, requests.size());
        assertEquals(itemRequest1.getDescription(), requests.get(0).getDescription());
    }
}
