package ru.practicum.shareit.booking.repostiory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class BookingRepositoryIT {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User user;
    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
        user = userRepository.save(new User(1L, "User", "user@example.com"));
        owner = userRepository.save(new User(2L, "Owner", "owner@example.com"));
        item = itemRepository.save(new Item(1L, "Item", "Description", true, owner,
                null));
    }

    @Test
    void findAllBookingsByBooker_idOrderByStartDesc_whenBookingsExist_thenBookingsReturned() {
        Booking booking1 = new Booking(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item,
                user, Status.WAITING);
        Booking booking2 = new Booking(null, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), item,
                user, Status.APPROVED);
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findAllBookingsByBooker_idOrderByStartDesc(user.getId());

        assertEquals(2, bookings.size());
        assertEquals(booking2, bookings.get(0));
        assertEquals(booking1, bookings.get(1));
    }

    @Test
    void findAllByBooker_idAndStartBeforeAndEndAfterOrderByStartDesc_whenBookingsExist_thenBookingsReturned() {
        Booking booking1 = new Booking(null, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), item,
                user, Status.WAITING);
        Booking booking2 = new Booking(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item,
                user, Status.APPROVED);
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findAllByBooker_idAndStartBeforeAndEndAfterOrderByStartDesc(
                user.getId(), LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1));

        assertEquals(1, bookings.size());
        assertEquals(booking1, bookings.get(0));
    }

    @Test
    void findAllByItem_Owner_idOrderByStartDesc_whenBookingsExist_thenBookingsReturned() {
        Booking booking1 = bookingRepository.save(new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, user, Status.WAITING));
        Booking booking2 = bookingRepository.save(new Booking(2L, LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(4), item, user, Status.APPROVED));

        List<Booking> bookings = bookingRepository.findAllByItem_Owner_idOrderByStartDesc(owner.getId());

        assertEquals(2, bookings.size());
        assertEquals(booking2, bookings.get(0));
        assertEquals(booking1, bookings.get(1));
    }

    @Test
    void findFirstByItem_Owner_idAndStartAfterOrderByStartAsc_whenFutureBookingExists_thenBookingReturned() {
        Booking futureBooking =
                bookingRepository.save(new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now()
                        .plusDays(2), item, user, Status.WAITING));

        Booking foundBooking = bookingRepository.findFirstByItem_Owner_idAndStartAfterOrderByStartAsc(owner.getId(),
                LocalDateTime.now()).orElse(null);

        assertEquals(futureBooking, foundBooking);
    }

    @Test
    void findFirstByItem_Owner_idAndStartAfterOrderByStartAsc_whenNoFutureBookingExists_thenNullReturned() {
        Booking foundBooking = bookingRepository.findFirstByItem_Owner_idAndStartAfterOrderByStartAsc(owner.getId(),
                LocalDateTime.now()).orElse(null);

        assertNull(foundBooking);
    }

    @Test
    void findByItemIdAndBookerIdAndEndBefore_whenBookingExists_thenBookingReturned() {
        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), item,
                user, Status.APPROVED);
        bookingRepository.save(booking);

        Booking foundBooking = bookingRepository.findByItemIdAndBookerIdAndEndBefore(item.getId(), user.getId(),
                LocalDateTime.now().plusDays(2)).orElse(null);

        assertEquals(booking, foundBooking);
    }

    @Test
    void findByItemIdAndBookerIdAndEndBefore_whenNoBookingExists_thenNullReturned() {
        Booking foundBooking = bookingRepository.findByItemIdAndBookerIdAndEndBefore(item.getId(), user.getId(),
                LocalDateTime.now()).orElse(null);

        assertNull(foundBooking);
    }
}
