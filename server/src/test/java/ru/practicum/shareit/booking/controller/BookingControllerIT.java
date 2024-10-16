package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerIT {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookingService bookingService;

    @SneakyThrows
    @Test
    void addBooking_whenInvoked_BookingReturnedAndStatusIsOk() {
        BookingAddDto bookingAddDto = new BookingAddDto();
        bookingAddDto.setItemId(1L);
        bookingAddDto.setStart(LocalDateTime.now().plusDays(1));
        bookingAddDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingAddDto.setBooker(new User(1L, "bookerName", "bookerEmail@example.com"));

        Booking booking = new Booking(1L, bookingAddDto.getStart(), bookingAddDto.getEnd(),
                new Item(1L, "itemName", "itemDescription", true, null, null),
                bookingAddDto.getBooker(), Status.WAITING);

        when(bookingService.addBooking(any(), anyLong())).thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingAddDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.status").value(booking.getStatus().name()));
    }

    @SneakyThrows
    @Test
    void changeStatus_whenInvoked_BookingDtoReturnedAndStatusIsOk() {
        Long bookingId = 1L;
        Boolean approved = true;
        Booking booking = new Booking(bookingId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                new Item(1L, "itemName", "itemDescription", true, null, null),
                new User(1L, "bookerName", "bookerEmail@example.com"), Status.APPROVED);

        BookingDto bookingDto = BookingMapper.mapToBookingDto(booking);

        when(bookingService.changeStatus(bookingId, approved, 1L)).thenReturn(booking);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", approved.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().name()));
    }

    @SneakyThrows
    @Test
    void findSpecificBooking_whenBookingFound_thenReturnStatusIsOkAndBookingDto() {
        Long bookingId = 1L;
        Booking booking = new Booking(bookingId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                new Item(1L, "itemName", "itemDescription", true, null, null),
                new User(1L, "bookerName", "bookerEmail@example.com"), Status.WAITING);

        BookingDto bookingDto = BookingMapper.mapToBookingDto(booking);

        when(bookingService.findSpecificBooking(bookingId, 1L)).thenReturn(booking);

        String result = mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto), result);
    }

    @SneakyThrows
    @Test
    void findSpecificBooking_whenBookingNotFound_thenReturnStatusIsNotFoundAndNotFoundExceptionThrown() {
        Long bookingId = 1L;
        when(bookingService.findSpecificBooking(bookingId, 1L))
                .thenThrow(new NotFoundException("Бронирование с id = " + bookingId + " не найдено"));

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", 1L))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Ошибка с входными параметрами."))
                .andExpect(jsonPath("$.description")
                        .value("Бронирование с id = " + bookingId + " не найдено"));
    }

    @SneakyThrows
    @Test
    void findAllBookingsOfBooker_whenInvoked_ListOfBookingDtoReturnedAndStatusIsOk() {
        Long userId = 1L;
        Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                new Item(1L, "itemName", "itemDescription", true, null, null),
                new User(1L, "bookerName", "bookerEmail@example.com"), Status.WAITING);

        when(bookingService.findAllBookingsOfBooker(userId, State.ALL)).thenReturn(Collections.singletonList(booking));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(booking.getId()))
                .andExpect(jsonPath("$[0].status").value(booking.getStatus().name()));
    }

    @SneakyThrows
    @Test
    void findAllBookingsOfOwner_whenInvoked_ListOfBookingDtoReturnedAndStatusIsOk() {
        Long userId = 1L;
        Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                new Item(1L, "itemName", "itemDescription", true, null, null),
                new User(1L, "bookerName", "bookerEmail@example.com"), Status.WAITING);

        when(bookingService.findAllBookingsOfOwner(userId, State.ALL)).thenReturn(Collections.singletonList(booking));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(booking.getId()))
                .andExpect(jsonPath("$[0].status").value(booking.getStatus().name()));
    }
}
