package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ItemRepositoryIT {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User testUser;
    private ItemRequest testRequest;
    private Item testItem1;
    private Item testItem2;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("testuser@example.com");
        userRepository.save(testUser);

        testRequest = new ItemRequest();
        testRequest.setDescription("Test Request");
        testRequest.setRequestor(testUser);
        testRequest.setCreated(LocalDateTime.now());
        itemRequestRepository.save(testRequest);

        testItem1 = new Item();
        testItem1.setName("Test Item 1");
        testItem1.setDescription("Description for Test Item 1");
        testItem1.setAvailable(true);
        testItem1.setOwner(testUser);
        testItem1.setRequest(testRequest);

        testItem2 = new Item();
        testItem2.setName("Test Item 2");
        testItem2.setDescription("Description for Test Item 2");
        testItem2.setAvailable(true);
        testItem2.setOwner(testUser);
        testItem2.setRequest(testRequest);

        itemRepository.save(testItem1);
        itemRepository.save(testItem2);
    }

    @Test
    public void whenFindByOwnerId_thenReturnItems() {
        List<Item> foundItems = itemRepository.findByOwnerId(testUser.getId());
        assertThat(foundItems).hasSize(2);
        assertThat(foundItems).contains(testItem1, testItem2);
    }

    @Test
    public void whenSearchByName_thenReturnItem() {
        List<Item> foundItems = itemRepository.search("Test Item 1");
        assertThat(foundItems).hasSize(1);
        assertThat(foundItems).contains(testItem1);
    }

    @Test
    public void whenSearchByDescription_thenReturnItem() {
        List<Item> foundItems = itemRepository.search("Description for Test Item 2");
        assertThat(foundItems).hasSize(1);
        assertThat(foundItems).contains(testItem2);
    }

    @Test
    public void whenSearchByPartialText_thenReturnAllItems() {
        List<Item> foundItems = itemRepository.search("Test Item");
        assertThat(foundItems).hasSize(2);
        assertThat(foundItems).contains(testItem1, testItem2);
    }

    @Test
    public void whenFindAllByRequestId_thenReturnItems() {
        List<Item> foundItems = itemRepository.findAllByRequestId(testRequest.getId());
        assertThat(foundItems).hasSize(2);
        assertThat(foundItems).contains(testItem1, testItem2);
    }
}
