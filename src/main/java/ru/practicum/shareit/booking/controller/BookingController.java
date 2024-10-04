package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreatedDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import jakarta.validation.Valid;

import java.util.Collection;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService service;

    @PostMapping
    public ResponseEntity<BookingDto> addBooking(@Valid @RequestBody BookingCreatedDto bookingCreatedDto,
                                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос POST на добавление бронирования");
        BookingDto bookingDto = service.addBooking(bookingCreatedDto, userId);
        log.info("Бронь с ID: {} успешно добавлена! \n {}", bookingDto, bookingDto);
        return new ResponseEntity<>(bookingDto, HttpStatus.OK);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approveBooking(@PathVariable long bookingId,
                                                     @RequestParam(name = "approved") boolean status,
                                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос PATCH на изменение статуса бронирования");
        BookingDto bookingDto = service.approveBooking(bookingId, status, userId);
        log.info("Статус брони с ID {} успешно изменена! \n {}", bookingId, bookingDto);
        return new ResponseEntity<>(bookingDto, HttpStatus.OK);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable long bookingId,
                                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос GET на получение брони по ID: {}", bookingId);
        BookingDto bookingDto = service.getBookingById(bookingId, userId);
        log.info("Вывод брони с ID: {}", bookingDto);
        return new ResponseEntity<>(bookingDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Collection<BookingDto>> getAllBookingsByUser(
            @RequestParam(name = "state", defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") long bookerId) {
        log.info("Получен запрос GET на получение всех бронирований пользователя c ID: {}", bookerId);
        Collection<BookingDto> bookingDtos = service.getUserBookings(state, bookerId);
        log.info("Вывод всех бронирований {}", bookingDtos);
        return new ResponseEntity<>(bookingDtos, HttpStatus.OK);
    }

    @GetMapping("/owner")
    public ResponseEntity<Collection<BookingDto>> getBookingsByUser(
            @RequestParam(name = "state", defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос GET на получение всех бронирований вещей принадлежащих пользователю с ID: {}", userId);
        Collection<BookingDto> bookingDtos = service.getAllBookingsByUserOwner(state, userId);
        log.info("Вывод всех бронирований {}", bookingDtos);
        return new ResponseEntity<>(bookingDtos, HttpStatus.OK);
    }
}
