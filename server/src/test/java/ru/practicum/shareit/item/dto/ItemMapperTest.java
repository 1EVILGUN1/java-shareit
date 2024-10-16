package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemMapperTest {

    @Mock
    private ItemService itemService;

    @Mock
    private BookingService bookingService;

    private Item item;

    @BeforeEach
    void setUp() {
        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
    }

    @Test
    void mapToDtoWithComments_whenItemIsProvided_thenItemDtoWithCommentsIsReturned() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setAuthor(new User(1L, "Author Name", "author@example.com"));
        comment.setText("This is a test comment.");
        comment.setCreated(LocalDateTime.now());

        when(itemService.getComments(item.getId())).thenReturn(List.of(comment));

        ItemDto itemDto = ItemMapper.mapToDtoWithComments(item, itemService);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.isAvailable());
        assertEquals(1, itemDto.getComments().size());
        assertEquals(comment.getText(), itemDto.getComments().get(0).getText());
        assertEquals(comment.getAuthor().getName(), itemDto.getComments().get(0).getAuthorName());
    }

    @Test
    void mapToDtoWithComments_whenItemHasNoComments_thenItemDtoWithEmptyCommentsIsReturned() {
        when(itemService.getComments(item.getId())).thenReturn(Collections.emptyList());

        ItemDto itemDto = ItemMapper.mapToDtoWithComments(item, itemService);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.isAvailable());
        assertEquals(0, itemDto.getComments().size());
    }

    @Test
    void mapToDto_whenItemIsProvided_thenItemDtoIsReturned() {
        ItemDto itemDto = ItemMapper.mapToDto(item);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.isAvailable());
    }

    @Test
    void mapToItem_whenItemDtoIsProvided_thenItemIsReturned() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        Item mappedItem = ItemMapper.mapToItem(itemDto);

        assertEquals(itemDto.getId(), mappedItem.getId());
        assertEquals(itemDto.getName(), mappedItem.getName());
        assertEquals(itemDto.getDescription(), mappedItem.getDescription());
        assertEquals(itemDto.isAvailable(), mappedItem.getAvailable());
    }

    @Test
    void mapToDtoOwner_whenItemIsProvidedWithBookingsAndComments_thenItemOwnerDtoIsReturned() {
        User booker = new User(1L, "name", "email@email.ru");

        Booking lastBooking = new Booking();
        lastBooking.setId(1L);
        lastBooking.setItem(item);
        lastBooking.setStart(LocalDateTime.now().minusDays(1));
        lastBooking.setEnd(LocalDateTime.now());
        lastBooking.setBooker(booker);

        Booking futureBooking = new Booking();
        futureBooking.setId(2L);
        futureBooking.setItem(item);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        futureBooking.setBooker(booker);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setAuthor(new User(1L, "Author Name", "author@example.com"));
        comment.setText("This is a test comment.");
        comment.setCreated(LocalDateTime.now());

        when(bookingService.findLastBooking(item)).thenReturn(lastBooking);
        when(bookingService.findFutureBooking(item)).thenReturn(futureBooking);
        when(itemService.getComments(item.getId())).thenReturn(List.of(comment));

        ItemOwnerDto itemOwnerDto = ItemMapper.mapToDtoOwner(item, bookingService, itemService);

        assertEquals(item.getName(), itemOwnerDto.getName());
        assertEquals(item.getDescription(), itemOwnerDto.getDescription());
        assertEquals(1, itemOwnerDto.getComments().size());
        assertEquals(comment.getText(), itemOwnerDto.getComments().get(0).getText());
        assertEquals(comment.getAuthor().getName(), itemOwnerDto.getComments().get(0).getAuthorName());
        assertEquals(1L, itemOwnerDto.getLastBooking().getId());
        assertEquals(2L, itemOwnerDto.getNextBooking().getId());
    }

    @Test
    void mapToDtoOwner_whenItemHasNoBookingsOrComments_thenItemOwnerDtoIsReturnedWithEmptyValues() {
        when(bookingService.findLastBooking(item)).thenReturn(null);
        when(bookingService.findFutureBooking(item)).thenReturn(null);
        when(itemService.getComments(item.getId())).thenReturn(Collections.emptyList());

        ItemOwnerDto itemOwnerDto = ItemMapper.mapToDtoOwner(item, bookingService, itemService);

        assertEquals(item.getName(), itemOwnerDto.getName());
        assertEquals(item.getDescription(), itemOwnerDto.getDescription());
        assertEquals(0, itemOwnerDto.getComments().size());
        assertNull(itemOwnerDto.getLastBooking());
        assertNull(itemOwnerDto.getNextBooking());
    }
}
