package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {

    private final BookingService service;

    @PostMapping
    public Booking add(@RequestHeader("X-Sharer-User-Id") Long userId, @Validated @RequestBody BookingAddDto dto) {
        log.info("POST Запрос на добавление брони пользователем с ID: {} \n {}", userId, dto);
        Booking addedBooking = service.addBooking(dto, userId);
        log.info("Запрос на добавление брони выполнен: {}", addedBooking);
        return addedBooking;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto changeStatus(@PathVariable("bookingId") Long id,
                                   @RequestParam Boolean approved,
                                   @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("PATCH Запрос на изменение статус брони с ID: {} пользователем с ID {}", id, userId);
        BookingDto dto = service.changeStatus(id, approved, userId);
        log.info("Статус изменен: {}", dto);
        return dto;
    }

    @GetMapping("/{bookingId}")
    public BookingDto findSpecificBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable("bookingId") Long id) {
        log.info("GET Запрос на получение брони с ID : {} пользователя с ID {}", id, userId);
        BookingDto dto = service.findSpecificBooking(id, userId);
        log.info("Запрос на получение конкретной брони выполнен: {}", dto);
        return dto;
    }

    @GetMapping
    public List<BookingDto> findAllBookingsOfBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(defaultValue = "ALL") State state) {
        log.info("GET Запрос на получение всех броней пользователя с ID: {} со статусом {}", userId, state);
        List<BookingDto> bookingDtoList = service.findAllBookingsOfBooker(userId, state);
        log.info("Запрос на вывод броней пользователя с определенным статусом выполнен: {}", bookingDtoList);
        return bookingDtoList;
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllBookingsOfOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(defaultValue = "ALL") State state) {
        log.info("GET Запрос на получение всех броней владельца с ID: {} и статусом {}", userId, state);
        List<BookingDto> bookingDtoList = service.findAllBookingsOfOwner(userId, state);
        log.info("Запрос на получение всех броней владельца с определенным статус выполнен: {}", bookingDtoList);
        return bookingDtoList;
    }
}
