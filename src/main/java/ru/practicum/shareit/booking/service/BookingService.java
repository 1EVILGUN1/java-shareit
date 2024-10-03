package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingCreatedDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

public interface BookingService {
    BookingDto addBooking(BookingCreatedDto bookingCreatedDto, long userId);

    BookingDto approveBooking(long bookingId, boolean status, long userId);

    BookingDto findById(long bookingId, long userId);

    Collection<BookingDto> getUserBookings(String state, long bookerId);

    Collection<BookingDto> getAllBookingsByUserOwner(String state, long userId);

    Booking checkBookingDto(long bookingId);
}
