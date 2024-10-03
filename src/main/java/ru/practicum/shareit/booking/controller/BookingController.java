package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> saveBooking(@Valid @RequestBody BookingDto booking,
                                                  @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос POST на добавление бронирования");
        BookingDto savedBooking = bookingService.saveBooking(booking, userId);
        log.info("Бронь с ID: {} успешно добавлена!", savedBooking);
        return new ResponseEntity<>(savedBooking, HttpStatus.OK);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approveBooking(@PathVariable long bookingId,
                                                     @RequestParam(name = "approved") boolean status,
                                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос PATCH на изменение статуса бронирования");
        BookingDto approvedBooking = bookingService.approveBooking(bookingId, status, userId);
        log.info("Статус брони с ID {},\n {} \n успешно изменена!", bookingId, approvedBooking);
        return new ResponseEntity<>(approvedBooking, HttpStatus.OK);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> findBookingById(@PathVariable long bookingId,
                                                      @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос GET на получение брони по ID: {}", bookingId);
        BookingDto bookingDto = bookingService.findBookingById(bookingId, userId);
        log.info("Вывод брони с ID: {},\n {}", bookingId, bookingDto);
        return new ResponseEntity<>(bookingDto, HttpStatus.OK);
    }

    @GetMapping
    public Collection<BookingDto> allBookingsByUser(
            @RequestParam(name = "state", defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") long bookerId) {
        log.info("Получен запрос GET на получение всех бронирований пользователя c ID: {}", bookerId);
        Collection<BookingDto> bookingDtos = bookingService.userBookings(state, bookerId);
        log.info("Вывод всех бронирований {}", bookingDtos);
        return bookingDtos;
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getBookingsByUser(
            @RequestParam(name = "state", defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос GET на получение всех бронирований вещей принадлежащих пользователю с ID: {}", userId);
        Collection<BookingDto> bookingDtos = bookingService.allBookingsByUserOwner(state, userId);
        log.info("Вывод всех бронирований {}", bookingDtos);
        return bookingDtos;
    }
}

