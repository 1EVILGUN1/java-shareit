package ru.practicum.shareit.booking.service;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemDoNotBelongToUser;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private User owner;
    private Item item;
    private BookingAddDto bookingAddDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "User", "user@example.com");
        owner = new User(2L, "Owner", "owner@email.com");
        item = new Item(1L, "Item", "Description", true, owner, null);
        bookingAddDto = new BookingAddDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                item.getId(), user, Status.WAITING, State.WAITING);
    }

    @Test
    void addBooking_whenItemIsAvailable_thenBookingIsSaved() {
        when(userService.findById(user.getId())).thenReturn(user);
        when(itemService.getItem(item.getId())).thenReturn(item);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking actualBooking = bookingService.addBooking(bookingAddDto, user.getId());

        assertEquals(Status.WAITING, actualBooking.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void addBooking_whenItemIsNotAvailable_thenNotAvailableExceptionThrown() {
        item.setAvailable(false);
        when(userService.findById(user.getId())).thenReturn(user);
        when(itemService.getItem(item.getId())).thenReturn(item);

        assertThrows(NotAvailableException.class, () -> bookingService.addBooking(bookingAddDto, user.getId()));
    }

    @Test
    void changeStatus_whenUserIsOwner_thenStatusIsChanged() {
        Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, owner,
                Status.WAITING);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking updatedBooking = bookingService.changeStatus(1L, true, owner.getId());

        assertEquals(Status.APPROVED, updatedBooking.getStatus());
        verify(bookingRepository, times(1)).save(updatedBooking);
    }

    @Test
    void changeStatus_whenUserIsNotOwner_thenItemDoNotBelongToUserExceptionThrown() {
        User anotherUser = new User(2L, "Another User", "another@example.com");
        Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, user,
                Status.WAITING);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ItemDoNotBelongToUser.class, () -> bookingService
                .changeStatus(1L, true, user.getId()));
    }

    @Test
    void findSpecificBooking_whenUserIsBookerOrOwner_thenBookingIsReturned() {
        Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, user,
                Status.WAITING);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Booking actualBooking = bookingService.findSpecificBooking(1L, user.getId());

        assertEquals(booking, actualBooking);
    }

    @Test
    void findAllBookingsOfBooker_whenUserExists_thenListOfBookingsReturned() {
        when(userService.findById(user.getId())).thenReturn(user);
        Booking booking1 = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, user,
                Status.WAITING);
        Booking booking2 = new Booking(2L, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), item,
                user, Status.APPROVED);
        when(bookingRepository.findAllBookingsByBooker_idOrderByStartDesc(user.getId()))
                .thenReturn(List.of(booking1, booking2));

        List<Booking> bookings = bookingService.findAllBookingsOfBooker(user.getId(), State.ALL);

        assertEquals(2, bookings.size());
        assertEquals(booking1, bookings.get(0));
        assertEquals(booking2, bookings.get(1));
    }

    @Test
    void findAllBookingsOfBooker_whenNoBookingsExist_thenEmptyListReturned() {
        when(userService.findById(user.getId())).thenReturn(user);
        when(bookingRepository.findAllBookingsByBooker_idOrderByStartDesc(user.getId()))
                .thenReturn(Collections.emptyList());

        List<Booking> actualBookings = bookingService
                .findAllBookingsOfBooker(user.getId(), State.ALL);

        assertTrue(actualBookings.isEmpty());
    }

    @Test
    void findAllBookingsOfOwner_whenUserExists_thenListOfBookingsReturned() {
        Booking booking1 = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, user,
                Status.WAITING);
        Booking booking2 = new Booking(2L, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), item,
                user, Status.APPROVED);
        when(bookingRepository.findAllByItem_Owner_idOrderByStartDesc(user.getId()))
                .thenReturn(List.of(booking1, booking2));

        List<Booking> bookings = bookingService.findAllBookingsOfOwner(user.getId(), State.ALL);

        assertEquals(2, bookings.size());
        assertEquals(booking1, bookings.get(0));
        assertEquals(booking2, bookings.get(1));
    }

    @Test
    void findAllBookingsOfOwner_whenNoBookingsExist_thenItemDoNotBelongToUserExceptionThrown() {
        when(bookingRepository.findAllByItem_Owner_idOrderByStartDesc(user.getId())).thenReturn(Collections.emptyList());

        assertThrows(ItemDoNotBelongToUser.class, () -> bookingService.findAllBookingsOfOwner(user.getId(), State.ALL));
    }

    @SneakyThrows
    @Test
    void findLastBooking_whenBookingExists_thenLastBookingReturned() {
        when(itemService.getItem(item.getId())).thenReturn(item);
        Booking lastBooking = new Booking(1L, LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2), item, owner, Status.WAITING);
        when(bookingRepository.findFirstByItem_Owner_idAndStartBeforeOrderByStartDesc(anyLong(),
                any(LocalDateTime.class)))
                .thenReturn(Optional.of(lastBooking));

        Booking foundBooking = bookingService.findLastBooking(item);

        assertEquals(lastBooking, foundBooking);
    }

    @Test
    void findLastBooking_whenNoBookingExists_thenNullReturned() {
        when(itemService.getItem(item.getId())).thenReturn(item);
        when(bookingRepository.findFirstByItem_Owner_idAndStartBeforeOrderByStartDesc(anyLong(),
                any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        Booking foundBooking = bookingService.findLastBooking(item);

        assertNull(foundBooking);
    }

    @Test
    void findFutureBooking_whenFutureBookingExists_thenBookingReturned() {
        when(itemService.getItem(item.getId())).thenReturn(item);
        Booking futureBooking = new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, owner, Status.WAITING);

        when(bookingRepository.findFirstByItem_Owner_idAndStartAfterOrderByStartAsc(anyLong(),
                any(LocalDateTime.class)))
                .thenReturn(Optional.of(futureBooking));

        Booking foundBooking = bookingService.findFutureBooking(item);

        assertEquals(futureBooking, foundBooking);
    }

    @Test
    void findFutureBooking_whenNoFutureBookingExists_thenNullReturned() {
        when(itemService.getItem(item.getId())).thenReturn(item);
        when(bookingRepository.findFirstByItem_Owner_idAndStartAfterOrderByStartAsc(anyLong(),
                any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        Booking foundBooking = bookingService.findFutureBooking(item);

        assertNull(foundBooking);
    }

    @Test
    void findAllBookingsOfBooker_whenStateIsAll_thenAllBookingsReturned() {
        when(userService.findById(user.getId())).thenReturn(user);
        Booking booking1 = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, user, Status.WAITING);
        Booking booking2 = new Booking(2L, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), item, user, Status.APPROVED);
        when(bookingRepository.findAllBookingsByBooker_idOrderByStartDesc(user.getId())).thenReturn(List.of(booking1, booking2));

        List<Booking> bookings = bookingService.findAllBookingsOfBooker(user.getId(), State.ALL);

        assertEquals(2, bookings.size());
        assertEquals(booking1, bookings.get(0));
        assertEquals(booking2, bookings.get(1));
    }

    @Test
    void findAllBookingsOfBooker_whenStateIsCurrent_thenCurrentBookingsReturned() {
        when(userService.findById(user.getId())).thenReturn(user);
        Booking currentBooking = new Booking(1L, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1), item, user, Status.WAITING);
        when(bookingRepository.findAllByBooker_idAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(List.of(currentBooking));

        List<Booking> bookings = bookingService.findAllBookingsOfBooker(user.getId(), State.CURRENT);

        assertEquals(1, bookings.size());
        assertEquals(currentBooking, bookings.get(0));
    }

    @Test
    void findAllBookingsOfBooker_whenStateIsPast_thenPastBookingsReturned() {
        when(userService.findById(user.getId())).thenReturn(user);
        Booking pastBooking = new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item, user, Status.APPROVED);
        when(bookingRepository.findAllByBooker_idAndEndAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class))).thenReturn(List.of(pastBooking));

        List<Booking> bookings = bookingService.findAllBookingsOfBooker(user.getId(), State.PAST);

        assertEquals(1, bookings.size());
        assertEquals(pastBooking, bookings.get(0));
    }

    @Test
    void findAllBookingsOfBooker_whenStateIsFuture_thenFutureBookingsReturned() {
        when(userService.findById(user.getId())).thenReturn(user);
        Booking futureBooking = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, user, Status.WAITING);
        when(bookingRepository.findAllByBooker_idAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class))).thenReturn(List.of(futureBooking));

        List<Booking> bookings = bookingService.findAllBookingsOfBooker(user.getId(), State.FUTURE);

        assertEquals(1, bookings.size());
        assertEquals(futureBooking, bookings.get(0));
    }

    @Test
    void findAllBookingsOfBooker_whenStateIsWaiting_thenWaitingBookingsReturned() {
        when(userService.findById(user.getId())).thenReturn(user);
        Booking waitingBooking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, user, Status.WAITING);
        when(bookingRepository.findAllByBooker_idAndStatusOrderByStartDesc(user.getId(), Status.WAITING)).thenReturn(List.of(waitingBooking));

        List<Booking> bookings = bookingService.findAllBookingsOfBooker(user.getId(), State.WAITING);

        assertEquals(1, bookings.size());
        assertEquals(waitingBooking, bookings.get(0));
    }

    @Test
    void findAllBookingsOfBooker_whenStateIsRejected_thenRejectedBookingsReturned() {
        when(userService.findById(user.getId())).thenReturn(user);
        Booking rejectedBooking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, user, Status.REJECTED);
        when(bookingRepository.findAllByBooker_idAndStatusOrderByStartDesc(user.getId(), Status.REJECTED)).thenReturn(List.of(rejectedBooking));

        List<Booking> bookings = bookingService.findAllBookingsOfBooker(user.getId(), State.REJECTED);

        assertEquals(1, bookings.size());
        assertEquals(rejectedBooking, bookings.get(0));
    }


    @Test
    void findAllBookingsOfOwner_whenStateIsAll_thenAllBookingsReturned() {
        Booking booking1 = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, user, Status.WAITING);
        Booking booking2 = new Booking(2L, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), item, user, Status.APPROVED);
        when(bookingRepository.findAllByItem_Owner_idOrderByStartDesc(owner.getId())).thenReturn(List.of(booking1, booking2));

        List<Booking> bookings = bookingService.findAllBookingsOfOwner(owner.getId(), State.ALL);

        assertEquals(2, bookings.size());
        assertEquals(booking1, bookings.get(0));
        assertEquals(booking2, bookings.get(1));
    }

    @Test
    void findAllBookingsOfOwner_whenStateIsCurrent_thenCurrentBookingsReturned() {
        Booking currentBooking = new Booking(1L, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1), item, user, Status.WAITING);
        when(bookingRepository.findAllByItem_Owner_idAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(List.of(currentBooking));

        List<Booking> bookings = bookingService.findAllBookingsOfOwner(owner.getId(), State.CURRENT);

        assertEquals(1, bookings.size());
        assertEquals(currentBooking, bookings.get(0));
    }

    @Test
    void findAllBookingsOfOwner_whenStateIsPast_thenPastBookingsReturned() {
        Booking pastBooking = new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item, user, Status.APPROVED);
        when(bookingRepository.findAllByItem_Owner_idAndEndAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class))).thenReturn(List.of(pastBooking));

        List<Booking> bookings = bookingService.findAllBookingsOfOwner(owner.getId(), State.PAST);

        assertEquals(1, bookings.size());
        assertEquals(pastBooking, bookings.get(0));
    }

    @Test
    void findAllBookingsOfOwner_whenStateIsFuture_thenFutureBookingsReturned() {
        Booking futureBooking = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, user, Status.WAITING);
        when(bookingRepository.findAllByItem_Owner_idAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class))).thenReturn(List.of(futureBooking));

        List<Booking> bookings = bookingService.findAllBookingsOfOwner(owner.getId(), State.FUTURE);

        assertEquals(1, bookings.size());
        assertEquals(futureBooking, bookings.get(0));
    }

    @Test
    void findAllBookingsOfOwner_whenStateIsWaiting_thenWaitingBookingsReturned() {
        Booking waitingBooking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, user, Status.WAITING);
        when(bookingRepository.findAllByItem_Owner_idAndStatusOrderByStartDesc(owner.getId(), Status.WAITING)).thenReturn(List.of(waitingBooking));

        List<Booking> bookings = bookingService.findAllBookingsOfOwner(owner.getId(), State.WAITING);

        assertEquals(1, bookings.size());
        assertEquals(waitingBooking, bookings.get(0));
    }

    @Test
    void findAllBookingsOfOwner_whenStateIsRejected_thenRejectedBookingsReturned() {
        Booking rejectedBooking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, user, Status.REJECTED);
        when(bookingRepository.findAllByItem_Owner_idAndStatusOrderByStartDesc(owner.getId(), Status.REJECTED)).thenReturn(List.of(rejectedBooking));

        List<Booking> bookings = bookingService.findAllBookingsOfOwner(owner.getId(), State.REJECTED);

        assertEquals(1, bookings.size());
        assertEquals(rejectedBooking, bookings.get(0));
    }


    @Test
    void findAllBookingsOfOwner_whenStateIsCurrentAndNoBookingsExist_thenItemDoNotBelongToUserThrown() {
        when(bookingRepository.findAllByItem_Owner_idAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(Collections.emptyList());

        assertThrows(ItemDoNotBelongToUser.class, () -> bookingService.findAllBookingsOfOwner(owner.getId(), State.CURRENT));
    }

    @Test
    void findAllBookingsOfOwner_whenStateIsPastAndNoBookingsExist_thenItemDoNotBelongToUserThrown() {
        when(bookingRepository.findAllByItem_Owner_idAndEndAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class))).thenReturn(Collections.emptyList());

        assertThrows(ItemDoNotBelongToUser.class, () -> bookingService.findAllBookingsOfOwner(owner.getId(), State.PAST));
    }

    @Test
    void findAllBookingsOfOwner_whenStateIsFutureAndNoBookingsExist_thenItemDoNotBelongToUserThrown() {
        when(bookingRepository.findAllByItem_Owner_idAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class))).thenReturn(Collections.emptyList());

        assertThrows(ItemDoNotBelongToUser.class, () -> bookingService.findAllBookingsOfOwner(owner.getId(), State.FUTURE));
    }

    @Test
    void findAllBookingsOfOwner_whenStateIsWaitingAndNoBookingsExist_thenEmptyListReturned() {
        when(bookingRepository.findAllByItem_Owner_idAndStatusOrderByStartDesc(owner.getId(), Status.WAITING)).thenReturn(Collections.emptyList());

        assertThrows(ItemDoNotBelongToUser.class, () -> bookingService.findAllBookingsOfOwner(owner.getId(), State.WAITING));
    }

    @Test
    void findAllBookingsOfOwner_whenStateIsRejectedAndNoBookingsExist_thenEmptyListReturned() {
        when(bookingRepository.findAllByItem_Owner_idAndStatusOrderByStartDesc(owner.getId(), Status.REJECTED)).thenReturn(Collections.emptyList());

        assertThrows(ItemDoNotBelongToUser.class, () -> bookingService.findAllBookingsOfOwner(owner.getId(), State.REJECTED));
    }
}
