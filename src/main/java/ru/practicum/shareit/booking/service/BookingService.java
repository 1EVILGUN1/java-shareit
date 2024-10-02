package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

public interface BookingService {
    BookingDto saveBooking(BookingDto booking, long userId);

    BookingDto approveBooking(long bookingId, boolean status, long userId);

    BookingDto findBookingById(long bookingId, long userId);

    Collection<BookingDto> userBookings(String state, long userId);

    Collection<BookingDto> allBookingsByUserOwner(String state, long userId);
}
