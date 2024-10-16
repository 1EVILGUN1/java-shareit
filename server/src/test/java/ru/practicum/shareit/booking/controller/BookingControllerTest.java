package ru.practicum.shareit.booking.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @Test
    void add_whenInvoked_thenBookingReturned() {
        Long userId = 1L;
        BookingAddDto bookingAddDto = new BookingAddDto();
        bookingAddDto.setStart(LocalDateTime.now().plusDays(1));
        bookingAddDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingAddDto.setItemId(1L);

        Booking expectedBooking = new Booking();
        expectedBooking.setId(1L);
        expectedBooking.setStart(bookingAddDto.getStart());
        expectedBooking.setEnd(bookingAddDto.getEnd());
        expectedBooking.setItem(new Item());
        expectedBooking.setBooker(new User());
        expectedBooking.setStatus(Status.WAITING);

        when(bookingService.addBooking(any(BookingAddDto.class), anyLong())).thenReturn(expectedBooking);

        Booking actualBooking = bookingController.add(userId, bookingAddDto);

        assertEquals(expectedBooking, actualBooking);
    }

    @Test
    void changeStatus_whenInvoked_thenBookingDtoReturned() {
        Long bookingId = 1L;
        Long userId = 1L;
        Boolean approved = true;

        Booking expectedBooking = new Booking();
        expectedBooking.setId(bookingId);
        expectedBooking.setStart(LocalDateTime.now().plusSeconds(1));
        expectedBooking.setEnd(LocalDateTime.now().plusSeconds(2));
        expectedBooking.setItem(new Item());
        expectedBooking.setBooker(new User());
        expectedBooking.setStatus(approved ? Status.APPROVED : Status.REJECTED);

        BookingDto expectedBookingDto = BookingMapper.mapToBookingDto(expectedBooking);

        when(bookingService.changeStatus(anyLong(), any(Boolean.class), anyLong())).thenReturn(expectedBooking);

        BookingDto actualBookingDto = bookingController.changeStatus(bookingId, approved, userId);

        assertEquals(expectedBookingDto, actualBookingDto);
    }

    @Test
    void findSpecificBooking_whenInvoked_thenBookingDtoReturned() {
        Long bookingId = 1L;
        Long userId = 1L;

        Booking expectedBooking = new Booking();
        expectedBooking.setId(bookingId);
        expectedBooking.setStart(LocalDateTime.now().plusDays(1));
        expectedBooking.setEnd(LocalDateTime.now().plusDays(2));
        expectedBooking.setItem(new Item());
        expectedBooking.setBooker(new User());
        expectedBooking.setStatus(Status.WAITING);

        BookingDto expectedBookingDto = BookingMapper.mapToBookingDto(expectedBooking);

        when(bookingService.findSpecificBooking(anyLong(), anyLong())).thenReturn(expectedBooking);

        BookingDto actualBookingDto = bookingController.findSpecificBooking(userId, bookingId);

        assertEquals(expectedBookingDto, actualBookingDto);
    }

    @Test
    void findAllBookingsOfBooker_whenInvoked_thenListOfBookingDtosReturned() {
        Long userId = 1L;
        State state = State.ALL;

        Booking expectedBooking = new Booking();
        expectedBooking.setId(1L);
        expectedBooking.setStart(LocalDateTime.now().plusDays(1));
        expectedBooking.setEnd(LocalDateTime.now().plusDays(2));
        expectedBooking.setItem(new Item());
        expectedBooking.setBooker(new User());
        expectedBooking.setStatus(Status.WAITING);

        BookingDto expectedBookingDto = BookingMapper.mapToBookingDto(expectedBooking);
        List<Booking> expectedBookings = Collections.singletonList(expectedBooking);
        List<BookingDto> expectedBookingDtos = Collections.singletonList(expectedBookingDto);

        when(bookingService.findAllBookingsOfBooker(anyLong(), any(State.class))).thenReturn(expectedBookings);

        List<BookingDto> actualBookingDtos = bookingController.findAllBookingsOfBooker(userId, state);

        assertEquals(expectedBookingDtos, actualBookingDtos);
    }

    @Test
    void findAllBookingsOfOwner_whenInvoked_thenListOfBookingDtosReturned() {
        Long userId = 1L;
        State state = State.ALL;

        Booking expectedBooking = new Booking();
        expectedBooking.setId(1L);
        expectedBooking.setStart(LocalDateTime.now().plusDays(1));
        expectedBooking.setEnd(LocalDateTime.now().plusDays(2));
        expectedBooking.setItem(new Item());
        expectedBooking.setBooker(new User());
        expectedBooking.setStatus(Status.WAITING);

        BookingDto expectedBookingDto = BookingMapper.mapToBookingDto(expectedBooking);
        List<Booking> expectedBookings = Collections.singletonList(expectedBooking);
        List<BookingDto> expectedBookingDtos = Collections.singletonList(expectedBookingDto);

        when(bookingService.findAllBookingsOfOwner(anyLong(), any(State.class))).thenReturn(expectedBookings);

        List<BookingDto> actualBookingDtos = bookingController.findAllBookingsOfOwner(userId, state);

        assertEquals(expectedBookingDtos, actualBookingDtos);
    }
}
