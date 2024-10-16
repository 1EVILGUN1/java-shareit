package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService service;

    @PostMapping
    public Booking add(@RequestHeader("X-Sharer-User-Id") Long userId, @Validated @RequestBody BookingAddDto dto) {
        Booking addedBooking = service.addBooking(dto, userId);
        log.info("Added booking: {}", addedBooking);
        return addedBooking;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto changeStatus(@PathVariable("bookingId") Long id,
                                   @RequestParam Boolean approved,
                                   @RequestHeader("X-Sharer-User-Id") Long userId) {
        BookingDto bookingDto = BookingMapper.mapToBookingDto(service.changeStatus(id, approved, userId));
        log.info("Change booking: {}", bookingDto);
        return bookingDto;
    }

    @GetMapping("/{bookingId}")
    public BookingDto findSpecificBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable("bookingId") Long id) {
        BookingDto bookingDto = BookingMapper.mapToBookingDto(service.findSpecificBooking(id, userId));
        log.info("Find booking: {}", bookingDto);
        return bookingDto;
    }

    @GetMapping
    public List<BookingDto> findAllBookingsOfBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(defaultValue = "ALL") State state) {
        List<BookingDto> bookingDtos = service
                .findAllBookingsOfBooker(userId, state)
                .stream()
                .map(BookingMapper::mapToBookingDto)
                .toList();
        log.info("Find all bookings of booker: {}", bookingDtos);
        return bookingDtos;
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllBookingsOfOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(defaultValue = "ALL") State state) {
        List<BookingDto> bookingDtos = service
                .findAllBookingsOfOwner(userId, state)
                .stream().map(BookingMapper::mapToBookingDto)
                .toList();
        log.info("Find all bookings of owner: {}", bookingDtos);
        return bookingDtos;
    }
}
