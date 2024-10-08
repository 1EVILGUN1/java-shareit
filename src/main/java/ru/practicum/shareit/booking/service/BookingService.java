package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface BookingService {

    Booking addBooking(BookingAddDto bookingDto, Long userId);

    BookingDto changeStatus(Long id, Boolean approved, Long userId);

    BookingDto findSpecificBooking(Long id, Long userId);

    List<BookingDto> findAllBookingsOfBooker(Long userId, State state);

    List<BookingDto> findAllBookingsOfOwner(Long userId, State state);

    Booking findLastBooking(Item item);

    Booking findFutureBooking(Item item);
}
