package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerIT {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookingClient bookingClient;

    @SneakyThrows
    @Test
    void bookItem_whenValidRequest_thenReturnStatusIsOk() {
        BookItemRequestDto requestDto = new BookItemRequestDto(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                1L
        );

        when(bookingClient.bookItem(any(Long.class), any(BookItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void bookItem_whenStartIsInThePast_thenReturnStatusIsBadRequest() {
        BookItemRequestDto requestDto = new BookItemRequestDto(
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                1L
        );

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void bookItem_whenEndIsBeforeStart_thenReturnStatusIsBadRequest() {
        BookItemRequestDto requestDto = new BookItemRequestDto(
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1),
                1L
        );

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void changeStatus_whenValidRequest_thenReturnStatusIsOk() {
        long bookingId = 1L;
        boolean approved = true;

        when(bookingClient.changeStatus(eq(bookingId), eq(approved), any(Long.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", String.valueOf(approved)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void getBooking_whenValidRequest_thenReturnStatusIsOk() {
        long bookingId = 1L;

        when(bookingClient.getBooking(any(Long.class), eq(bookingId)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void getBookings_whenValidRequest_thenReturnStatusIsOk() {
        when(bookingClient.getBookings(any(Long.class), any(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok(Collections.emptyList()));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "all")
                        .param("from", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void getBookings_whenUnknownState_thenReturnStatusIsInternalServerError() {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "unknown")
                        .param("from", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @SneakyThrows
    @Test
    void getBookingsOFOwner_whenValidRequest_thenReturnStatusIsOk() {
        when(bookingClient.getOwnerBookings(any(Long.class), any()))
                .thenReturn(ResponseEntity.ok(Collections.emptyList()));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "all"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void getBookingsOFOwner_whenUnknownState_thenReturnStatusIsInternalServerError() {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "unknown"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }
}
