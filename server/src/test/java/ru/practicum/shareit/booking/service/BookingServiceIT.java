package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemDoNotBelongToUser;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
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
public class BookingServiceIT {

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    void addBooking_whenItemIsAvailable_thenBookingIsSaved() {

        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setName("User Name");
        user = userService.save(user);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setAvailable(true);
        itemDto.setName("Item Name");
        itemDto.setDescription("Item Description");
        Item item = itemService.save(user.getId(), itemDto);

        BookingAddDto bookingAddDto = new BookingAddDto();
        bookingAddDto.setStart(LocalDateTime.now().plusDays(1));
        bookingAddDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingAddDto.setItemId(item.getId());

        Booking booking = bookingService.addBooking(bookingAddDto, user.getId());

        assertEquals(bookingAddDto.getStart(), booking.getStart());
        assertEquals(bookingAddDto.getEnd(), booking.getEnd());
        assertEquals(item.getId(), booking.getItem().getId());
        assertEquals(user.getId(), booking.getBooker().getId());
    }

    @Test
    void addBooking_whenItemIsNotAvailable_thenNotAvailableExceptionThrown() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setName("User Name");
        User savedUser = userService.save(user);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setAvailable(false);
        itemDto.setName("Item Name");
        itemDto.setDescription("Item Description");
        Item item = itemService.save(savedUser.getId(), itemDto);

        BookingAddDto bookingAddDto = new BookingAddDto();
        bookingAddDto.setStart(LocalDateTime.now().plusDays(1));
        bookingAddDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingAddDto.setItemId(item.getId());

