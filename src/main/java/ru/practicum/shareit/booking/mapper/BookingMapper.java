package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public final class BookingMapper {
    public static BookingDto mapToBookingDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setBooker(UserMapper.mapToUserDto(booking.getBooker()));
        dto.setItem(ItemMapper.mapToItemDto(booking.getItem()));
        dto.setStatus(booking.getStatus());
        return dto;
    }

    public static Booking mapToBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setBooker(UserMapper.mapToUser(bookingDto.getBooker()));
        booking.setItem(ItemMapper.mapToItem(bookingDto.getItem()));
        booking.setStatus(bookingDto.getStatus());
        return booking;
    }

    public static Collection<BookingDto> mapToBookingDtos(Collection<Booking> bookings) {
        return bookings.stream().map(BookingMapper::mapToBookingDto).collect(Collectors.toList());
    }
}
