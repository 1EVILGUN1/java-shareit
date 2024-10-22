package ru.practicum.shareit.item.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemDoNotBelongToUser;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class ItemServiceIT {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        commentRepository.deleteAll();
        bookingRepository.deleteAll();
        itemRequestRepository.deleteAll();
        user = userService.save(new User(null, "User", "user@example.com"));
    }

    @Test
    void addNewItem_whenItemIsValid_itemIsSaved() {
        ItemDto itemDto = new ItemDto(null, "Test Item", "Test Description",
                true, null, null, null, null);
        Item item = itemService.save(user.getId(), itemDto);

        assertThat(item).isNotNull();
        assertThat(item.getId()).isNotNull();
        assertThat(item.getName()).isEqualTo("Test Item");
        assertThat(item.getDescription()).isEqualTo("Test Description");
        assertThat(item.getOwner().getId()).isEqualTo(user.getId());
    }

    @Test
    void updateItem_whenItemExists_itemIsUpdated() {
        ItemDto itemDto = new ItemDto(null, "Test Item", "Test Description", true,
                null, null, null, null);
        Item item = itemService.save(user.getId(), itemDto);

        ItemDto updatedItemDto = new ItemDto(null, "Updated Item", "Updated Description",
                false, null, null, null, null);
        Item updatedItem = itemService.update(user.getId(), item.getId(), updatedItemDto);

        assertThat(updatedItem.getName()).isEqualTo("Updated Item");
        assertThat(updatedItem.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedItem.getAvailable()).isFalse();
    }

    @Test
    void getItem_whenItemExists_itemIsReturned() {
        ItemDto itemDto = new ItemDto(null, "Test Item", "Test Description",
                true, null, null, null, null);
        Item item = itemService.save(user.getId(), itemDto);

        Item foundItem = itemService.getItem(item.getId());

        assertThat(foundItem).isNotNull();
        assertThat(foundItem.getId()).isEqualTo(item.getId());
    }

    @Test
    void getAllItemsOfOwner_whenOwnerHasItems_listOfItemsIsReturned() {
        ItemDto itemDto1 = new ItemDto(null, "Item 1", "Description 1",
                true, null, null, null, null);
        ItemDto itemDto2 = new ItemDto(null, "Item 2", "Description 2",
                true, null, null, null, null);
        itemService.save(user.getId(), itemDto1);
        itemService.save(user.getId(), itemDto2);

        List<Item> items = itemService.getAllItemsOfOwner(user.getId());

        assertThat(items).hasSize(2);
    }

    @SneakyThrows
    @Test
    void addComment_whenCommentIsValid_commentIsAdded() {
        ItemDto itemDto = new ItemDto(null, "Test Item", "Test Description",
                true, null, null, null, null);
        Item item = itemService.save(user.getId(), itemDto);

        User booker = userService.save(new User(null, "Booker", "booker@example.com"));

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().plusSeconds(1));
        booking.setEnd(LocalDateTime.now().plusSeconds(2));
        booking.setStatus(Status.WAITING);
        bookingRepository.save(booking);

        Thread.sleep(3000);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        Comment comment = itemService.addComment(booker.getId(), item.getId(), commentDto);

        assertThat(comment).isNotNull();
        assertThat(comment.getText()).isEqualTo("Great item!");
        assertThat(comment.getItem().getId()).isEqualTo(item.getId());
        assertThat(comment.getAuthor().getId()).isEqualTo(booker.getId());
    }

    @SneakyThrows
    @Test
    void getComments_whenItemHasComments_listOfCommentsIsReturned() {
        ItemDto itemDto = new ItemDto(null, "Test Item", "Test Description",
                true, null, null, null, null);
        Item item = itemService.save(user.getId(), itemDto);

        User booker = userService.save(new User(null, "Booker", "booker@example.com"));

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().plusSeconds(1));
        booking.setEnd(LocalDateTime.now().plusSeconds(2));
        booking.setStatus(Status.WAITING);
        bookingRepository.save(booking);

        Thread.sleep(3000);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");
        itemService.addComment(booker.getId(), item.getId(), commentDto);

        List<Comment> comments = itemService.getComments(item.getId());

        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getText()).isEqualTo("Great item!");
    }

    @Test
    void searchItems_whenItemsAreAvailable_listOfAvailableItemsIsReturned() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Request for items");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest = itemRequestRepository.save(itemRequest);

        ItemDto itemDto1 = new ItemDto(null, "Available Item", "Description",
                true, null, null, null, itemRequest.getId());
        ItemDto itemDto2 = new ItemDto(null, "Unavailable Item", "Description",
                false, null, null, null, itemRequest.getId());
        itemService.save(user.getId(), itemDto1);
        itemService.save(user.getId(), itemDto2);

        List<Item> foundItems = itemService.searchItems("Available");

        assertThat(foundItems).hasSize(1);
        assertThat(foundItems.get(0).getName()).isEqualTo("Available Item");
    }

    @Test
    void getItemsByRequestId_whenRequestHasItems_listOfItemsIsReturned() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Request for items");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest = itemRequestRepository.save(itemRequest);

        ItemDto itemDto1 = new ItemDto(null, "Item 1", "Description 1",
                true, null, null, null, itemRequest.getId());
        ItemDto itemDto2 = new ItemDto(null, "Item 2", "Description 2",
                true, null, null, null, itemRequest.getId());
        itemService.save(user.getId(), itemDto1);
        itemService.save(user.getId(), itemDto2);

        List<Item> items = itemService.getItemsByRequestId(itemRequest.getId());

        assertThat(items).hasSize(2);
        assertThat(items.get(0).getName()).isEqualTo("Item 1");
        assertThat(items.get(1).getName()).isEqualTo("Item 2");
    }

    @Test
    void addComment_whenItemNotFound_throwsNotFoundException() {
        User booker = new User(2L, "Booker", "booker@example.com");
        userService.save(booker);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        assertThrows(NotFoundException.class, () -> {
            itemService.addComment(booker.getId(), 999L, commentDto);
        });
    }

    @Test
    void addComment_whenUserNotFound_throwsNotFoundException() {
        ItemDto itemDto = new ItemDto(null, "Test Item", "Test Description",
                true, null, null, null, null);
        Item item = itemService.save(user.getId(), itemDto);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        assertThrows(NotFoundException.class, () -> {
            itemService.addComment(999L, item.getId(), commentDto);
        });
    }

    @Test
    void updateItem_whenItemNotFound_throwsNotFoundException() {
        ItemDto itemDto = new ItemDto(null, "Test Item", "Test Description",
                true, null, null, null, null);
        itemService.save(user.getId(), itemDto);

        ItemDto updatedItemDto = new ItemDto(null, "Updated Item", "Updated Description",
                false, null, null, null, null);

        assertThrows(NotFoundException.class, () -> {
            itemService.update(user.getId(), 999L, updatedItemDto);
        });
    }

    @Test
    void updateItem_whenUserNotOwner_throwsValidationException() {
        ItemDto itemDto = new ItemDto(null, "Test Item", "Test Description",
                true, null, null, null, null);
        Item item = itemService.save(user.getId(), itemDto);
        User anotherUser = userService.save(new User(null, "Another User", "another@example.com"));

        ItemDto updatedItemDto = new ItemDto(null, "Updated Item", "Updated Description",
                false, null, null, null, null);

        assertThrows(ItemDoNotBelongToUser.class, () -> {
            itemService.update(anotherUser.getId(), item.getId(), updatedItemDto);
        });
    }

    @Test
    void getItem_whenItemNotFound_throwsNotFoundException() {
        assertThrows(NotFoundException.class, () -> {
            itemService.getItem(999L);
        });
    }

    @Test
    void searchItems_whenSearchTermIsEmpty_returnsEmptyList() {
        List<Item> foundItems = itemService.searchItems("");

        assertThat(foundItems).isEmpty();
    }

    @Test
    void getItemsByRequestId_whenRequestNotFound_ReturnsEmptyList() {
        List<Item> items = itemService.getItemsByRequestId(999L);

        assertTrue(items.isEmpty(), "Ожидается пустой список, когда requestId не существует");
    }
}