        assertThrows(NotAvailableException.class, () -> bookingService.addBooking(bookingAddDto, savedUser.getId()));
    }

    @Test
    void addBooking_whenUserDoesNotExist_thenNotFoundExceptionThrown() {
        BookingAddDto bookingAddDto = new BookingAddDto();
        bookingAddDto.setStart(LocalDateTime.now().plusDays(1));
        bookingAddDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingAddDto.setItemId(1L);

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(bookingAddDto, 1L));
    }

    @Test
    void changeStatus_whenBookingExists_thenStatusIsChanged() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setName("User Name");
        user = userService.save(user);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setAvailable(true);
        itemDto.setName("Item Name");
        itemDto.setDescription("Item Description");
        Item item = itemService.save(user.getId(), itemDto);

        BookingAddDto bookingAddDto = new BookingAddDto();
        bookingAddDto.setStart(LocalDateTime.now().plusDays(1));
        bookingAddDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingAddDto.setItemId(item.getId());

        Booking booking = bookingService.addBooking(bookingAddDto, user.getId());

        Booking updatedBooking = bookingService.changeStatus(booking.getId(), true, user.getId());

        assertEquals(Status.APPROVED, updatedBooking.getStatus());
    }

    @Test
    void changeStatus_whenBookingDoesNotExist_thenNotFoundExceptionThrown() {
        assertThrows(NotFoundException.class, () -> bookingService.changeStatus(999L, true, 1L));
    }

    @Test
    void changeStatus_whenUserIsNotOwner_thenItemDoNotBelongToUserExceptionThrown() {
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@example.com");
        user1.setName("User One");
        user1 = userService.save(user1);

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@example.com");
        user2.setName("User Two");
        User savedUser2 = userService.save(user2);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setAvailable(true);
        itemDto.setName("Item Name");
        itemDto.setDescription("Item Description");
        Item item = itemService.save(user1.getId(), itemDto);

        BookingAddDto bookingAddDto = new BookingAddDto();
        bookingAddDto.setStart(LocalDateTime.now().plusDays(1));
        bookingAddDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingAddDto.setItemId(item.getId());

        Booking booking = bookingService.addBooking(bookingAddDto, user1.getId());

        assertThrows(ItemDoNotBelongToUser.class, () ->
                bookingService.changeStatus(booking.getId(), true, savedUser2.getId()));
    }

    @Test
    void findSpecificBooking_whenBookingExistsAndUserIsOwner_thenBookingReturned() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setName("User Name");
        user = userService.save(user);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setAvailable(true);
        itemDto.setName("Item Name");
        itemDto.setDescription("Item Description");
        Item item = itemService.save(user.getId(), itemDto);

        BookingAddDto bookingAddDto = new BookingAddDto();
        bookingAddDto.setStart(LocalDateTime.now().plusDays(1));
        bookingAddDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingAddDto.setItemId(item.getId());

        Booking booking = bookingService.addBooking(bookingAddDto, user.getId());

        Booking foundBooking = bookingService.findSpecificBooking(booking.getId(), user.getId());

        assertEquals(booking.getId(), foundBooking.getId());
    }

    @Test
    void findSpecificBooking_whenBookingExistsAndUserIsBooker_thenBookingReturned() {
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@example.com");
        user1.setName("User One");
        userService.save(user1);

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@example.com");
        user2.setName("User Two");
        userService.save(user2);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setAvailable(true);
        itemDto.setName("Item Name");
        itemDto.setDescription("Item Description");
        itemService.save(user1.getId(), itemDto);

        BookingAddDto bookingAddDto = new BookingAddDto();
        bookingAddDto.setStart(LocalDateTime.now().plusDays(1));
        bookingAddDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingAddDto.setItemId(itemDto.getId());

        Booking booking = bookingService.addBooking(bookingAddDto, user2.getId());

        Booking foundBooking = bookingService.findSpecificBooking(booking.getId(), user2.getId());

        assertEquals(booking.getId(), foundBooking.getId());
    }

    @Test
    void findSpecificBooking_whenUserIsNeitherOwnerNorBooker_thenItemDoNotBelongToUserExceptionThrown() {
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

        User user3 = new User();
        user3.setId(3L);
        user3.setEmail("user3@example.com");
        user3.setName("User Three");
        User savedUser3 = userService.save(user3);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setAvailable(true);
        itemDto.setName("Item Name");
        itemDto.setDescription("Item Description");
        Item item = itemService.save(user1.getId(), itemDto);

        BookingAddDto bookingAddDto = new BookingAddDto();
        bookingAddDto.setStart(LocalDateTime.now().plusDays(1));
        bookingAddDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingAddDto.setItemId(item.getId());

        Booking booking = bookingService.addBooking(bookingAddDto, user2.getId());

        assertThrows(ItemDoNotBelongToUser.class, () ->
                bookingService.findSpecificBooking(booking.getId(), savedUser3.getId()));
    }

    @Test
    void findAllBookingsOfBooker_whenUserExists_thenBookingsReturned() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setName("User Name");
        user = userService.save(user);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setAvailable(true);
        itemDto.setName("Item Name");
        itemDto.setDescription("Item Description");
        Item item = itemService.save(user.getId(), itemDto);

        BookingAddDto bookingAddDto = new BookingAddDto();
        bookingAddDto.setStart(LocalDateTime.now().plusDays(1));
        bookingAddDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingAddDto.setItemId(item.getId());

        bookingService.addBooking(bookingAddDto, user.getId());

        List<Booking> bookings = bookingService.findAllBookingsOfBooker(user.getId(), State.ALL);

        assertEquals(1, bookings.size());
        assertEquals(item.getId(), bookings.get(0).getItem().getId());
        assertEquals(user.getId(), bookings.get(0).getBooker().getId());
    }

    @Test
    void findAllBookingsOfBooker_whenUserHasNoBookings_thenEmptyListReturned() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setName("User Name");
        user = userService.save(user);

        List<Booking> bookings = bookingService.findAllBookingsOfBooker(user.getId(), State.ALL);

        assertEquals(0, bookings.size());
    }

    @Test
    void findAllBookingsOfOwner_whenUserExists_thenBookingsReturned() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setName("User Name");
        user = userService.save(user);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setAvailable(true);
        itemDto.setName("Item Name");
        itemDto.setDescription("Item Description");
        Item item = itemService.save(user.getId(), itemDto);

        BookingAddDto bookingAddDto = new BookingAddDto();
        bookingAddDto.setStart(LocalDateTime.now().plusDays(1));
        bookingAddDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingAddDto.setItemId(item.getId());

        User booker = new User();
        booker.setId(2L);
        booker.setEmail("booker@example.com");
        booker.setName("Booker Name");
        booker = userService.save(booker);
        bookingService.addBooking(bookingAddDto, booker.getId());

        // when
        List<Booking> bookings = bookingService.findAllBookingsOfOwner(user.getId(), State.ALL);

        // then
        assertEquals(1, bookings.size());
        assertEquals(item.getId(), bookings.get(0).getItem().getId());
        assertEquals(booker.getId(), bookings.get(0).getBooker().getId());
    }

    @Test
    void findAllBookingsOfOwner_whenUserHasNoBookings_thenEmptyListReturned() {
        // given
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setName("User Name");
        userService.save(user);

        assertThrows(ItemDoNotBelongToUser.class, () -> bookingService.findAllBookingsOfOwner(user.getId(), State.ALL));
    }
}
